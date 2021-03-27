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
import io.reinert.requestor.header.Header;
import io.reinert.requestor.uri.Uri;

/**
 * PreparedRequest implementation that ensures the request is dispatched only once.
 *
 * @author Danilo Reinert
 */
class PreparedRequestImpl<R> implements PreparedRequest {

    private final RequestDispatcher dispatcher;
    private final MutableSerializedRequest request;
    private final Deferred<R> deferred;
    private final Class<R> resolveType;
    private final Class<?> parametrizedType;
    private final ResponseType responseType;
    private final UriWithQueryBuilder uri;

    private boolean withCredentials;
    private boolean sent;

    public PreparedRequestImpl(RequestDispatcher dispatcher, MutableSerializedRequest request, Deferred<R> deferred,
                               Class<R> resolveType, Class<?> parametrizedType) {
        this(dispatcher, request, deferred, resolveType, parametrizedType, false, false);
    }

    private PreparedRequestImpl(RequestDispatcher dispatcher, MutableSerializedRequest request, Deferred<R> deferred,
                                Class<R> resolveType, Class<?> parametrizedType, boolean withCredentials,
                                boolean sent) {
        this.dispatcher = dispatcher;
        this.request = request;
        this.deferred = deferred;
        this.resolveType = resolveType;
        this.parametrizedType = parametrizedType;
        this.responseType = ResponseType.of(resolveType);
        this.withCredentials = withCredentials;
        this.sent = sent;
        this.uri = new UriWithQueryBuilder(request.getUri());
    }

    @Override
    public void abort(RawResponse response) {
        if (sent)
            throw new IllegalStateException("PreparedRequest couldn't be aborted: Request has already been sent.");

        dispatcher.evalResponse(request, deferred, resolveType, parametrizedType, response);

        sent = true;
    }

    @Override
    public void abort(RequestException error) {
        if (sent)
            throw new IllegalStateException("PreparedRequest couldn't be aborted: Request has already been sent.");

        deferred.reject(error);

        sent = true;
    }

    @Override
    public void send() {
        if (sent)
            throw new IllegalStateException("PreparedRequest has already been sent.");

        try {
            dispatcher.send(this, deferred, resolveType, parametrizedType);
        } catch (Exception e) {
            deferred.reject(new RequestDispatchException(
                    "Some non-caught exception occurred while dispatching the request", e));
        }

        sent = true;
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
    public Object getPayload() {
        return request.getPayload();
    }

    @Override
    public Payload getSerializedPayload() {
        return request.getSerializedPayload();
    }

    @Override
    public int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public Uri getUri() {
        return uri.getUri();
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public Storage getStorage() {
        return request.getStorage();
    }

    @Override
    public boolean isWithCredentials() {
        return withCredentials;
    }

    @Override
    public void addHeader(Header header) {
        request.addHeader(header);
    }

    @Override
    public void setHeader(String name, String value) {
        request.setHeader(name, value);
    }

    @Override
    public void removeHeader(String headerName) {
        request.removeHeader(headerName);
    }

    @Override
    public void setQueryParam(String name, String... values) {
        uri.setQueryParam(name, values);
    }

    @Override
    public MutableSerializedRequest getMutableCopy() {
        return request.copy();
    }

    @Override
    public void setWithCredentials(boolean withCredentials) {
        this.withCredentials = withCredentials;
    }

    @Override
    public Class<R> getResolveType() {
        return resolveType;
    }

    @Override
    public Class<?> getParametrizedType() {
        return parametrizedType;
    }
}
