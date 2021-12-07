/*
 * Copyright 2014-2021 Danilo Reinert
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

import io.reinert.requestor.core.callback.DualCallback;
import io.reinert.requestor.core.callback.ResponseCallback;

/**
 * Use it in the case you want to create a special deferred executing a single callback.
 *
 * @author Danilo Reinert
 */
class CallbackDeferred implements Deferred<Response>, DeferredPool<Response> {

    private final DualCallback callback;
    private ResponseCallback resolveCallback;
    private Boolean resolved;

    protected CallbackDeferred(DualCallback callback) {
        this.callback = callback;
    }

    private CallbackDeferred(DualCallback callback, ResponseCallback resolveCallback) {
        this.callback = callback;
        this.resolveCallback = resolveCallback;
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
        if (resolveCallback != null) resolveCallback.execute(response);
        callback.onLoad(response);
    }

    @Override
    public void reject(RequestException exception) {
        resolved = Boolean.FALSE;
        callback.onError(exception);
    }

    @Override
    public void notifyDownload(RequestProgress progress) {
    }

    @Override
    public void notifyUpload(RequestProgress progress) {
    }

    @Override
    public void setHttpConnection(HttpConnection connection) {
    }

    @Override
    public void setRequestRetrier(RequestRetrier retrier) {
    }

    @Override
    public Deferred<Response> newDeferred() {
        return this;
    }

    @Override
    public PollingRequest<Response> getRequest() {
        return null;
    }
}
