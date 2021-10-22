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
package io.reinert.requestor.impl.gdeferred;

import java.util.ArrayList;
import java.util.logging.Level;

import io.reinert.gdeferred.impl.DeferredObject;
import io.reinert.requestor.Deferred;
import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.Promise;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.Response;

/**
 * DeferredRequest implementation of GDeferred.
 *
 * @param <T> Expected type in {@link Promise#done(io.reinert.gdeferred.DoneCallback)}.
 */
public class GDeferredRequest<T> extends DeferredObject<T, Throwable, RequestProgress>
        implements Deferred<T>, Promise<T> {

    private HttpConnection connection;
    private ArrayList<io.reinert.gdeferred.ProgressCallback<RequestProgress>> uploadProgressCallbacks;

    public GDeferredRequest() {
    }

    //===================================================================
    // Promise
    //===================================================================

    @Override
    public Promise<T> always(io.reinert.gdeferred.AlwaysCallback<T, Throwable> callback) {
        super.always(callback);
        return this;
    }

    @Override
    public Promise<T> done(io.reinert.gdeferred.DoneCallback<T> callback) {
        super.done(callback);
        return this;
    }

    @Override
    public Promise<T> fail(io.reinert.gdeferred.FailCallback<Throwable> callback) {
        super.fail(callback);
        return this;
    }

    @Override
    public Promise<T> progress(io.reinert.gdeferred.ProgressCallback<RequestProgress> callback) {
        super.progress(callback);
        return this;
    }

    @Override
    public Promise<T> upProgress(io.reinert.gdeferred.ProgressCallback<RequestProgress> callback) {
        getUploadProgressCallbacks().add(callback);
        return this;
    }

    @Override
    public Promise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback) {
        super.then(doneCallback);
        return this;
    }

    @Override
    public Promise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                                  io.reinert.gdeferred.FailCallback<Throwable> failCallback) {
        super.then(doneCallback, failCallback);
        return this;
    }

    @Override
    public Promise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                                  io.reinert.gdeferred.FailCallback<Throwable> failCallback,
                                  io.reinert.gdeferred.ProgressCallback<RequestProgress> progressCallback) {
        super.then(doneCallback, failCallback, progressCallback);
        return this;
    }

    @Override
    public Promise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                                  io.reinert.gdeferred.FailCallback<Throwable> failCallback,
                                  io.reinert.gdeferred.ProgressCallback<RequestProgress> progressCallback,
                                  io.reinert.gdeferred.ProgressCallback<RequestProgress> upProgressCallback) {
        super.then(doneCallback, failCallback, progressCallback);
        this.upProgress(upProgressCallback);
        return this;
    }

    //===================================================================
    // Requestor Deferred
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

    @Override
    public HttpConnection getHttpConnection() {
        return this.connection;
    }

    @Override
    public Promise<T> getPromise() {
        return this;
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
