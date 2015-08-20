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

/**
 * Represents a request with its payload already serialized.
 * <p/>
 *
 * This is the final form of a request before it's sent to the server.
 *
 * @author Danilo Reinert
 */
public class SerializedRequestImpl implements SerializedRequest {

    private final HttpMethod httpMethod;
    private final String url;
    private final Headers headers;
    private final Payload payload;
    private final int timeout;
    private final ResponseType responseType;
    private final Auth auth;

    public SerializedRequestImpl(HttpMethod httpMethod, String url) {
        this(httpMethod, url, new Headers(), null, 0, ResponseType.DEFAULT, PassThroughAuth.getInstance());
    }

    public SerializedRequestImpl(HttpMethod httpMethod, String url, Payload payload) {
        this(httpMethod, url, new Headers(), payload, 0, ResponseType.DEFAULT, PassThroughAuth.getInstance());
    }

    public SerializedRequestImpl(HttpMethod httpMethod, String url, Headers headers) {
        this(httpMethod, url, headers, null, 0, ResponseType.DEFAULT, PassThroughAuth.getInstance());
    }

    public SerializedRequestImpl(HttpMethod httpMethod, String url, Headers headers, Payload payload) {
        this(httpMethod, url, headers, payload, 0, ResponseType.DEFAULT, PassThroughAuth.getInstance());
    }

    public SerializedRequestImpl(HttpMethod httpMethod, String url, Headers headers, Payload payload, int timeout) {
        this(httpMethod, url, headers, payload, timeout, ResponseType.DEFAULT, PassThroughAuth.getInstance());
    }

    public SerializedRequestImpl(HttpMethod httpMethod, String url, Headers headers, Payload payload, int timeout,
                                 ResponseType responseType) {
        this(httpMethod, url, headers, payload, timeout, responseType, PassThroughAuth.getInstance());
    }

    public SerializedRequestImpl(HttpMethod httpMethod, String url, Headers headers, Payload payload, int timeout,
                                 ResponseType responseType, Auth auth) {
        checkNotNull(httpMethod, "HTTP Method cannot be null.");
        checkNotNullOrEmpty(url, "URL cannot be null neither empty.");
        checkNotNull(headers, "Headers cannot be null.");
        checkNotNull(responseType, "ResponseType cannot be null.");
        checkNotNull(auth, "Auth cannot be null.");
        this.httpMethod = httpMethod;
        this.url = url;
        this.headers = headers;
        this.payload = payload;
        this.timeout = timeout > 0 ? timeout : 0;
        this.responseType = responseType;
        this.auth = auth;
    }

    @Override
    public HttpMethod getMethod() {
        return httpMethod;
    }

    @Override
    public String getUrl() {
        return url;
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
        return headers;
    }

    @Override
    public String getHeader(String name) {
        return headers.getValue(name);
    }

    @Override
    public Payload getPayload() {
        return payload;
    }

    @Override
    public Auth getAuth() {
        return auth;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    private void checkNotNull(Object o, String message) {
        if (o == null) throw new IllegalArgumentException(message);
    }

    private void checkNotNullOrEmpty(String s, String message) {
        if (s == null || s.isEmpty()) throw new IllegalArgumentException(message);
    }
}
