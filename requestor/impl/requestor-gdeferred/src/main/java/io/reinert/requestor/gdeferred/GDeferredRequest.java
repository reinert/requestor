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
package io.reinert.requestor.gdeferred;

import java.util.ArrayList;
import java.util.logging.Level;

import io.reinert.gdeferred.FailCallback;
import io.reinert.gdeferred.impl.DeferredObject;
import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.Response;
import io.reinert.requestor.deferred.Callback;
import io.reinert.requestor.deferred.Deferred;
import io.reinert.requestor.deferred.Promise;

/**
 * DeferredRequest implementation of GDeferred.
 *
 * @param <T> Expected type in {@link RequestPromise#done(io.reinert.gdeferred.DoneCallback)}.
 */
public class GDeferredRequest<T> extends DeferredObject<T, Throwable, RequestProgress>
        implements GDeferredRequestPromise<T>, Deferred<T> {

    private HttpConnection connection;
    private ArrayList<io.reinert.gdeferred.ProgressCallback<RequestProgress>> uploadProgressCallbacks;

    public GDeferredRequest() {
    }

    //===================================================================
    // RequestPromise
    //===================================================================

    @Override
    public RequestPromise<T> always(io.reinert.gdeferred.AlwaysCallback<T, Throwable> callback) {
        super.always(callback);
        return this;
    }

    @Override
    public RequestPromise<T> done(io.reinert.gdeferred.DoneCallback<T> callback) {
        super.done(callback);
        return this;
    }

    @Override
    public RequestPromise<T> fail(io.reinert.gdeferred.FailCallback<Throwable> callback) {
        super.fail(callback);
        return this;
    }

    @Override
    public RequestPromise<T> progress(io.reinert.gdeferred.ProgressCallback<RequestProgress> callback) {
        super.progress(callback);
        return this;
    }

    @Override
    public RequestPromise<T> upProgress(io.reinert.gdeferred.ProgressCallback<RequestProgress> callback) {
        getUploadProgressCallbacks().add(callback);
        return this;
    }

    @Override
    public RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback) {
        super.then(doneCallback);
        return this;
    }

    @Override
    public RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                                  io.reinert.gdeferred.FailCallback<Throwable> failCallback) {
        super.then(doneCallback, failCallback);
        return this;
    }

    @Override
    public RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                                  io.reinert.gdeferred.FailCallback<Throwable> failCallback,
                                  io.reinert.gdeferred.ProgressCallback<RequestProgress> progressCallback) {
        super.then(doneCallback, failCallback, progressCallback);
        return this;
    }

    @Override
    public RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                                  io.reinert.gdeferred.FailCallback<Throwable> failCallback,
                                  io.reinert.gdeferred.ProgressCallback<RequestProgress> progressCallback,
                                  io.reinert.gdeferred.ProgressCallback<RequestProgress> upProgressCallback) {
        super.then(doneCallback, failCallback, progressCallback);
        this.upProgress(upProgressCallback);
        return this;
    }

    //===================================================================
    // Promise
    //===================================================================

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
    public <R> io.reinert.requestor.deferred.Promise<R> then(final Callback<T, R> onFulfilled) {
        // FIXME: It's ignoring the callback return
        done(new io.reinert.gdeferred.DoneCallback<T>() {
            @Override
            public void onDone(T result) {
                onFulfilled.call(result);
            }
        });
        return (Promise<R>) this;
    }

    @Override
    public <R> io.reinert.requestor.deferred.Promise<R> then(final Callback<T, R> onFulfilled,
                                                             final Callback<Throwable, R> onRejected) {
        // FIXME: It's ignoring the callback return
        done(new io.reinert.gdeferred.DoneCallback<T>() {
            @Override
            public void onDone(T result) {
                onFulfilled.call(result);
            }
        });
        fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                onRejected.call(result);
            }
        });
        return (Promise<R>) this;
    }

    //===================================================================
    // Deferred
    //===================================================================

    @Override
    public void resolve(final Response<T> response) {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object already finished, cannot resolve again");
        }

        state = State.RESOLVED;
        resolveResult = response.getPayload();

        try {
            triggerDone(response);
        } finally {
            triggerAlways(resolveResult, null);
        }
    }

    @Override
    public void reject(RequestException error) {
        // If the http connection is still opened, then close it
        if (connection != null && connection.isPending())
            connection.cancel();
        super.reject(error);
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

    //===================================================================
    // Internal methods
    //===================================================================

    @SuppressWarnings("unchecked")
    protected void triggerDone(Response<T> resolved) {
        for (io.reinert.gdeferred.DoneCallback<T> callback : getDoneCallbacks()) {
            try {
                if (callback instanceof DoneCallback) {
                    ((DoneCallback) callback).onDone(resolved);
                } else {
                    callback.onDone(resolved.getPayload());
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a DoneCallback", e);
            }
        }
    }

    protected void triggerUploadProgress(RequestProgress progress) {
        for (io.reinert.gdeferred.ProgressCallback<RequestProgress> callback : getUploadProgressCallbacks()) {
            try {
                triggerProgress(callback, progress);
            } catch (Exception e) {
                log.log(Level.SEVERE, "An uncaught exception occurred in a ProgressCallback", e);
            }
        }
    }

    protected ArrayList<io.reinert.gdeferred.ProgressCallback<RequestProgress>> getUploadProgressCallbacks() {
        if (uploadProgressCallbacks == null)
            uploadProgressCallbacks = new ArrayList<io.reinert.gdeferred.ProgressCallback<RequestProgress>>();
        return uploadProgressCallbacks;
    }
}
