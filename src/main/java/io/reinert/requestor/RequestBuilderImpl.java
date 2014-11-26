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
public class RequestBuilderImpl implements RequestBuilder {

    private final String url;
    private String httpMethod;
    private Headers headers;
    private String password;
    private Object payload;
    private int timeout;
    private String user;
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
        copy.password = request.getPassword();
        copy.user = request.getUser();
        copy.timeout = request.getTimeout();
        copy.payload = request.getPayload();
        copy.httpMethod = request.getMethod();
        return copy;
    }

    //===================================================================
    // Request methods
    //===================================================================

    public String getAccept() {
        return headers.getValue("Accept");
    }

    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    public Headers getHeaders() {
        // Returns a defensive copy
        return new Headers(headers);
    }

    public String getMethod() {
        return httpMethod;
    }

    public String getPassword() {
        return password;
    }

    public Object getPayload() {
        return payload;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getUrl() {
        return url;
    }

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

    public RequestBuilder accept(String mediaType) {
        headers.add(new AcceptHeader(mediaType));
        return this;
    }

    public RequestBuilder contentType(String mediaType) {
        headers.add(new ContentTypeHeader(mediaType));
        return this;
    }

    public RequestBuilder header(String header, String value) {
        headers.add(new SimpleHeader(header, value));
        return this;
    }

    public RequestBuilder header(Header header) {
        headers.add(header);
        return this;
    }

    public RequestBuilder password(String password) {
        this.password = password;
        return this;
    }

    public RequestBuilder payload(Object object) {
        payload = object;
        return this;
    }

    public RequestBuilder responseType(ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }

    public RequestBuilder timeout(int timeoutMillis) {
        if (timeoutMillis > 0)
            timeout = timeoutMillis;
        return this;
    }

    public RequestBuilder user(String user) {
        this.user = user;
        return this;
    }

    //===================================================================
    // Own methods
    //===================================================================

    protected RequestBuilder build() {
        return RequestBuilderImpl.copyOf(this);
    }

    protected void setMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
}
