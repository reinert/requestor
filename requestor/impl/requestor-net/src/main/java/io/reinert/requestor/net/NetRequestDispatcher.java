/*
 * Copyright 2021 Danilo Reinert
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
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * RequestDispatcher implementation using {@link HttpURLConnection}.
 *
 * @author Onezino Gabriel
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

        HttpURLConnection conn = null;
        SerializedPayload serializedPayload = request.getSerializedPayload();

        // open connection
        try {
            URL url = new URL(request.getUri().toString());

            conn = (HttpURLConnection) url.openConnection();

            deferred.setHttpConnection(getConnection(conn));

            conn.setDoOutput(!serializedPayload.isEmpty());
            conn.setDoInput(payloadType != null && payloadType.getType() != Void.class);

            conn.setRequestMethod(request.getMethod().getValue());

            // set headers
            for (Header header : request.getHeaders()) {
                conn.setRequestProperty(header.getName(), header.getValue());
            }

            if (!request.hasHeader("Content-Type")) {
                conn.setRequestProperty("Content-Type", "text/plain");
            }

            // timeout
            if (request.getTimeout() > 0) {
                conn.setConnectTimeout(request.getTimeout());
            }

            // handle cookies ??

            // follow redirects ??

            conn.connect();
        } catch (MalformedURLException e) {
            deferred.reject(new RequestAbortException(request,
                    "Invalid url format", e));
            return;
        } catch (IOException e) {
            deferred.reject(new RequestAbortException(request,
                    "Failed to open connection", e));
            disconnect(deferred, conn);
            return;
        }

        try {
            // payload
            if (conn.getDoOutput()) {
                OutputStreamWriter osw;
                try {
                    // TODO define the charcode
                    osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                    osw.write(serializedPayload.asString());
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    deferred.reject(new RequestCancelException(request,
                            "Failed to write request payload", e));
                    disconnect(deferred, conn);
                    return;
                }
            }

            HttpStatus responseStatus;

            try {
                responseStatus = Status.of(conn.getResponseCode());
            } catch (IOException e) {
                deferred.reject(new RequestCancelException(request,
                        "Failed to read response status", e));
                disconnect(deferred, conn);
                return;
            }

            SerializedPayload serializedResponse;

            // read response body
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
                } catch (IOException e) {
                        deferred.reject(new RequestCancelException(request,
                                "Failed to read response body", e));
                        disconnect(deferred, conn);
                        return;
                }

                String responseBody = content.toString();

                serializedResponse = serializeResponseContent(conn.getHeaderField("Content-Type"),
                        responseBody);
            } else {
                serializedResponse = SerializedPayload.EMPTY_PAYLOAD;
            }

            RawResponse response = new RawResponse(
                    deferred,
                    responseStatus,
                    readResponseHeaders(conn),
                    payloadType,
                    serializedResponse
            );

            disconnect(deferred, conn);

            evalResponse(response);
        } catch (RuntimeException e) {
            deferred.reject(new RequestCancelException(request,
                    "An unexpected error has occurred while sending the request.", e));
            disconnect(deferred, conn);
        }
    }

    private <R> void disconnect(Deferred<R> deferred, HttpURLConnection conn) {
        if (conn != null) conn.disconnect();
        setFinalizedConnection(deferred);
    }

    private Headers readResponseHeaders(HttpURLConnection conn) {
        List<Header> headers = new ArrayList<Header>();
        // TODO multiple values for a header
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                headers.add(Header.fromRawHeader(header.getKey(), join(", ", header.getValue())));
            }
        }
        return new Headers(headers);
    }

    private SerializedPayload serializeResponseContent(String type, String body) {
        if (body == null || body.equals("")) return SerializedPayload.EMPTY_PAYLOAD;
        return new  SerializedPayload(body);
    }

    private HttpConnection getConnection(final HttpURLConnection conn) {
        return new HttpConnection() {
            public void cancel() {
                conn.disconnect();
            }

            public boolean isPending() {
                return true;
            }
        };
    }

    private void setFinalizedConnection(Deferred<?> deferred) {
        deferred.setHttpConnection(new HttpConnection() {
            public void cancel() {
            }

            public boolean isPending() {
                return false;
            }
        });
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
