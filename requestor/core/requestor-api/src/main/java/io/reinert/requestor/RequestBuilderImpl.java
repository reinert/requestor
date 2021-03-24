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
package io.reinert.requestor;

import io.reinert.requestor.auth.Auth;
import io.reinert.requestor.header.AcceptHeader;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.SimpleHeader;
import io.reinert.requestor.uri.Uri;

/**
 * Default implementation for {@link RequestBuilder}.
 *
 * @author Danilo Reinert
 */
class RequestBuilderImpl implements RequestBuilder, RequestFilterContext {

    private final Uri uri;
    private final VolatileStorage storage;
    private final Headers headers;
    private HttpMethod httpMethod;
    private int timeout;
    private Object payload;
    private Auth auth = PassThroughAuth.getInstance();

    public RequestBuilderImpl(Uri uri, VolatileStorage storage) {
        this(uri, storage, new Headers());
    }

    public RequestBuilderImpl(Uri uri, VolatileStorage storage, Headers headers) {
        this.uri = uri;
        this.storage = storage;
        this.headers = headers;
    }

    private static RequestBuilderImpl copy(RequestBuilderImpl request) {
        RequestBuilderImpl copy = new RequestBuilderImpl(
                request.getUri(),
                VolatileStorage.copy(request.getStorage()),
                request.getHeaders()
        );
        copy.httpMethod = request.getMethod();
        copy.auth = request.getAuth();
        copy.timeout = request.getTimeout();
        copy.payload = request.getPayload();
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
    public Uri getUri() {
        return uri;
    }

    @Override
    public Auth getAuth() {
        return auth;
    }

    @Override
    public VolatileStorage getStorage() {
        return storage;
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
        headers.set(header, value);
        return this;
    }

    @Override
    public RequestBuilder header(Header header) {
        headers.add(header);
        return this;
    }

    @Override
    public RequestBuilder payload(Object payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public RequestBuilder timeout(int timeoutMillis) {
        if (timeoutMillis > 0)
            timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestBuilder auth(Auth auth) {
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
    public void removeHeader(String name) {
        headers.remove(name);
    }

    @Override
    public void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    //===================================================================
    // Own methods
    //===================================================================

    protected RequestBuilderImpl build() {
        return RequestBuilderImpl.copy(this);
    }
}
