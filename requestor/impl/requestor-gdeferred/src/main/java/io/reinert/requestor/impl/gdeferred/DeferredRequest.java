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
package io.reinert.requestor.impl.gdeferred;

import java.util.ArrayList;
import java.util.logging.Logger;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.FailCallback;
import io.reinert.gdeferred.impl.DeferredObject;
import io.reinert.requestor.Deferred;
import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.Promise;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.RequestTimeoutException;
import io.reinert.requestor.Response;
import io.reinert.requestor.Status;
import io.reinert.requestor.StatusFamily;
import io.reinert.requestor.callbacks.ExceptionCallback;
import io.reinert.requestor.callbacks.PayloadCallback;
import io.reinert.requestor.callbacks.PayloadResponseCallback;
import io.reinert.requestor.callbacks.ProgressCallback;
import io.reinert.requestor.callbacks.ResponseCallback;
import io.reinert.requestor.callbacks.TimeoutCallback;

/**
 * DeferredRequest implementation of GDeferred.
 *
 * @param <T> Expected type in Promise#done.
 */
public class DeferredRequest<T> implements Deferred<T>, Promise<T> {

    private static Logger LOGGER = Logger.getLogger(DeferredRequest.class.getName());

    private final DeferredObject<Response<Object>, RequestException, RequestProgress> deferred =
            new DeferredObject<Response<Object>, RequestException, RequestProgress>();
    private HttpConnection connection;
    private ArrayList<ProgressCallback> uploadProgressCallbacks;

    public DeferredRequest() {
    }

    //===================================================================
    // Promise
    //===================================================================

    @Override
    public Promise<T> abort(final ExceptionCallback callback) {
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                callback.execute(e);
            }
        });
        return this;
    }

    @Override
    public Promise<T> load(final ResponseCallback<Object> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> fail(final ResponseCallback<Object> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                if (!isSuccessful(response)) callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> progress(final ProgressCallback callback) {
        deferred.progress(new io.reinert.gdeferred.ProgressCallback<RequestProgress>() {
            public void onProgress(RequestProgress progress) {
                callback.execute(progress);
            }
        });
        return this;
    }

    @Override
    public Promise<T> status(final int statusCode, final ResponseCallback<Object> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                if (response.getStatusCode() == statusCode)
                    callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> status(final Status status, final ResponseCallback<Object> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                if (response.getStatusCode() == status.getStatusCode())
                    callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> status(final StatusFamily family, final ResponseCallback<Object> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                if (StatusFamily.of(response.getStatusCode()) == family)
                    callback.execute(response);
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> Promise<T> success(final PayloadCallback<E> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                if (isSuccessful(response)) callback.execute((E) response.getPayload());
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> Promise<T> success(final PayloadResponseCallback<E> callback) {
        deferred.done(new DoneCallback<Response<Object>>() {
            public void onDone(Response<Object> response) {
                if (isSuccessful(response)) {
                    Response<E> r = (Response<E>) ((Response<T>) response);
                    callback.execute(r.getPayload(), r);
                }
            }
        });
        return this;
    }

    @Override
    public Promise<T> upProgress(ProgressCallback callback) {
        if (uploadProgressCallbacks == null)
            uploadProgressCallbacks = new ArrayList<ProgressCallback>();

        uploadProgressCallbacks.add(callback);
        return this;
    }

    @Override
    public Promise<T> timeout(final TimeoutCallback callback) {
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestTimeoutException) {
                    RequestTimeoutException timeoutException = (RequestTimeoutException) e;
                    callback.execute(timeoutException);
                }
            }
        });
        return this;
    }

    //===================================================================
    // Requestor Deferred
    //===================================================================

    @SuppressWarnings("unchecked")
    @Override
    public void resolve(final Response<T> response) {
        deferred.resolve((Response<Object>) response);
    }

    @Override
    public void reject(RequestException error) {
        // If the http connection is still opened, then close it
        if (connection != null && connection.isPending())
            connection.cancel();

        deferred.reject(error);
    }

    @Override
    public void notifyDownload(RequestProgress progress) {
        deferred.notify(progress);
    }

    @Override
    public void notifyUpload(RequestProgress progress) {
        if (!deferred.isPending())
            throw new IllegalStateException("Request already finished, cannot notify upload progress");

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

    private void triggerUploadProgress(RequestProgress progress) {
        if (uploadProgressCallbacks == null)
            return;

        for (ProgressCallback callback : uploadProgressCallbacks)
            callback.execute(progress);
    }

    private boolean isSuccessful(Response<Object> response) {
        return response.getStatusCode() / 100 == 2;
    }
}
