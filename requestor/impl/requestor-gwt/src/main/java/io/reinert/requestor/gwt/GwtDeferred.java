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
package io.reinert.requestor.gwt;

import java.util.ArrayList;
import java.util.logging.Logger;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.FailCallback;
import io.reinert.gdeferred.impl.RequestorDeferred;
import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.IncompatibleTypeException;
import io.reinert.requestor.core.Promise;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestProgress;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.ProgressCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;

/**
 * DeferredRequest implementation using GDeferred.
 *
 * @param <T> Expected type in Promise#done.
 */
public class GwtDeferred<T> implements Deferred<T>, Promise<T> {

    private static final Logger logger = Logger.getLogger(GwtDeferred.class.getName());
    private static DeferredRequestFactory factory;

    private final RequestorDeferred<Response, RequestException, RequestProgress> deferred;
    private HttpConnection connection;
    private ArrayList<ProgressCallback> uploadProgressCallbacks;

    public GwtDeferred() {
        this(new RequestorDeferred<Response, RequestException, RequestProgress>());
    }

    protected GwtDeferred(RequestorDeferred<Response, RequestException, RequestProgress> deferred) {
        this.deferred = deferred;
    }

    public static Deferred.Factory getFactory() {
        if (factory == null) factory = new DeferredRequestFactory();
        return factory;
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
    public Promise<T> load(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> fail(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
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
    public Promise<T> status(final int statusCode, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode)
                    callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> status(final Status status, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode())
                    callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Promise<T> status(final StatusFamily family, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family)
                    callback.execute(response);
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> Promise<T> success(final PayloadCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute((E) response.getPayload());
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    }
                }
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> Promise<T> success(final PayloadResponseCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute((E) response.getPayload(), response);
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    }
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
    public void resolve(final Response response) {
        deferred.resolve(response);
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

    @Override
    public Deferred<T> getUnresolvedCopy() {
        GwtDeferred<T> copy = new GwtDeferred<T>(this.deferred.getUnresolvedCopy());
        if (this.uploadProgressCallbacks != null) {
            copy.uploadProgressCallbacks = new ArrayList<ProgressCallback>();
            copy.uploadProgressCallbacks.addAll(this.uploadProgressCallbacks);
        }
        return copy;
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

    private boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }
}
