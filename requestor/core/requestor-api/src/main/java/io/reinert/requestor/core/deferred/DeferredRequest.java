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
import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.Request;
import io.reinert.requestor.core.RequestAbortException;
import io.reinert.requestor.core.RequestCancelException;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestRetrier;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.WriteProgress;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;
import io.reinert.requestor.core.callback.VoidCallback;
import io.reinert.requestor.core.callback.WriteCallback;

/**
 * Default Deferred implementation.
 *
 * @param <T> Expected type in Request#done.
 *
 * @author Danilo Reinert
 */
public class DeferredRequest<T> implements Deferred<T> {

    private static final Logger logger = Logger.getLogger(DeferredRequest.class.getName());

    private final PollingRequest<T> request;
    private final DeferredObject<Response, RequestException, WriteProgress> deferred;
    private HttpConnection connection;
    private RequestRetrier retrier;
    private boolean noAbortCallbackRegistered = true;
    private boolean noCancelCallbackRegistered = true;
    private boolean noErrorCallbackRegistered = true;
    private boolean noTimeoutCallbackRegistered = true;

    protected DeferredRequest(PollingRequest<T> request) {
        this.request = request;
        this.deferred = new DeferredObject<Response, RequestException, WriteProgress>();
    }

    private DeferredRequest(PollingRequest<T> request,
                            DeferredObject<Response, RequestException, WriteProgress> deferredObject,
                            boolean noAbortCallbackRegistered,
                            boolean noCancelCallbackRegistered,
                            boolean noErrorCallbackRegistered,
                            boolean noTimeoutCallbackRegistered) {
        this.request = request;
        this.deferred = deferredObject;
        this.noAbortCallbackRegistered = noAbortCallbackRegistered;
        this.noCancelCallbackRegistered = noCancelCallbackRegistered;
        this.noErrorCallbackRegistered = noErrorCallbackRegistered;
        this.noTimeoutCallbackRegistered = noTimeoutCallbackRegistered;
    }

    //===================================================================
    // Request
    //===================================================================

    public HttpConnection getHttpConnection() {
        return connection;
    }

    public int getRetryCount() {
        if (retrier == null) return 0;
        return retrier.getRetryCount();
    }

    public DeferredRequest<T> onAbort(final ExceptionCallback callback) {
        noAbortCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestAbortException) {
                    callback.execute(e);
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onAbort(final ExceptionRequestCallback<T> callback) {
        noAbortCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestAbortException) {
                    callback.execute(e, request);
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onCancel(final ExceptionCallback callback) {
        noCancelCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestCancelException) {
                    callback.execute(e);
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onCancel(final ExceptionRequestCallback<T> callback) {
        noCancelCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestCancelException) {
                    callback.execute(e, request);
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onError(final VoidCallback callback) {
        noErrorCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                callback.execute();
            }
        });
        return this;
    }

    public DeferredRequest<T> onError(final ExceptionCallback callback) {
        noErrorCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                callback.execute(e);
            }
        });
        return this;
    }

    public DeferredRequest<T> onError(final ExceptionRequestCallback<T> callback) {
        noErrorCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                callback.execute(e, request);
            }
        });
        return this;
    }

    public DeferredRequest<T> onLoad(final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                callback.execute();
            }
        });
        return this;
    }

    public DeferredRequest<T> onLoad(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                callback.execute(response);
            }
        });
        return this;
    }

    public DeferredRequest<T> onLoad(final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                callback.execute(response, request);
            }
        });
        return this;
    }

    public DeferredRequest<T> onFail(final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) callback.execute();
            }
        });
        return this;
    }

    public DeferredRequest<T> onFail(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) callback.execute(response);
            }
        });
        return this;
    }

    public DeferredRequest<T> onFail(final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) callback.execute(response, request);
            }
        });
        return this;
    }

    public DeferredRequest<T> onProgress(final WriteCallback callback) {
        deferred.progress(new ProgressCallback<WriteProgress>() {
            public void onProgress(WriteProgress progress) {
                callback.execute(progress);
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final int statusCode, final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode) callback.execute();
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final int statusCode, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode) callback.execute(response);
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final int statusCode, final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode) callback.execute(response, request);
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final Status status, final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode()) callback.execute();
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final Status status, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode()) callback.execute(response);
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final Status status, final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode()) callback.execute(response, request);
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final StatusFamily family, final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family) callback.execute();
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final StatusFamily family, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family) callback.execute(response);
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final StatusFamily family, final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family) callback.execute(response, request);
            }
        });
        return this;
    }

    public DeferredRequest<T> onSuccess(final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) callback.execute();
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E extends T> DeferredRequest<T> onSuccess(final PayloadCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute(response.getPayload().<E>asObject());
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().asObject().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    }
                }
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E extends T> DeferredRequest<T> onSuccess(final PayloadResponseCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute(response.getPayload().<E>asObject(), response);
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().asObject().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    }
                }
            }
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E extends T> DeferredRequest<T> onSuccess(final PayloadResponseRequestCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute(response.getPayload().<E>asObject(), response, (PollingRequest<E>) request);
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().asObject().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onTimeout(final TimeoutCallback callback) {
        noTimeoutCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestTimeoutException) {
                    callback.execute((RequestTimeoutException) e);
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onTimeout(final TimeoutRequestCallback<T> callback) {
        noTimeoutCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                if (e instanceof RequestTimeoutException) {
                    callback.execute((RequestTimeoutException) e, request);
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onUpProgress(final WriteCallback callback) {
        deferred.upProgress(new ProgressCallback<WriteProgress>() {
            public void onProgress(WriteProgress progress) {
                callback.execute(progress);
            }
        });
        return this;
    }

    //===================================================================
    // Deferred
    //===================================================================

    @Override
    public boolean isPending() {
        return deferred.isPending();
    }

    @Override
    public boolean isRejected() {
        return deferred.isRejected();
    }

    @Override
    public boolean isResolved() {
        return deferred.isResolved();
    }

    @Override
    public void resolve(final Response response) {
        if (retrier != null && retrier.maybeRetry(response)) return;

        deferred.resolve(response);
    }

    @Override
    public void reject(RequestException e) {
        if (retrier != null && retrier.maybeRetry(e)) return;

        if (noErrorCallbackRegistered) {
            if (e instanceof RequestTimeoutException) {
                if (noTimeoutCallbackRegistered) throw e;
            } else if (e instanceof RequestCancelException) {
                if (noCancelCallbackRegistered) throw e;
            } else if (e instanceof RequestAbortException) {
                if (noAbortCallbackRegistered) throw e;
            } else {
                throw e;
            }
        }

        deferred.reject(e);
    }

    @Override
    public void notifyDownload(WriteProgress progress) {
        deferred.notifyDownload(progress);
    }

    @Override
    public void notifyUpload(WriteProgress progress) {
        deferred.notifyUpload(progress);
    }

    @Override
    public void setHttpConnection(HttpConnection connection) {
        this.connection = connection;
    }

    @Override
    public void setRequestRetrier(RequestRetrier retrier) {
        this.retrier = retrier;
    }

    @Override
    public Request<T> getRequest() {
        return request;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }

    protected DeferredRequest<T> replicate() {
        return new DeferredRequest<T>(request, deferred.replicate(), noAbortCallbackRegistered,
                noCancelCallbackRegistered, noErrorCallbackRegistered, noTimeoutCallbackRegistered);
    }
}
