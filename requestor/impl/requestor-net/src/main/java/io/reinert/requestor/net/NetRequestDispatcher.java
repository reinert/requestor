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
package io.reinert.requestor.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.HttpStatus;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.RequestAbortException;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestOptions;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.ResponseProcessor;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.uri.Uri;

/**
 * RequestDispatcher implementation using {@link HttpURLConnection}.
 *
 * @author Danilo Reinert
 */
class NetRequestDispatcher extends RequestDispatcher {

    private final ScheduledExecutorService scheduledExecutorService;
    private final int inputBufferSize;
    private final int outputBufferSize;

    public NetRequestDispatcher(RequestProcessor requestProcessor,
                                ResponseProcessor responseProcessor,
                                DeferredPool.Factory deferredPoolFactory,
                                ScheduledExecutorService scheduledExecutorService,
                                int inputBufferSize, int outputBufferSize) {
        super(requestProcessor, responseProcessor, deferredPoolFactory);
        this.scheduledExecutorService = scheduledExecutorService;
        this.inputBufferSize = inputBufferSize;
        this.outputBufferSize = outputBufferSize;
    }

    public void scheduleRun(final Runnable runnable, int delay) {
        scheduledExecutorService.schedule(runnable, Math.max(delay, 50), TimeUnit.MILLISECONDS);
    }

    protected <R> void send(PreparedRequest request, Deferred<R> deferred, PayloadType payloadType) {
        // Return if deferred were rejected or resolved before this method was called
        if (!deferred.isPending()) return;

        URL url = null;
        HttpURLConnection conn = null;
        NetHttpConnection netConn = null;
        SerializedPayload serializedPayload = request.getSerializedPayload();

        try {
            // Set up connection
            url = new URL(request.getUri().toString());

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);

            conn.setDoOutput(!serializedPayload.isEmpty());

            conn.setRequestMethod(request.getMethod().getValue());

            for (Header header : request.getHeaders()) {
                conn.setRequestProperty(header.getName(), header.getValue());
            }

            // TODO: expose a default content-type option
            if (!request.hasHeader("Content-Type")) conn.setRequestProperty("Content-Type", "text/plain");

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
            disconnect(conn, deferred, new RequestAbortException(request, "Invalid url format", e));
            return;
        } catch (SocketTimeoutException e) {
            disconnect(conn, deferred, new RequestTimeoutException(request, request.getTimeout()));
            return;
        } catch (IOException e) {
            disconnect(conn, deferred, new RequestAbortException(request, "Failed to open connection", e));
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
                // TODO: define the charcode
                try (Writer writer = new BufferedWriter(
                        new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8), outputBufferSize)) {
                    writer.write(serializedPayload.asString());
                    writer.flush();
                } catch (SocketTimeoutException e) {
                    netConn.cancel(new RequestTimeoutException(request, request.getTimeout()));
                    return;
                } catch (IOException e) {
                    netConn.cancel(new RequestCancelException(request, "Failed to write request payload", e));
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
                netConn.cancel(new RequestCancelException(request, "Failed to read response status", e));
                return;
            }

            // Payload download
            SerializedPayload serializedResponse;
            if (payloadType != null && payloadType.getType() != Void.class) {
                StringWriter body = new StringWriter();

                // TODO: define the charcode
                try (Reader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8), inputBufferSize)) {
                    char[] charBuffer = new char[inputBufferSize];
                    int i;
                    while ((i = reader.read(charBuffer)) != -1) {
                        body.write(charBuffer, 0, i);
                    }
                } catch (SocketTimeoutException e) {
                    netConn.cancel(new RequestTimeoutException(request, request.getTimeout()));
                    return;
                } catch (IOException e) {
                    netConn.cancel(new RequestCancelException(request, "Failed to read response body", e));
                    return;
                }

                serializedResponse = serializeResponseContent(conn.getHeaderField("Content-Type"),
                        body.toString());
            } else {
                serializedResponse = SerializedPayload.EMPTY_PAYLOAD;
            }

            // Evaluate response
            RawResponse response = new RawResponse(
                    deferred,
                    responseStatus,
                    readResponseHeaders(conn),
                    payloadType,
                    serializedResponse
            );

            conn.disconnect();

            evalResponse(response);
        } catch (RuntimeException e) {
            netConn.cancel(new RequestCancelException(request,
                    "An unexpected error has occurred while sending the request.", e));
        }
    }

    private <R> void disconnect(HttpURLConnection conn, Deferred<R> deferred, RequestException exception) {
        if (conn != null) conn.disconnect();
        if (deferred.isPending()) deferred.reject(exception);
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

    private SerializedPayload serializeResponseContent(String mediaType, String body) {
        if (body == null || body.equals("")) return SerializedPayload.EMPTY_PAYLOAD;
        return new TextSerializedPayload(body);
    }

    private NetHttpConnection getNetConnection(HttpURLConnection conn, Deferred<?> deferred, RequestOptions request) {
        return new NetHttpConnection(conn, deferred, request);
    }
}
