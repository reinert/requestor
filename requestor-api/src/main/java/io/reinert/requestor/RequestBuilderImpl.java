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

import io.reinert.requestor.header.AcceptHeader;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.SimpleHeader;

/**
 * Default implementation for {@link RequestBuilder}.
 *
 * @author Danilo Reinert
 */
public class RequestBuilderImpl implements RequestBuilder, RequestFilterContext {

    private final String url;
    private String httpMethod;
    private Headers headers;
    private String user;
    private String password;
    private int timeout;
    private Object payload;
    private ResponseType responseType = ResponseType.DEFAULT;

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
        copy.user = request.getUser();
        copy.password = request.getPassword();
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
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public String getPassword() {
        return password;
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
    public String getUser() {
        return user;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
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
    public RequestBuilder password(String password) {
        this.password = password;
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
    public RequestBuilder user(String user) {
        this.user = user;
        return this;
    }

    //===================================================================
    // RequestFilterContext methods
    //===================================================================

    @Override
    public String getHeader(String name) {
        return headers.get(name).getValue();
    }

    @Override
    public void addHeader(Header header) {
        headers.add(header);
    }

    @Override
    public void setMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public void setUser(String username) {
        this.user = username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
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
