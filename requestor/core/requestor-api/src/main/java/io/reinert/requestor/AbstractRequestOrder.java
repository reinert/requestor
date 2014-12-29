/*
 * Copyright 2014 Danilo Reinert
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
package io.reinert.requestor;

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.header.SimpleHeader;

/**
 * Abstract implementation of RequestOrder which ensures the request to be dispatched only once.
 *
 * @author Danilo Reinert
 */
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
    public HttpMethod getMethod() {
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
