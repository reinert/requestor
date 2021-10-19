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

import com.google.gwt.core.client.Callback;

/**
 * Use it in the case you want to create a special deferred executing a single callback.
 *
 * @param <T> Type of the resolved object
 *
 * @author Danilo Reinert
 */
class CallbackDeferred<T> implements Deferred<T> {

    private final Callback<T, Throwable> callback;

    protected CallbackDeferred(Callback<T, Throwable> callback) {
        this.callback = callback;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resolve(Response response) {
        try {
            callback.onSuccess((T) response.getPayload());
        } catch (ClassCastException e) {
            throw new IncompatibleTypeException("Cannot cast " +
                    response.getPayload().getClass().getName() + " to " +
                    response.getPayloadType().getType().getName() + ".", e);
        }
    }

    @Override
    public void reject(RequestException error) {
        callback.onFailure(error);
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
    public HttpConnection getHttpConnection() {
        return null;
    }

    @Override
    public Promise<T> getPromise() {
        return null;
    }

    @Override
    public Deferred<T> getUnresolvedCopy() {
        return new CallbackDeferred<T>(callback);
    }
}
