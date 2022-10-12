/*
 * Copyright 2021-2022 Danilo Reinert
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

import java.util.Set;

import io.reinert.requestor.core.header.ContentTypeHeader;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.uri.Uri;

/**
 * A request that process an {@link Auth}.
 *
 * @param <R> the expected type in the response payload.
 *
 * @author Danilo Reinert
 */
class RequestInAuthProcess<R> implements ProcessableRequest {

    private final MutableSerializedRequest request;
    private final PayloadType responsePayloadType;
    private final RequestDispatcher dispatcher;
    private final Deferred<R> deferred;

    public RequestInAuthProcess(MutableSerializedRequest request, PayloadType responsePayloadType,
                                RequestDispatcher dispatcher, Deferred<R> deferred) {
        this.request = request;
        this.responsePayloadType = responsePayloadType;
        this.dispatcher = dispatcher;
        this.deferred = deferred;
    }

    //===================================================================
    // RequestInProcess methods
    //===================================================================

    @Override
    public void process() {
        final Auth auth = request.getAuth();

        if (auth instanceof Base64Codec.Holder) {
            ((Base64Codec.Holder) auth).setBase64Codec(Base64Codec.getInstance());
        }

        final PreparedRequestImpl<R> preparedRequest = new PreparedRequestImpl<R>(dispatcher, this, deferred,
                responsePayloadType);

        if (request.isRetryEnabled()) {
            deferred.setRequestRetrier(new RequestRetrier(preparedRequest, dispatcher, request.getRetryPolicy()));
        }

        if (auth == null) {
            preparedRequest.send();
        } else {
            auth.auth(preparedRequest);
        }
    }

    @Override
    public final void proceed() {
        this.process();
    }

    @Override
    public void abort(MockResponse response) {
        final RawResponse rawResponse = new RawResponse(deferred, response.getStatus(), response.getHeaders(),
                responsePayloadType, response.getSerializedPayload());

        dispatcher.evalResponse(rawResponse);
    }

    @Override
    public void abort(RequestAbortException error) {
        deferred.reject(error);
    }

    //===================================================================
    // MutableSerializedRequest methods
    //===================================================================

    @Override
    public void setUri(Uri uri) {
        request.setUri(uri);
    }

    @Override
    public final void setContentType(String mediaType) {
        request.setContentType(mediaType);
    }

    @Override
    public void setContentType(ContentTypeHeader header) {
        request.setHeader(header);
    }

    @Override
    public final void setAccept(String mediaType) {
        request.setAccept(mediaType);
    }

    @Override
    public final void setHeader(Header header) {
        request.setHeader(header);
    }

    @Override
    public final void setHeader(String header, String value) {
        request.setHeader(header, value);
    }

    @Override
    public final Header delHeader(String name) {
        return request.delHeader(name);
    }

    @Override
    public final void setAuth(Auth auth) {
        request.setAuth(auth);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        request.setAuth(authProvider);
    }

    @Override
    public final void setTimeout(int timeoutMillis) {
        request.setTimeout(timeoutMillis);
    }

    @Override
    public void setRetry(int[] delaysMillis, RequestEvent... events) {
        request.setRetry(delaysMillis, events);
    }

    @Override
    public void setRetry(RetryPolicy retryPolicy) {
        request.setRetry(retryPolicy);
    }

    @Override
    public void setRetry(RetryPolicy.Provider retryPolicyProvider) {
        request.setRetry(retryPolicyProvider);
    }

    @Override
    public int incrementPollingCount() {
        return request.incrementPollingCount();
    }

    @Override
    public final void setPayload(Object payload, String... fields) {
        request.setPayload(payload, fields);
    }

    @Override
    public final void setMethod(HttpMethod httpMethod) {
        request.setMethod(httpMethod);
    }

    @Override
    public void setCharset(String charset) {
        request.setCharset(charset);
    }

    @Override
    public void setSkippedProcesses(Process... processes) {
        request.setSkippedProcesses(processes);
    }

    @Override
    public Session getSession() {
        return request.getSession();
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        request.setSerializedPayload(serializedPayload);
    }

    @Override
    public MutableSerializedRequest copy() {
        return request.copy();
    }

    @Override
    public MutableSerializedRequest replicate() {
        return request.replicate();
    }

    @Override
    public void serializePayload(SerializedPayload serializedPayload) {
        try {
            ((SerializableRequest) request).serializePayload(serializedPayload);
        } catch (ClassCastException e) {
            throw new RuntimeException("Cannot serialize payload. Delegated request is not a SerializableRequest.", e);
        }
    }

    //===================================================================
    // RequestOptions methods
    //===================================================================

    @Override
    public final String getAccept() {
        return request.getAccept();
    }

    @Override
    public final String getContentType() {
        return request.getContentType();
    }

    @Override
    public final Headers getHeaders() {
        return request.getHeaders();
    }

    @Override
    public final String getHeader(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public boolean hasHeader(String headerName) {
        return request.hasHeader(headerName);
    }

    @Override
    public final HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public final Payload getPayload() {
        return request.getPayload();
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        return request.getSerializedPayload();
    }

    @Override
    public final int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public int getDelay() {
        return request.getDelay();
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return request.getRetryPolicy();
    }

    @Override
    public boolean isRetryEnabled() {
        return request.isRetryEnabled();
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
    public int getPollingCount() {
        return request.getPollingCount();
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
    public final Uri getUri() {
        return request.getUri();
    }

    @Override
    public final Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public String getCharset() {
        return request.getCharset();
    }

    @Override
    public Set<Process> getSkippedProcesses() {
        return request.getSkippedProcesses();
    }

    //===================================================================
    // Store methods
    //===================================================================

    @Override
    @Deprecated
    public <T> T retrieve(String key) {
        return getValue(key);
    }

    @Override
    public <T> T getValue(String key) {
        return request.getValue(key);
    }

    @Override
    public Data getData(String key) {
        return request.getData(key);
    }

    @Override
    public RequestInAuthProcess<R> save(String key, Object value) {
        request.save(key, value);
        return this;
    }

    @Override
    public RequestInAuthProcess<R> save(String key, Object value, Level level) {
        request.save(key, value, level);
        return this;
    }

    @Override
    public RequestInAuthProcess<R> save(String key, Object value, long ttl, Level level) {
        request.save(key, value, ttl, level);
        return this;
    }

    @Override
    public RequestInAuthProcess<R> save(String key, Object value, long ttl) {
        request.save(key, value, ttl);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return request.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return request.exists(key, value);
    }

    @Override
    public Data remove(String key) {
        return request.remove(key);
    }

    @Override
    public Data refresh(String key, long ttlMillis) {
        return request.refresh(key, ttlMillis);
    }

    @Override
    public Data refresh(String key) {
        return request.refresh(key);
    }

    @Override
    public void clear() {
        request.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        request.clear(fireRemovedEvent);
    }

    @Override
    public RequestInAuthProcess<R> onSaved(String key, Handler handler) {
        request.onSaved(key, handler);
        return this;
    }

    @Override
    public RequestInAuthProcess<R> onRemoved(String key, Handler handler) {
        request.onRemoved(key, handler);
        return this;
    }

    @Override
    public RequestInAuthProcess<R> onExpired(String key, Handler handler) {
        request.onExpired(key, handler);
        return this;
    }

    //===================================================================
    // RequestInAuthProcess methods
    //===================================================================

    public RequestDispatcher getDispatcher() {
        return dispatcher;
    }

    public Deferred<R> getDeferred() {
        return deferred;
    }
}
