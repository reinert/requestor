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
import io.reinert.requestor.header.AcceptHeader;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.SimpleHeader;

/**
 * Default implementation for {@link RequestBuilder}.
 *
 * @author Danilo Reinert
 */
class RequestBuilderImpl implements RequestBuilder, RequestFilterContext {

    private final String url;
    private HttpMethod httpMethod;
    private Headers headers;
    private int timeout;
    private Object payload;
    private ResponseType responseType = ResponseType.DEFAULT;
    private Authentication auth = PassThroughAuth.getInstance();

    public RequestBuilderImpl(String url) {
        this(url, new Headers());
    }

    public RequestBuilderImpl(String url, Headers headers) {
        this.url = url;
        this.headers = headers;
    }

    public static RequestBuilderImpl copyOf(RequestBuilder request) {
        RequestBuilderImpl copy = new RequestBuilderImpl(request.getUrl(), request.getHeaders());
        copy.httpMethod = request.getMethod();
        copy.auth = request.getAuth();
        copy.timeout = request.getTimeout();
        copy.payload = request.getPayload();
        copy.responseType = request.getResponseType();
        return copy;
    }

    //===================================================================
    // Request methods
    //===================================================================

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
        // Returns a defensive copy
        return new Headers(headers);
    }

    @Override
    public HttpMethod getMethod() {
        return httpMethod;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public Authentication getAuth() {
        return auth;
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public RequestBuilder accept(String mediaType) {
        headers.add(new AcceptHeader(mediaType));
        return this;
    }

    @Override
    public RequestBuilder contentType(String mediaType) {
        headers.add(new ContentTypeHeader(mediaType));
        return this;
    }

    @Override
    public RequestBuilder header(String header, String value) {
        headers.add(new SimpleHeader(header, value));
        return this;
    }

    @Override
    public RequestBuilder header(Header header) {
        headers.add(header);
        return this;
    }

    @Override
    public RequestBuilder payload(Object object) {
        payload = object;
        return this;
    }

    @Override
    public RequestBuilder responseType(ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }

    @Override
    public RequestBuilder timeout(int timeoutMillis) {
        if (timeoutMillis > 0)
            timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestBuilder auth(Authentication auth) {
        if (auth == null)
            throw new IllegalArgumentException("Auth cannot be null.");
        this.auth = auth;
        return this;
    }

    //===================================================================
    // RequestFilterContext methods
    //===================================================================

    @Override
    public String getHeader(String name) {
        return headers.getValue(name);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.add(new SimpleHeader(name, value));
    }

    @Override
    public void addHeader(Header header) {
        headers.add(header);
    }

    @Override
    public void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public void setAuth(Authentication auth) {
        this.auth = auth;
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    @Override
    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    //===================================================================
    // Own methods
    //===================================================================

    protected RequestBuilderImpl build() {
        return RequestBuilderImpl.copyOf(this);
    }

}
