/*
 * Copyright 2014-2022 Danilo Reinert
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
import java.util.concurrent.Future;

import io.reinert.requestor.core.callback.DualCallback;
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
 * Use it in the case you want to create a special deferred executing a single callback.
 *
 * @author Danilo Reinert
 */
class CallbackDeferred implements Deferred<Response>, DeferredPool<Response>, PollingRequest<Response> {

    private final DualCallback callback;
    private final SerializedRequest serializedRequest;
    private ResponseCallback resolveCallback;
    private Boolean resolved;
    private HttpConnection connection;

    protected CallbackDeferred(DualCallback callback, SerializedRequest serializedRequest) {
        this.callback = callback;
        this.serializedRequest = serializedRequest;
    }

    public void onResolve(ResponseCallback responseCallback) {
        resolveCallback = responseCallback;
    }

    @Override
    public boolean isPending() {
        return resolved == null;
    }

    @Override
    public boolean isRejected() {
        return resolved == Boolean.FALSE;
    }

    @Override
    public boolean isResolved() {
        return resolved == Boolean.TRUE;
    }

    @Override
    public void resolve(Response response) {
        resolved = Boolean.TRUE;
        if (resolveCallback != null) {
            try {
                resolveCallback.execute(response);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        callback.onLoad(response);
    }

    @Override
    public void reject(RequestException exception) {
        resolved = Boolean.FALSE;
        callback.onError(exception);
    }

    @Override
    public void notifyDownload(ReadProgress progress) {
    }

    @Override
    public void notifyUpload(WriteProgress progress) {
    }

    @Override
    public void notifyResponse(RawResponse response) {
    }

    @Override
    public void setHttpConnection(HttpConnection connection) {
        this.connection = connection;
    }

    @Override
    public void setRequestRetrier(RequestRetrier retrier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Deferred<Response> getDeferred() {
        return this;
    }

    @Override
    public PollingRequest<Response> getRequest() {
        return this;
    }

    @Override
    public RequestException getRejectResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response getResolveResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRunner.Lock getResponseHeaderLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRunner.Lock getResponseBodyLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRunner.Lock getResponseLock() {
        throw new UnsupportedOperationException();
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

    @Override
    public <O> O retrieve(String key) {
        return serializedRequest.retrieve(key);
    }

    @Override
    public Data getData(String key) {
        return serializedRequest.getData(key);
    }

    @Override
    public PollingRequest<Response> save(String key, Object value) {
        serializedRequest.save(key, value);
        return this;
    }

    @Override
    public PollingRequest<Response> save(String key, Object value, Store.Level level) {
        serializedRequest.save(key, value, level);
        return this;
    }

    @Override
    public PollingRequest<Response> save(String key, Object value, long ttl, Level level) {
        serializedRequest.save(key, value, ttl, level);
        return this;
    }

    @Override
    public PollingRequest<Response> save(String key, Object value, long ttl) {
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
    public void clear() {
        serializedRequest.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        serializedRequest.clear(fireRemovedEvent);
    }

    @Override
    public PollingRequest<Response> onSaved(String key, Handler handler) {
        serializedRequest.onSaved(key, handler);
        return this;
    }

    @Override
    public PollingRequest<Response> onRemoved(String key, Handler handler) {
        serializedRequest.onRemoved(key, handler);
        return this;
    }

    @Override
    public PollingRequest<Response> onExpired(String key, Handler handler) {
        serializedRequest.onExpired(key, handler);
        return this;
    }

    //===================================================================
    // Request
    //===================================================================

    @Override
    public Response await() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<IncomingResponse> getResponse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpConnection getHttpConnection() {
        return connection;
    }

    @Override
    public int getRetryCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onAbort(ExceptionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onAbort(ExceptionRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onCancel(ExceptionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onCancel(ExceptionRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onError(VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onError(ExceptionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onError(ExceptionRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onLoad(VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onLoad(ResponseCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onLoad(ResponseRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onFail(VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onFail(ResponseCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onFail(ResponseRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onRead(ReadCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(int statusCode, VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(int statusCode, ResponseCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(int statusCode, ResponseRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(Status status, VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(Status status, ResponseCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(Status status, ResponseRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(StatusFamily family, VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(StatusFamily family, ResponseCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onStatus(StatusFamily family, ResponseRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onSuccess(VoidCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onSuccess(PayloadCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onSuccess(PayloadResponseCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onSuccess(PayloadResponseRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onTimeout(TimeoutCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onTimeout(TimeoutRequestCallback<Response> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PollingRequest<Response> onWrite(WriteCallback callback) {
        throw new UnsupportedOperationException();
    }
}
