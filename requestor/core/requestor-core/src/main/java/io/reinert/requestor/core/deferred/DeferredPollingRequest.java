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
package io.reinert.requestor.core.deferred;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import io.reinert.requestor.core.AsyncRunner;
import io.reinert.requestor.core.Auth;
import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.HttpMethod;
import io.reinert.requestor.core.IncomingResponse;
import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.PollingStrategy;
import io.reinert.requestor.core.Process;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.RetryPolicy;
import io.reinert.requestor.core.SerializedRequest;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ReadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;
import io.reinert.requestor.core.callback.VoidCallback;
import io.reinert.requestor.core.callback.WriteCallback;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.uri.Uri;

/**
 * Default DeferredPool implementation.
 *
 * @param <T> Expected type in successful responses.
 *
 * @author Danilo Reinert
 */
public class DeferredPollingRequest<T> implements DeferredPool<T>, PollingRequest<T> {

    private final SerializedRequest serializedRequest;
    private final List<DeferredRequest<T>> deferreds;
    private final AsyncRunner asyncRunner;

    public DeferredPollingRequest(SerializedRequest serializedRequest, AsyncRunner asyncRunner) {
        this(serializedRequest, asyncRunner, new ArrayList<DeferredRequest<T>>());
    }

    protected DeferredPollingRequest(SerializedRequest serializedRequest, AsyncRunner asyncRunner,
                                     List<DeferredRequest<T>> deferreds) {
        this.serializedRequest = serializedRequest;
        this.asyncRunner = asyncRunner;
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
    public String getCharset() {
        return serializedRequest.getCharset();
    }

    @Override
    public Set<Process> getSkippedProcesses() {
        return serializedRequest.getSkippedProcesses();
    }

    @Override
    public Session getSession() {
        return serializedRequest.getSession();
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return serializedRequest.getRetryPolicy();
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
    public int getPollingCount() {
        return serializedRequest.getPollingCount();
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

    /**
     * @deprecated use {@link #getValue(String)} instead.
     */
    @Override
    @Deprecated
    public <T> T retrieve(String key) {
        return getValue(key);
    }

    @Override
    public <T> T getValue(String key) {
        return serializedRequest.getValue(key);
    }

    @Override
    public Data getData(String key) {
        return serializedRequest.getData(key);
    }

    @Override
    public DeferredPollingRequest<T> save(String key, Object value) {
        serializedRequest.save(key, value);
        return this;
    }

    @Override
    public DeferredPollingRequest<T> save(String key, Object value, Level level) {
        serializedRequest.save(key, value, level);
        return this;
    }

    @Override
    public DeferredPollingRequest<T> save(String key, Object value, long ttl, Level level) {
        serializedRequest.save(key, value, ttl, level);
        return this;
    }

    @Override
    public DeferredPollingRequest<T> save(String key, Object value, long ttl) {
        serializedRequest.save(key, value, ttl);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return serializedRequest.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return serializedRequest.exists(key, value);
    }

    @Override
    public Data remove(String key) {
        return serializedRequest.remove(key);
    }

    @Override
    public Data refresh(String key, long ttlMillis) {
        return serializedRequest.refresh(key, ttlMillis);
    }

    @Override
    public Data refresh(String key) {
        return serializedRequest.refresh(key);
    }

    @Override
    public void clear() {
        serializedRequest.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        serializedRequest.clear(fireRemovedEvent);
    }

    @Override
    public DeferredPollingRequest<T> onSaved(String key, Handler handler) {
        serializedRequest.onSaved(key, handler);
        return this;
    }

    @Override
    public DeferredPollingRequest<T> onRemoved(String key, Handler handler) {
        serializedRequest.onRemoved(key, handler);
        return this;
    }

    @Override
    public DeferredPollingRequest<T> onExpired(String key, Handler handler) {
        serializedRequest.onExpired(key, handler);
        return this;
    }

    //===================================================================
    // Request
    //===================================================================

    @Override
    public Response await() throws RequestException {
        DeferredRequest<T> deferred = getLastDeferred();

        while (deferred.isPending()) {
            try {
                deferred.getResponseLock().await(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (deferred.isRejected()) {
            throw deferred.getRejectResult();
        }

        return deferred.getResolveResult();
    }

    @Override
    public Future<IncomingResponse> getResponse() {
        return getLastDeferred().getFuture();
    }

    @Override
    public HttpConnection getHttpConnection() {
        return getLastDeferred().getHttpConnection();
    }

    @Override
    public int getRetryCount() {
        return getLastDeferred().getRetryCount();
    }

    @Override
    public PollingRequest<T> onAbort(ExceptionCallback callback) {
        getLastDeferred().onAbort(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onAbort(ExceptionRequestCallback<T> callback) {
        getLastDeferred().onAbort(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onCancel(ExceptionCallback callback) {
        getLastDeferred().onCancel(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onCancel(ExceptionRequestCallback<T> callback) {
        getLastDeferred().onCancel(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onError(VoidCallback callback) {
        getLastDeferred().onError(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onError(ExceptionCallback callback) {
        getLastDeferred().onError(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onError(ExceptionRequestCallback<T> callback) {
        getLastDeferred().onError(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onLoad(VoidCallback callback) {
        getLastDeferred().onLoad(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onLoad(ResponseCallback callback) {
        getLastDeferred().onLoad(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onLoad(ResponseRequestCallback<T> callback) {
        getLastDeferred().onLoad(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onFail(VoidCallback callback) {
        getLastDeferred().onFail(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onFail(ResponseCallback callback) {
        getLastDeferred().onFail(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onFail(ResponseRequestCallback<T> callback) {
        getLastDeferred().onFail(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onRead(ReadCallback callback) {
        getLastDeferred().onProgress(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(int statusCode, VoidCallback callback) {
        getLastDeferred().onStatus(statusCode, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(int statusCode, ResponseCallback callback) {
        getLastDeferred().onStatus(statusCode, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(int statusCode, ResponseRequestCallback<T> callback) {
        getLastDeferred().onStatus(statusCode, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(Status status, VoidCallback callback) {
        getLastDeferred().onStatus(status, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(Status status, ResponseCallback callback) {
        getLastDeferred().onStatus(status, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(Status status, ResponseRequestCallback<T> callback) {
        getLastDeferred().onStatus(status, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(StatusFamily family, VoidCallback callback) {
        getLastDeferred().onStatus(family, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(StatusFamily family, ResponseCallback callback) {
        getLastDeferred().onStatus(family, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onStatus(StatusFamily family, ResponseRequestCallback<T> callback) {
        getLastDeferred().onStatus(family, callback);
        return this;
    }

    @Override
    public PollingRequest<T> onSuccess(VoidCallback callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onSuccess(PayloadCallback<T> callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onSuccess(PayloadResponseCallback<T> callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onSuccess(PayloadResponseRequestCallback<T> callback) {
        getLastDeferred().onSuccess(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onTimeout(TimeoutCallback callback) {
        getLastDeferred().onTimeout(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onTimeout(TimeoutRequestCallback<T> callback) {
        getLastDeferred().onTimeout(callback);
        return this;
    }

    @Override
    public PollingRequest<T> onWrite(WriteCallback callback) {
        getLastDeferred().onUpProgress(callback);
        return this;
    }

    //===================================================================
    // Request
    //===================================================================

    @Override
    public Deferred<T> getDeferred() {
        return getLastDeferred();
    }

    public Deferred<T> newDeferred() {
        final DeferredRequest<T> deferred = deferreds.isEmpty() ?
                new DeferredRequest<T>(this, asyncRunner) : getLastDeferred().replicate();
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
        if (deferreds.isEmpty()) deferreds.add(new DeferredRequest<T>(this, asyncRunner));
        return deferreds.get(deferreds.size() - 1);
    }
}
