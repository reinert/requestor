/*
 * Copyright 2021-2022 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.java.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.HttpMethod;
import io.reinert.requestor.core.HttpStatus;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.ReadProgress;
import io.reinert.requestor.core.RequestAbortException;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestLogger;
import io.reinert.requestor.core.RequestOptions;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.ResponseHeader;
import io.reinert.requestor.core.ResponseProcessor;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.Store;
import io.reinert.requestor.core.WriteProgress;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.java.ChunkedProgressEvent;
import io.reinert.requestor.java.FixedProgressEvent;
import io.reinert.requestor.java.RequestRedirectException;
import io.reinert.requestor.java.payload.BinarySerializedPayload;
import io.reinert.requestor.java.payload.CompositeSerializedPayload;
import io.reinert.requestor.java.payload.InputStreamSerializedPayload;

import static io.reinert.requestor.java.net.Requestor.INPUT_BUFFER_SIZE;
import static io.reinert.requestor.java.net.Requestor.OUTPUT_BUFFER_SIZE;

/**
 * RequestDispatcher implementation using {@link HttpURLConnection}.
 *
 * @author Danilo Reinert
 */
class JavaNetRequestDispatcher extends RequestDispatcher {

    private final ScheduledExecutorService scheduledExecutorService;
    private final int inputBufferSize;
    private final int outputBufferSize;

    public JavaNetRequestDispatcher(RequestProcessor requestProcessor,
                                    ResponseProcessor responseProcessor,
                                    DeferredPool.Factory deferredPoolFactory,
                                    RequestLogger logger,
                                    ScheduledExecutorService scheduledExecutorService,
                                    int inputBufferSize, int outputBufferSize) {
        super(requestProcessor, responseProcessor, deferredPoolFactory, logger);
        this.scheduledExecutorService = scheduledExecutorService;
        this.inputBufferSize = inputBufferSize;
        this.outputBufferSize = outputBufferSize;
    }

    public void scheduleRun(final Runnable runnable, int delay) {
        scheduledExecutorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected <R> void send(PreparedRequest request, Deferred<R> deferred, PayloadType payloadType) {
        // Return if deferred were rejected or resolved before this method was called
        if (!deferred.isPending()) return;

        URL url = null;
        HttpURLConnection conn = null;
        JavaNetHttpConnection netConn = null;
        SerializedPayload serializedPayload = request.getSerializedPayload();

        try {
            // Set up connection
            url = new URL(request.getUri().toString());

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);

            if (!serializedPayload.isEmpty()) {
                conn.setDoOutput(true);
                if (serializedPayload.getLength() > 0) {
                    conn.setFixedLengthStreamingMode(serializedPayload.getLength());
                } else if (!request.exists(Requestor.CHUNKED_STREAMING_MODE_DISABLED, Boolean.TRUE)) {
                    conn.setChunkedStreamingMode(getOutputBufferSize(request));
                }
            }

            try {
                conn.setRequestMethod(request.getMethod().getValue());
            } catch (ProtocolException e) {
                if (!e.getMessage().contains(HttpMethod.PATCH.getValue())) throw e;
                setPatchMethod(conn);
            }

            for (Header header : request.getHeaders()) {
                conn.setRequestProperty(header.getName(), header.getValue());
            }

            if (!request.hasHeader("Content-Type") && request.exists(Requestor.DEFAULT_CONTENT_TYPE)) {
                conn.setRequestProperty("Content-Type", request.retrieve(Requestor.DEFAULT_CONTENT_TYPE));
            }

            if (request.getTimeout() > 0) {
                conn.setConnectTimeout(request.getTimeout());
                conn.setReadTimeout(request.getTimeout());
            }

            // TODO: handle cookies

            if (!deferred.isPending()) return;

            conn.connect();

            netConn = getNetConnection(conn, deferred, request);
            deferred.setHttpConnection(netConn);
        } catch (MalformedURLException e) {
            sleep(SLEEP_TIME_BEFORE_ABORTING);
            disconnect(conn, deferred, new RequestAbortException(request, "Invalid url format.", e));
            return;
        } catch (SocketTimeoutException e) {
            disconnect(conn, deferred, new RequestTimeoutException(request, request.getTimeout()));
            return;
        } catch (IOException e) {
            sleep(SLEEP_TIME_BEFORE_ABORTING);
            disconnect(conn, deferred, new RequestAbortException(request, "Failed to open connection.", e));
            return;
        }

        if (!url.getHost().equals(conn.getURL().getHost())) {
            // We were redirected!
            netConn.cancel(new RequestRedirectException(request, Uri.create(conn.getURL().toString())));
            return;
        }

        try {
            // Payload upload
            if (conn.getDoOutput()) {
                try (OutputStream out = conn.getOutputStream()) {
                    if (serializedPayload instanceof CompositeSerializedPayload) {
                        final CompositeSerializedPayload csp = (CompositeSerializedPayload) serializedPayload;
                        final long totalSize = csp.getLength();
                        long totalWritten = 0;
                        for (SerializedPayload part : csp) {
                            totalWritten = writeSerializedPayloadToOutputStream(request, deferred, out, part,
                                    totalWritten, totalSize);
                        }
                    } else {
                        writeSerializedPayloadToOutputStream(request, deferred, out, serializedPayload, 0,
                                serializedPayload.getLength());
                    }
                } catch (SocketTimeoutException e) {
                    netConn.cancel(new RequestTimeoutException(request, request.getTimeout()));
                    return;
                } catch (IOException e) {
                    netConn.cancel(new RequestCancelException(request, "Failed to write request payload.", e));
                    return;
                } catch (RuntimeException e) {
                    netConn.cancel(new RequestCancelException(request,
                            "An unexpected error has occurred while writing the request payload.", e));
                    return;
                }
            }

            if (!deferred.isPending()) {
                conn.disconnect();
                return;
            }

            // Response status
            HttpStatus responseStatus;
            try {
                responseStatus = Status.of(conn.getResponseCode());
            } catch (SocketTimeoutException e) {
                netConn.cancel(new RequestTimeoutException(request, request.getTimeout()));
                return;
            } catch (IOException e) {
                netConn.cancel(new RequestCancelException(request, "Failed to read response status.", e));
                return;
            }

            final RawResponse response = new RawResponse(
                    deferred,
                    responseStatus,
                    readResponseHeaders(conn),
                    payloadType
            );

            // Notify response headers received
            deferred.notifyResponse(response);

            // Payload download
            SerializedPayload serializedResponse = SerializedPayload.EMPTY_PAYLOAD;
            if (payloadType.getType() != Void.class ||
                    request.exists(Requestor.READ_CHUNKING_ENABLED, Boolean.TRUE)) {
                try (InputStream in = responseStatus.getFamily() == StatusFamily.SUCCESSFUL ?
                                conn.getInputStream() : conn.getErrorStream()) {
                    if (in != null) {
                        serializedResponse = readInputStreamToSerializedPayload(request, deferred, conn, in, response);
                    }
                } catch (SocketTimeoutException e) {
                    netConn.cancel(new RequestTimeoutException(request, request.getTimeout()));
                    return;
                } catch (IOException e) {
                    netConn.cancel(new RequestCancelException(request, "Failed to read response payload.", e));
                    return;
                } catch (RuntimeException e) {
                    netConn.cancel(new RequestCancelException(request,
                            "An unexpected error has occurred while reading the response payload.", e));
                    return;
                }
            }

            // Evaluate response
            response.setSerializedPayload(serializedResponse);

            conn.disconnect();

            evalResponse(response);
        } catch (RuntimeException e) {
            netConn.cancel(new RequestCancelException(request,
                    "An unexpected error has occurred while sending the request.", e));
        }
    }

    private <R> long writeSerializedPayloadToOutputStream(PreparedRequest request, Deferred<R> deferred,
                                                          OutputStream out, SerializedPayload serializedPayload,
                                                          long totalWritten, long totalSize) throws IOException {
        if (serializedPayload instanceof InputStreamSerializedPayload) {
            InputStreamSerializedPayload isp = (InputStreamSerializedPayload) serializedPayload;
            return writeInputToOutputStream(request, deferred, out, isp.getInputStream(), totalWritten, totalSize);
        }

        return writeBytesToOutputStream(request, deferred, out, serializedPayload.asBytes(), totalWritten, totalSize);
    }

    private <R> void disconnect(HttpURLConnection conn, Deferred<R> deferred, RequestException exception) {
        if (conn != null) conn.disconnect();
        if (deferred.isPending()) deferred.reject(exception);
    }

    private <R> long writeBytesToOutputStream(PreparedRequest request, Deferred<R> deferred, OutputStream out,
                                              byte[] bytes, long totalWritten, long totalSize) throws IOException {
        final boolean chunkingEnabled = request.exists(Requestor.WRITE_CHUNKING_ENABLED, Boolean.TRUE);
        final int bufferSize = getOutputBufferSize(request);
        for (int i = 0; i <= (bytes.length - 1) / bufferSize; i++) {
            int off = i * bufferSize;
            int len = Math.min(bufferSize, bytes.length - off);
            out.write(bytes, off, len);
            out.flush();

            totalWritten += len;

            byte[] chunk = chunkingEnabled ? Arrays.copyOfRange(bytes, off, off + len) : null;
            deferred.notifyUpload(new WriteProgress(request, totalSize > 0 ?
                    new FixedProgressEvent(totalWritten, totalSize) :
                    new ChunkedProgressEvent(totalWritten),
                    serializeContent(request.getContentType(), chunk, request.getCharset())));
        }

        return totalWritten;
    }

    private <R> long writeInputToOutputStream(PreparedRequest request, Deferred<R> deferred, OutputStream out,
                                              InputStream in, long totalWritten, long totalSize) throws IOException {
        final boolean chunkingEnabled = request.exists(Requestor.WRITE_CHUNKING_ENABLED, Boolean.TRUE);
        final int bufferSize = getOutputBufferSize(request);
        try (InputStream bis = new BufferedInputStream(in, bufferSize)) {
            byte[] buffer = new byte[bufferSize];
            int stepRead;
            while ((stepRead = bis.read(buffer)) != -1) {
                out.write(buffer, 0, stepRead);
                out.flush();

                totalWritten += stepRead;

                byte[] chunk = chunkingEnabled ? Arrays.copyOfRange(buffer, 0, stepRead) : null;
                deferred.notifyUpload(new WriteProgress(request, totalSize > 0 ?
                        new FixedProgressEvent(totalWritten, totalSize) :
                        new ChunkedProgressEvent(totalWritten),
                        serializeContent(request.getContentType(), chunk, request.getCharset())));
            }

            return totalWritten;
        }
    }

    private <R> SerializedPayload readInputStreamToSerializedPayload(PreparedRequest request, Deferred<R> deferred,
                                                                     HttpURLConnection conn, InputStream in,
                                                                     ResponseHeader response) throws IOException {
        final String contentType = conn.getContentType();
        final int contentLength = conn.getContentLength();

        final boolean payloadRequested = request.getResponsePayloadType().getType() != Void.class;
        final boolean chunkingEnabled = request.exists(Requestor.READ_CHUNKING_ENABLED, Boolean.TRUE);

        // NOTE: there should be no body when buffering is enabled but return type is void
        byte[] body = payloadRequested ? new byte[Math.max(contentLength, 0)] : null;

        byte[] buffer = new byte[getInputBufferSize(request)];
        int stepRead, totalRead = 0;
        while ((stepRead = in.read(buffer)) != -1) {
            if (payloadRequested) {
                if (contentLength > 0) {
                    System.arraycopy(buffer, 0, body, totalRead, stepRead);
                } else {
                    byte[] aux = new byte[totalRead + stepRead];
                    System.arraycopy(body, 0, aux, 0, totalRead);
                    System.arraycopy(buffer, 0, aux, totalRead, stepRead);
                    body = aux;
                }
            }

            totalRead += stepRead;

            byte[] chunk = chunkingEnabled ? Arrays.copyOf(buffer, stepRead) : null;
            deferred.notifyDownload(new ReadProgress(request, response, contentLength > 0 ?
                    new FixedProgressEvent(totalRead, contentLength) :
                    new ChunkedProgressEvent(totalRead),
                    serializeContent(contentType, chunk, request.getCharset())));
        }

        return serializeContent(contentType, body, request.getCharset());
    }

    private int getInputBufferSize(Store store) {
        final Integer customBufferSize = store.retrieve(INPUT_BUFFER_SIZE);
        return customBufferSize != null ? customBufferSize : inputBufferSize;
    }

    private int getOutputBufferSize(Store store) {
        final Integer customBufferSize = store.retrieve(OUTPUT_BUFFER_SIZE);
        return customBufferSize != null ? customBufferSize : outputBufferSize;
    }

    private Headers readResponseHeaders(HttpURLConnection conn) {
        List<Header> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                // TODO: process headers as elements
                headers.add(Header.fromRawHeader(header.getKey(), String.join(", ", header.getValue())));
            }
        }
        return new Headers(headers);
    }

    private SerializedPayload serializeContent(String mediaType, byte[] content, String charset) {
        if (content == null || content.length == 0) return SerializedPayload.EMPTY_PAYLOAD;
        return "application/octet-stream".equalsIgnoreCase(mediaType)
                ? new BinarySerializedPayload(content) : new TextSerializedPayload(content, charset);
    }

    private JavaNetHttpConnection getNetConnection(HttpURLConnection conn, Deferred<?> deferred, RequestOptions req) {
        return new JavaNetHttpConnection(conn, deferred, req);
    }

    private void setPatchMethod(HttpURLConnection conn) {
        try {
            // Try modifying the instance's method field value via reflection
            final Field methodField = HttpURLConnection.class.getDeclaredField("method");
            methodField.setAccessible(true);
            methodField.set(conn, HttpMethod.PATCH.getValue());
            methodField.setAccessible(false);

            try {
                // Set the delegate's method field value as well if it exists in the connection instance
                final Field delegateField = conn.getClass().getDeclaredField("delegate");
                delegateField.setAccessible(true);
                final HttpURLConnection delegate = (HttpURLConnection) delegateField.get(conn);
                methodField.setAccessible(true);
                methodField.set(delegate, HttpMethod.PATCH.getValue());
                methodField.setAccessible(false);
                delegateField.setAccessible(false);
            } catch (Exception ignored) { }

            if (!HttpMethod.PATCH.getValue().equals(conn.getRequestMethod())) {
                throw new IllegalStateException("PATCH is not set as the request method of the HttpURLConnection " +
                        "instance. The current method is: " + conn.getRequestMethod());
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot set PATCH as the connection request method. " +
                    "It's not supported for this runtime environment. " +
                    "If you're using jdk12+ then add the following command line arg to execute your java app: " +
                    "--add-opens java.base/java.net=ALL-UNNAMED. " +
                    "If you still keep getting errors, report it to https://github.com/reinert/requestor/issues.", e);
        }
    }
}
