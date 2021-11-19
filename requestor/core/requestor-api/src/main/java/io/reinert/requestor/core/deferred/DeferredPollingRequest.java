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
package io.reinert.requestor.core.deferred;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.reinert.requestor.core.Auth;
import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.HttpMethod;
import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.PollingStrategy;
import io.reinert.requestor.core.RequestEvent;
import io.reinert.requestor.core.SerializedRequest;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.Store;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ProgressRequestCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.uri.Uri;

/**
 * Default Deferred implementation.
 *
 * @param <T> Expected type in Request#done.
 */
public class DeferredPollingRequest<T> implements DeferredPool<T>, PollingRequest<T> {

    private static final Logger logger = Logger.getLogger(DeferredPollingRequest.class.getName());

    private final SerializedRequest serializedRequest;
    private final List<DeferredRequest<T>> deferreds;

    public DeferredPollingRequest(SerializedRequest serializedRequest) {
        this(serializedRequest, new ArrayList<DeferredRequest<T>>());
    }

    protected DeferredPollingRequest(SerializedRequest serializedRequest, List<DeferredRequest<T>> deferreds) {
        this.serializedRequest = serializedRequest;
        this.deferreds = deferreds;
    }

    //===================================================================
    // PollingRequest
    //===================================================================

    @Override
    public String getAccept() {
        return serializedRequest.getAccept();
    }

    @Override
    public String getContentType() {
        return serializedRequest.getContentType();
    }

    @Override
    public Headers getHeaders() {
        return serializedRequest.getHeaders();
    }

    @Override
    public String getHeader(String name) {
        return serializedRequest.getHeader(name);
    }

    @Override
    public HttpMethod getMethod() {
        return serializedRequest.getMethod();
    }

    @Override
    public Payload getPayload() {
        return serializedRequest.getPayload();
    }

    @Override
    public int getTimeout() {
        return serializedRequest.getTimeout();
    }

    @Override
    public int getDelay() {
        return serializedRequest.getDelay();
    }

    @Override
    public List<Integer> getRetryDelays() {
        return serializedRequest.getRetryDelays();
    }

    @Override
    public List<RequestEvent> getRetryEvents() {
        return serializedRequest.getRetryEvents();
    }

    @Override
    public boolean isRetryEnabled() {
        return serializedRequest.isRetryEnabled();
    }

    @Override
    public boolean isPolling() {
        return serializedRequest.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return serializedRequest.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return serializedRequest.getPollingLimit();
    }

    @Override
    public int getPollingCounter() {
        return serializedRequest.getPollingCounter();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return serializedRequest.getPollingStrategy();
    }

    @Override
    public void stopPolling() {
        serializedRequest.stopPolling();
    }

    @Override
    public Uri getUri() {
        return serializedRequest.getUri();
    }

    @Override
    public Auth getAuth() {
        return serializedRequest.getAuth();
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        return serializedRequest.getSerializedPayload();
    }

    @Override
    public Store getStore() {
        return serializedRequest.getStore();
    }

    //===================================================================
    // Request
    //===================================================================

    @Override
    public HttpConnection getHttpConnection() {
        return getLastDeferred().getHttpConnection();
    }

    @Override
    public PollingRequest<T> onAbort(final ExceptionCallback callback) {
        getLastDeferred().onAbort(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onAbort(final ExceptionRequestCallback<T> callback) {
        getLastDeferred().onAbort(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onCancel(final ExceptionCallback callback) {
        getLastDeferred().onCancel(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onCancel(final ExceptionRequestCallback<T> callback) {
        getLastDeferred().onCancel(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onError(final ExceptionCallback callback) {
        getLastDeferred().onError(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onError(final ExceptionRequestCallback<T> callback) {
        getLastDeferred().onError(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onLoad(final ResponseCallback callback) {
        getLastDeferred().onLoad(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onLoad(final ResponseRequestCallback<T> callback) {
        getLastDeferred().onLoad(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onFail(final ResponseCallback callback) {
        getLastDeferred().onFail(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onFail(final ResponseRequestCallback<T> callback) {
        getLastDeferred().onFail(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onProgress(final io.reinert.requestor.core.callback.ProgressCallback callback) {
        getLastDeferred().onProgress(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onProgress(final ProgressRequestCallback<T> callback) {
        getLastDeferred().onProgress(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(final int statusCode, final ResponseCallback callback) {
        getLastDeferred().onStatus(statusCode, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(final int statusCode, final ResponseRequestCallback<T> callback) {
        getLastDeferred().onStatus(statusCode, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(final Status status, final ResponseCallback callback) {
        getLastDeferred().onStatus(status, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(final Status status, final ResponseRequestCallback<T> callback) {
        getLastDeferred().onStatus(status, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(final StatusFamily family, final ResponseCallback callback) {
        getLastDeferred().onStatus(family, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(final StatusFamily family, final ResponseRequestCallback<T> callback) {
        getLastDeferred().onStatus(family, callback);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> PollingRequest<T> onSuccess(final PayloadCallback<E> callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> PollingRequest<T> onSuccess(final PayloadResponseCallback<E> callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> PollingRequest<T> onSuccess(final PayloadResponseRequestCallback<E> callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onTimeout(final TimeoutCallback callback) {
        getLastDeferred().onTimeout(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onTimeout(final TimeoutRequestCallback<T> callback) {
        getLastDeferred().onTimeout(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onUpProgress(final io.reinert.requestor.core.callback.ProgressCallback callback) {
        getLastDeferred().onUpProgress(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onUpProgress(final ProgressRequestCallback<T> callback) {
        getLastDeferred().onUpProgress(callback);
        return this;
    }

    //===================================================================
    // Request
    //===================================================================

    @Override
    public Deferred<T> newDeferred() {
        final DeferredRequest<T> deferred = deferreds.isEmpty() ?
                new DeferredRequest<T>(this) : getLastDeferred().replicate();
        deferreds.add(deferred);
        return deferred;
    }

    @Override
    public PollingRequest<T> getRequest() {
        return this;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected SerializedRequest getSerializedRequest() {
        return serializedRequest;
    }

    protected DeferredRequest<T> getLastDeferred() {
        if (deferreds.isEmpty()) deferreds.add(new DeferredRequest<T>(this));
        return deferreds.get(deferreds.size() - 1);
    }
}
