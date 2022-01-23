package io.reinert.requestor.net.dispatcher;

import io.reinert.requestor.core.*;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.net.payload.SerializedBufferPayload;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class JavaDispatcher extends RequestDispatcher {

    public JavaDispatcher(RequestProcessor requestProcessor, ResponseProcessor responseProcessor,
                          DeferredPool.Factory deferredPoolFactory) {
        super(requestProcessor, responseProcessor, deferredPoolFactory);
    }

    @Override
    public void scheduleRun(Runnable runnable, int delay) {}

    @Override
    protected <D> void send(final PreparedRequest request, final Deferred<D> deferred,
                            PayloadType payloadType) {
        if (!deferred.isPending()) return;

        MutableSerializedRequest req = request.getMutableCopy();
        HttpURLConnection conn;
        // open connection
        try {
            URL url = new URL(request.getUri().toString());

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(request.getMethod().getValue());

        } catch (MalformedURLException e) {
            deferred.reject(new RequestAbortException(req,
                    "invalid url format", e.getCause()));
            return;
        } catch (IOException e) {
            deferred.reject(new RequestAbortException(req,
                    "failed to open connection", e.getCause()));
            return;
        }
        // set headers
        for (Header header : request.getHeaders()) {
            conn.setRequestProperty(header.getName(), header.getValue());
        }
        // timeout
        if(request.getTimeout() > 0) {
            conn.setConnectTimeout(request.getTimeout());
        }

        // handle cookies ??

        // follow redirects ??

        // payload
        SerializedPayload serializedPayload = request.getSerializedPayload();
        if (!serializedPayload.isEmpty()) {
            conn.setDoOutput(true);
            try {
                OutputStream os = conn.getOutputStream();
                // TODO define the charcode
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(serializedPayload.asString());
                osw.flush();
                osw.close();
            } catch (IOException e) {
                deferred.reject(new RequestAbortException(req,
                        "failed to consume payload", e.getCause()));
                return;
            }
        }

        deferred.setHttpConnection(getConnection(conn));
        try {
            conn.connect();

        } catch (IOException e) {
            deferred.reject(new RequestAbortException(req,
                    "failed to request", e.getCause()));
            return;
        }

        // read response body
        StringBuffer content = new StringBuffer();
        try {
            // since we don't need read inputsteam because de getResponseMessage we should remove this code
            content.append(conn.getResponseMessage());
        } catch (IOException e) {
            deferred.reject(new RequestAbortException(req,
                    "failed to read response", e.getCause()));
            return;
        }
        //
        try {
            String responseBody = content.toString();

            SerializedPayload serializedResponse = serializeResponseContent(conn.getHeaderField("Content-Type"), responseBody);
            RawResponse response = new RawResponse(
                    deferred,
                    Status.of(conn.getResponseCode()),
                    readResponseHeaders(conn),
                    payloadType,
                    serializedResponse
            );
            evalResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Headers readResponseHeaders(HttpURLConnection conn) {
        List<Header> headers = new ArrayList<Header>();
        // TODO multiple values for a header
        for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
            for (String headerValue : header.getValue()) {
                headers.add(Header.fromRawHeader(header.getKey(), headerValue));
            }
        }
        return new Headers(headers);
    }

    private SerializedPayload serializeResponseContent(String type, String body) {
        if (body == null || body.equals(""))
            return SerializedPayload.EMPTY_PAYLOAD;
        return SerializedBufferPayload.fromString(body);
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
}
