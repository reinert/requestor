package io.reinert.requestor;

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.header.SimpleHeader;

abstract class AbstractRequestOrder implements RequestOrder {

    private final SerializedRequest request;
    private final Headers headers;
    private boolean withCredentials;
    private boolean sent;

    protected AbstractRequestOrder(SerializedRequest request) {
        this.request = request;
        this.headers = request.getHeaders();
    }

    protected abstract void doSend();

    @Override
    public void send() {
        if (!sent) {
            doSend();
            sent = true;
        } else {
            throw new IllegalStateException("RequestOrder has already been sent.");
        }
    }

    @Override
    public String getAccept() {
        return headers.getValue("Accept");
    }

    @Override
    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    @Override
    public Headers getHeaders() {
        return headers; // mutable
    }

    @Override
    public String getHeader(String name) {
        return headers.getValue(name);
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public Payload getPayload() {
        return request.getPayload();
    }

    @Override
    public int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public String getUrl() {
        return request.getUrl();
    }

    @Override
    public ResponseType getResponseType() {
        return request.getResponseType();
    }

    @Override
    public void setHeader(String name, String value) {
        headers.add(new SimpleHeader(name, value));
    }

    @Override
    public Authentication getAuth() {
        return request.getAuth();
    }

    @Override
    public boolean isWithCredentials() {
        return withCredentials;
    }

    @Override
    public void setWithCredentials(boolean withCredentials) {
        this.withCredentials = withCredentials;
    }
}
