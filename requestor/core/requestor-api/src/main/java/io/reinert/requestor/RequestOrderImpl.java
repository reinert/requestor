/*
 * Copyright 2015 Danilo Reinert
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

import java.util.Collection;

import javax.annotation.Nullable;

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.deferred.Deferred;
import io.reinert.requestor.header.SimpleHeader;

/**
 * Abstract implementation of RequestOrder which ensures the request to be dispatched only once.
 *
 * @author Danilo Reinert
 */
class RequestOrderImpl<T> implements RequestOrder {

    private final RequestDispatcher dispatcher;
    private final SerializedRequest request;
    private final Headers headers;
    private final Deferred<T> deferred;
    private final Class<T> resolveType;
    private final Class<?> parametrizedType;

    private boolean withCredentials;
    private boolean sent;

    public RequestOrderImpl(RequestDispatcher dispatcher, SerializedRequest request, Deferred<T> deferred,
                            Class<T> resolveType, @Nullable Class<?> parametrizedType) {
        this(dispatcher, request, deferred, resolveType, parametrizedType, false, false);
    }

    private RequestOrderImpl(RequestDispatcher dispatcher, SerializedRequest request, Deferred<T> deferred,
                             Class<T> resolveType, @Nullable Class<?> parametrizedType, boolean withCredentials,
                             boolean sent) {
        this.dispatcher = dispatcher;
        this.request = request;
        this.headers = request.getHeaders();
        this.deferred = deferred;
        this.resolveType = resolveType;
        this.parametrizedType = parametrizedType;
        this.withCredentials = withCredentials;
        this.sent = sent;
    }

    @Override
    public <D, C extends Collection> RequestOrder copy(Class<D> resultType, Class<C> containerType,
                                                       Deferred<C> deferred) {
        final SerializedRequest srCopy = new SerializedRequestImpl(request.getMethod(), request.getUrl(),
                request.getHeaders(), request.getPayload(), request.getTimeout(), request.getResponseType());
        return new RequestOrderImpl<C>(dispatcher, srCopy, deferred, containerType, resultType, withCredentials, false);
    }

    @Override
    public <D> RequestOrder copy(Class<D> resultType, Deferred<D> deferred) {
        final SerializedRequest srCopy = new SerializedRequestImpl(request.getMethod(), request.getUrl(),
                request.getHeaders(), request.getPayload(), request.getTimeout(), request.getResponseType());
        return new RequestOrderImpl<D>(dispatcher, srCopy, deferred, resultType, null, withCredentials, false);
    }

    @Override
    public void abort(RawResponse response) {
        if (sent)
            throw new IllegalStateException("RequestOrder couldn't be aborted: RequestOrder has already been sent.");

        dispatcher.evalResponse(request, deferred, resolveType, parametrizedType, response);

        sent = true;
    }

    @Override
    public void abort(RequestException error) {
        if (sent)
            throw new IllegalStateException("RequestOrder couldn't be aborted: RequestOrder has already been sent.");

        deferred.reject(error);

        sent = true;
    }

    @Override
    public void send() {
        if (sent)
            throw new IllegalStateException("RequestOrder has already been sent.");

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
