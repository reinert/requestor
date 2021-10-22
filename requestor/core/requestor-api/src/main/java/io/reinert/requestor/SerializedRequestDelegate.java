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

import io.reinert.requestor.auth.Auth;
import io.reinert.requestor.uri.Uri;

/**
 * Represents a request with its payload already serialized.
 * <p/>
 *
 * This class delegates all methods to the non-serialized request given at the constructor, except the payload.
 *
 * @author Danilo Reinert
 */
class SerializedRequestDelegate implements SerializedRequest, RequestInterceptorContext {

    private final Request request;
    private Payload serializedPayload;

    public SerializedRequestDelegate(Request request, Payload serializedPayload) {
        this.request = request;
        this.serializedPayload = serializedPayload;
    }

    @Override
    public String getAccept() {
        return request.getAccept();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public Headers getHeaders() {
        return request.getHeaders();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public Payload getPayload() {
        return serializedPayload;
    }

    @Override
    public int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public Uri getUri() {
        return request.getUri();
    }

    @Override
    public ResponseType getResponseType() {
        return request.getResponseType();
    }

    @Override
    public void setPayload(Payload payload) {
        this.serializedPayload = payload;
    }
}
