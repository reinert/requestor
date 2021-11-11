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
package io.reinert.requestor.core;

import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.uri.Uri;

/**
 * PreparedRequest implementation that ensures the request is dispatched only once.
 *
 * @author Danilo Reinert
 */
class PreparedRequestImpl<R> implements PreparedRequest {

    private final RequestDispatcher dispatcher;
    private final MutableSerializedRequest request;
    private final Deferred<R> deferred;
    private final PayloadType responsePayloadType;
    private final UriWithQueryBuilder uri;

    private boolean withCredentials;
    private boolean sent;

    public PreparedRequestImpl(RequestDispatcher dispatcher, MutableSerializedRequest request, Deferred<R> deferred,
                               PayloadType responsePayloadType) {
        this(dispatcher, request, deferred, responsePayloadType, false, false);
    }

    private PreparedRequestImpl(RequestDispatcher dispatcher, MutableSerializedRequest request, Deferred<R> deferred,
                                PayloadType responsePayloadType, boolean withCredentials, boolean sent) {
        this.dispatcher = dispatcher;
        this.request = request;
        this.deferred = deferred;
        this.responsePayloadType = responsePayloadType;
        this.withCredentials = withCredentials;
        this.sent = sent;
        this.uri = new UriWithQueryBuilder(request.getUri());
    }

    @Override
    public void abort(RawResponse response) {
        if (sent)
            throw new IllegalStateException("PreparedRequest couldn't be aborted: Request has already been sent.");

        dispatcher.evalResponse(response);

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
            dispatcher.send(this, deferred, responsePayloadType);
        } catch (Exception e) {
            deferred.reject(new RequestDispatchException(request,
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
    public String getHeader(String headerName) {
        return request.getHeader(headerName);
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
    public SerializedPayload getSerializedPayload() {
        return request.getSerializedPayload();
    }

    @Override
    public int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public int getDelay() {
        return request.getDelay();
    }

    @Override
    public boolean isPolling() {
        return request.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return request.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return request.getPollingLimit();
    }

    @Override
    public int getPollingCounter() {
        return request.getPollingCounter();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return request.getPollingStrategy();
    }

    @Override
    public void stopPolling() {
        request.stopPolling();
    }

    @Override
    public Uri getUri() {
        return uri.getUri();
    }

    @Override
    public PayloadType getResponsePayloadType() {
        return responsePayloadType;
    }

    @Override
    public RequestDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public Deferred<?> getDeferred() {
        return deferred;
    }

    @Override
    public Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public Store getStore() {
        return request.getStore();
    }

    @Override
    public boolean isWithCredentials() {
        return withCredentials;
    }

    @Override
    public void setHeader(Header header) {
        request.setHeader(header);
    }

    @Override
    public void setHeader(String name, String value) {
        request.setHeader(name, value);
    }

    @Override
    public Header delHeader(String headerName) {
        return request.delHeader(headerName);
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
}