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

import java.util.logging.Logger;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.IncompatibleTypeException;
import io.reinert.requestor.core.MutableSerializedRequest;
import io.reinert.requestor.core.Request;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestProgress;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.Store;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;

/**
 * Default Deferred implementation.
 *
 * @param <T> Expected type in Request#done.
 */
public class DeferredRequest<T> implements Deferred<T>, Request<T> {

    private static final Logger logger = Logger.getLogger(DeferredRequest.class.getName());

    private final MutableSerializedRequest request;
    private final DeferredObject<Response, RequestException, RequestProgress> deferred;
    private HttpConnection connection;

    public DeferredRequest(MutableSerializedRequest request) {
        this(request, new DeferredObject<Response, RequestException, RequestProgress>());
    }

    protected DeferredRequest(MutableSerializedRequest request,
                              DeferredObject<Response, RequestException, RequestProgress> deferred) {
        this.request = request;
        this.deferred = deferred;
    }

    //===================================================================
    // Request
    //===================================================================

    @Override
    public Store getStore() {
        return request.getStore();
    }

    @Override
    public Request<T> onAbort(final ExceptionCallback callback) {
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                callback.execute(e);
            }
        });
        return this;
    }

    @Override
    public Request<T> onLoad(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Request<T> onFail(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Request<T> onProgress(final io.reinert.requestor.core.callback.ProgressCallback callback) {
        deferred.progress(new ProgressCallback<RequestProgress>() {
            public void onProgress(RequestProgress progress) {
                callback.execute(progress);
            }
        });
        return this;
    }

    @Override
    public Request<T> onStatus(final int statusCode, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode)
                    callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Request<T> onStatus(final Status status, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode())
                    callback.execute(response);
            }
        });
        return this;
    }

    @Override
    public Request<T> onStatus(final StatusFamily family, final ResponseCallback callback) {
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
    public <E extends T> Request<T> onSuccess(final PayloadCallback<E> callback) {
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
    public <E extends T> Request<T> onSuccess(final PayloadResponseCallback<E> callback) {
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
    public Request<T> onUpProgress(final io.reinert.requestor.core.callback.ProgressCallback callback) {
        deferred.upProgress(new ProgressCallback<RequestProgress>() {
            public void onProgress(RequestProgress progress) {
                callback.execute(progress);
            }
        });
        return this;
    }

    @Override
    public Request<T> onTimeout(final TimeoutCallback callback) {
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
        deferred.notifyDownload(progress);
    }

    @Override
    public void notifyUpload(RequestProgress progress) {
        deferred.notifyUpload(progress);
    }

    @Override
    public void setHttpConnection(HttpConnection connection) {
        this.connection = connection;
    }

    @Override
    public HttpConnection getHttpConnection() {
        return connection;
    }

    @Override
    public Request<T> getRequest() {
        return this;
    }

    @Override
    public Deferred<T> getUnresolvedCopy() {
        return new DeferredRequest<T>(request, deferred.getUnresolvedCopy());
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected MutableSerializedRequest getSerializedRequest() {
        return request;
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }
}
