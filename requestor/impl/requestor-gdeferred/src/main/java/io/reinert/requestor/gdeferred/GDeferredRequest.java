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
package io.reinert.requestor.gdeferred;

import java.util.ArrayList;
import java.util.logging.Level;

import io.reinert.gdeferred.ProgressCallback;
import io.reinert.gdeferred.Promise;
import io.reinert.gdeferred.impl.DeferredObject;
import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.deferred.Callback;
import io.reinert.requestor.deferred.Deferred;

/**
 * DeferredRequest implementation of GDeferred.
 *
 * @param <T> Expected type in {@link RequestPromise#done(io.reinert.gdeferred.DoneCallback)}.
 */
public class GDeferredRequest<T> extends DeferredObject<T, Throwable, RequestProgress>
        implements GDeferredRequestPromise<T>, Deferred<T> {

    private HttpConnection connection;
    private ArrayList<ProgressCallback<RequestProgress>> uploadProgressCallbacks;

    public GDeferredRequest() {
    }

    @Override
    public void resolvePromise(T result) {
        super.resolve(result);
    }

    @Override
    public void rejectPromise(RequestException error) {
        // If the http connection is still opened, then close it
        if (connection != null && connection.isPending())
            connection.cancel();
        super.reject(error);
    }

    @Override
    public Promise<T, Throwable, RequestProgress> upProgress(ProgressCallback<RequestProgress> callback) {
        getUploadProgressCallbacks().add(callback);
        return this;
    }

    @Override
    public boolean isFulfilled() {
        return isResolved();
    }

    @Override
    public <R> io.reinert.requestor.deferred.Promise<R> downProgress(Callback<RequestProgress, R> onProgress) {
        // Not used
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> io.reinert.requestor.deferred.Promise<R> upProgress(Callback<RequestProgress, R> onProgress) {
        // Not used
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> io.reinert.requestor.deferred.Promise<R> then(Callback<T, R> onFulfilled) {
        // Not used
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> io.reinert.requestor.deferred.Promise<R> then(Callback<T, R> onFulfilled,
                                                             Callback<Throwable, R> onRejected) {
        // Not used
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyDownload(RequestProgress progress) {
        notify(progress);
    }

    @Override
    public void notifyUpload(RequestProgress progress) {
        if (!isPending())
            throw new IllegalStateException("Deferred object already finished, cannot notify upload progress");
        triggerUploadProgress(progress);
    }

    @Override
    public void setHttpConnection(HttpConnection connection) {
        this.connection = connection;
    }

    protected void triggerUploadProgress(RequestProgress progress) {
        for (ProgressCallback<RequestProgress> callback : getUploadProgressCallbacks()) {
            try {
                triggerProgress(callback, progress);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a ProgressCallback", e);
            }
        }
    }

    protected ArrayList<ProgressCallback<RequestProgress>> getUploadProgressCallbacks() {
        if (uploadProgressCallbacks == null)
            uploadProgressCallbacks = new ArrayList<ProgressCallback<RequestProgress>>();
        return uploadProgressCallbacks;
    }
}
