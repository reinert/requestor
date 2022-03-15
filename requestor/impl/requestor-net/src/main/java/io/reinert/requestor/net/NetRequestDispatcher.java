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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.HttpStatus;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.RequestAbortException;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.ResponseProcessor;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.uri.Uri;

/**
 * RequestDispatcher implementation using {@link HttpURLConnection}.
 *
 * @author Danilo Reinert
 */
class NetRequestDispatcher extends RequestDispatcher {

    private final ScheduledThreadPoolExecutor threadPool;

    public NetRequestDispatcher(RequestProcessor requestProcessor,
                                ResponseProcessor responseProcessor,
                                DeferredPool.Factory deferredPoolFactory,
                                ScheduledThreadPoolExecutor threadPool) {
        super(requestProcessor, responseProcessor, deferredPoolFactory);
        this.threadPool = threadPool;
    }

    public void scheduleRun(final Runnable runnable, int delay) {
        threadPool.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    protected <R> void send(PreparedRequest request, Deferred<R> deferred, PayloadType payloadType) {
        // Return if deferred were rejected or resolved before this method was called
        if (!deferred.isPending()) return;

        URL url = null;
        HttpURLConnection conn = null;
        SerializedPayload serializedPayload = request.getSerializedPayload();

        try {
            // Set up connection
            url = new URL(request.getUri().toString());

            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(!serializedPayload.isEmpty());

            conn.setDoInput(payloadType != null && payloadType.getType() != Void.class);

            conn.setRequestMethod(request.getMethod().getValue());

            for (Header header : request.getHeaders()) {
                conn.setRequestProperty(header.getName(), header.getValue());
            }

            // TODO: expose a default content-type option
            if (!request.hasHeader("Content-Type")) conn.setRequestProperty("Content-Type", "text/plain");

            if (request.getTimeout() > 0) {
                conn.setConnectTimeout(request.getTimeout());
                if (conn.getDoInput()) conn.setReadTimeout(request.getTimeout());
            }

            // TODO: handle cookies

            if (!deferred.isPending()) return;

            conn.connect();

            deferred.setHttpConnection(getConnection(conn));
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
            disconnect(conn, deferred, new RequestRedirectException(request, Uri.create(conn.getURL().toString())));
            return;
        }

        try {
            // Payload upload
            if (conn.getDoOutput()) {
                OutputStreamWriter osw;
                try {
                    // TODO define the charcode
                    osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                    osw.write(serializedPayload.asString());
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    disconnect(conn, deferred, new RequestCancelException(request,
                            "Failed to write request payload", e));
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
            } catch (IOException e) {
                disconnect(conn, deferred, new RequestCancelException(request,
                        "Failed to read response status", e));
                return;
            }

            // Payload download
            SerializedPayload serializedResponse;
            if (conn.getDoInput()) {
                StringWriter content = new StringWriter();

                InputStreamReader isr;
                try {
                    // TODO define the charcode
                    isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    char[] charBuffer = new char[1024];

                    int i;
                    while ((i = isr.read(charBuffer)) != -1) {
                        content.write(charBuffer, 0, i);
                    }

                    isr.close();
                } catch (SocketTimeoutException e) {
                    disconnect(conn, deferred, new RequestTimeoutException(request, request.getTimeout()));
                    return;
                } catch (IOException e) {
                    disconnect(conn, deferred, new RequestCancelException(request,
                            "Failed to read response body", e));
                    return;
                }

                serializedResponse = serializeResponseContent(conn.getHeaderField("Content-Type"),
                        content.toString());
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
            disconnect(conn, deferred, new RequestCancelException(request,
                    "An unexpected error has occurred while sending the request.", e));
        }
    }

    private <R> void disconnect(HttpURLConnection conn, Deferred<R> deferred, RequestException exception) {
        if (conn != null) conn.disconnect();
        if (deferred.isPending()) deferred.reject(exception);
    }

    private Headers readResponseHeaders(HttpURLConnection conn) {
        List<Header> headers = new ArrayList<Header>();
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                // TODO: process headers as elements
                headers.add(Header.fromRawHeader(header.getKey(), join(", ", header.getValue())));
            }
        }
        return new Headers(headers);
    }

    private SerializedPayload serializeResponseContent(String type, String body) {
        if (body == null || body.equals("")) return SerializedPayload.EMPTY_PAYLOAD;
        return new SerializedPayload(body);
    }

    private HttpConnection getConnection(final HttpURLConnection conn) {
        // TODO: extract this implementation into a class
        return new HttpConnection() {
            private boolean pending = true;

            public void cancel() {
                if (pending) {
                    conn.disconnect();
                    pending = false;
                }
            }

            public boolean isPending() {
                return pending;
            }

            public HttpURLConnection getHttpUrlConnection() {
                return conn;
            }
        };
    }

    private static String join(String separator, List<String> input) {
        if (input == null || input.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {
            sb.append(input.get(i));
            // if not the last item
            if (i != input.size() - 1) {
                sb.append(separator);
            }
        }

        return sb.toString();
    }
}
