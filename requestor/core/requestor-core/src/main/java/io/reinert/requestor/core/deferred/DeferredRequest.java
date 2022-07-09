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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reinert.requestor.core.AsyncRunner;
import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.HttpConnection;
import io.reinert.requestor.core.IncomingResponse;
import io.reinert.requestor.core.IncompatibleTypeException;
import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.ReadProgress;
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
import io.reinert.requestor.core.callback.ReadCallback;
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

    private final DeferredPollingRequest<T> request;
    private final AsyncRunner asyncRunner;
    private final DeferredObject<Response, RequestException, ReadProgress, WriteProgress> deferred;
    private final AsyncRunner.Lock responseHeaderLock;
    private final AsyncRunner.Lock responseBodyLock;
    private final AsyncRunner.Lock responseLock;
    private HttpConnection connection;
    private RequestRetrier retrier;
    private boolean noAbortCallbackRegistered = true;
    private boolean noCancelCallbackRegistered = true;
    private boolean noErrorCallbackRegistered = true;
    private boolean noTimeoutCallbackRegistered = true;
    private RawResponse rawResponse;

    protected DeferredRequest(DeferredPollingRequest<T> request, AsyncRunner asyncRunner) {
        this.request = request;
        this.asyncRunner = asyncRunner;
        this.deferred = new DeferredObject<Response, RequestException, ReadProgress, WriteProgress>();
        responseHeaderLock = asyncRunner.getLock();
        responseBodyLock = asyncRunner.getLock();
        responseLock = asyncRunner.getLock();
    }

    private DeferredRequest(DeferredPollingRequest<T> request,
                            AsyncRunner asyncRunner,
                            DeferredObject<Response, RequestException, ReadProgress, WriteProgress> deferredObject,
                            boolean noAbortCallbackRegistered,
                            boolean noCancelCallbackRegistered,
                            boolean noErrorCallbackRegistered,
                            boolean noTimeoutCallbackRegistered) {
        this.request = request;
        this.asyncRunner = asyncRunner;
        this.deferred = deferredObject;
        responseHeaderLock = asyncRunner.getLock();
        responseBodyLock = asyncRunner.getLock();
        responseLock = asyncRunner.getLock();
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
                    try {
                        callback.execute(e);
                    } catch (Throwable ex) {
                        e.printStackTrace();
                    }
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
                    try {
                        callback.execute(e, request);
                    } catch (Throwable ex) {
                        e.printStackTrace();
                    }
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
                    try {
                        callback.execute(e);
                    } catch (Throwable ex) {
                        e.printStackTrace();
                    }
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
                    try {
                        callback.execute(e, request);
                    } catch (Throwable ex) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onError(final VoidCallback callback) {
        noErrorCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                try {
                    callback.execute();
                } catch (Throwable ex) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onError(final ExceptionCallback callback) {
        noErrorCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                try {
                    callback.execute(e);
                } catch (Throwable ex) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onError(final ExceptionRequestCallback<T> callback) {
        noErrorCallbackRegistered = false;
        deferred.fail(new FailCallback<RequestException>() {
            public void onFail(RequestException e) {
                try {
                    callback.execute(e, request);
                } catch (Throwable ex) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onLoad(final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                try {
                    callback.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onLoad(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                try {
                    callback.execute(response);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onLoad(final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                try {
                    callback.execute(response, request);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onFail(final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) {
                    try {
                        callback.execute();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onFail(final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) {
                    try {
                        callback.execute(response);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onFail(final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (!isSuccessful(response)) {
                    try {
                        callback.execute(response, request);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onProgress(final ReadCallback callback) {
        deferred.progress(new ProgressCallback<ReadProgress>() {
            @Override
            public void onProgress(ReadProgress progress) {
                try {
                    callback.execute(progress);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final int statusCode, final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode) {
                    try {
                        callback.execute();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final int statusCode, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode) {
                    try {
                        callback.execute(response);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final int statusCode, final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == statusCode) {
                    try {
                        callback.execute(response, request);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final Status status, final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode()) {
                    try {
                        callback.execute();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final Status status, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode()) {
                    try {
                        callback.execute(response);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final Status status, final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (response.getStatusCode() == status.getStatusCode()) {
                    try {
                        callback.execute(response, request);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final StatusFamily family, final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family) {
                    try {
                        callback.execute();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final StatusFamily family, final ResponseCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family) {
                    try {
                        callback.execute(response);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onStatus(final StatusFamily family, final ResponseRequestCallback<T> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (StatusFamily.of(response.getStatusCode()) == family) {
                    try {
                        callback.execute(response, request);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onSuccess(final VoidCallback callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public <E extends T> DeferredRequest<T> onSuccess(final PayloadCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute(response.<E>getPayload());
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public <E extends T> DeferredRequest<T> onSuccess(final PayloadResponseCallback<E> callback) {
        deferred.done(new DoneCallback<Response>() {
            public void onDone(Response response) {
                if (isSuccessful(response)) {
                    try {
                        callback.execute(response.<E>getPayload(), response);
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    } catch (Throwable e) {
                        e.printStackTrace();
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
                        callback.execute(response.<E>getPayload(), response, (PollingRequest<E>) request);
                    } catch (ClassCastException e) {
                        throw new IncompatibleTypeException("Cannot cast " +
                                response.getPayload().getClass().getName() + " to " +
                                response.getPayloadType().getType().getName() + ".", e);
                    } catch (Throwable e) {
                        e.printStackTrace();
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
                    try {
                        callback.execute((RequestTimeoutException) e);
                    } catch (Throwable ex) {
                        e.printStackTrace();
                    }
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
                    try {
                        callback.execute((RequestTimeoutException) e, request);
                    } catch (Throwable ex) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return this;
    }

    public DeferredRequest<T> onUpProgress(final WriteCallback callback) {
        deferred.upProgress(new ProgressCallback<WriteProgress>() {
            public void onProgress(WriteProgress progress) {
                try {
                    callback.execute(progress);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
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

        if (request.isPolling()) request.newDeferred();

        responseLock.signalAll();
    }

    @Override
    public void reject(RequestException e) {
        if (retrier != null && retrier.maybeRetry(e)) return;

        deferred.reject(e);

        if (noErrorCallbackRegistered &&
                !responseHeaderLock.isAwaiting() &&
                !responseLock.isAwaiting()) {
            if (e instanceof RequestTimeoutException) {
                if (noTimeoutCallbackRegistered) e.printStackTrace();
            } else if (e instanceof RequestCancelException) {
                if (noCancelCallbackRegistered) e.printStackTrace();
            } else if (e instanceof RequestAbortException) {
                if (noAbortCallbackRegistered) e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }

        if (request.isPolling()) request.newDeferred();

        responseHeaderLock.signalAll();
        responseBodyLock.signalAll();
        responseLock.signalAll();
    }

    @Override
    public void notifyDownload(ReadProgress progress) {
        deferred.notifyDownload(progress);
    }

    @Override
    public void notifyUpload(WriteProgress progress) {
        deferred.notifyUpload(progress);
    }

    @Override
    public void notifyResponse(RawResponse response) {
        this.rawResponse = response;
        responseHeaderLock.signalAll();
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

    @Override
    public RequestException getRejectResult() {
        return deferred.rejectResult;
    }

    @Override
    public Response getResolveResult() {
        return deferred.resolveResult;
    }

    @Override
    public AsyncRunner.Lock getResponseHeaderLock() {
        return responseHeaderLock;
    }

    @Override
    public AsyncRunner.Lock getResponseBodyLock() {
        return responseBodyLock;
    }

    @Override
    public AsyncRunner.Lock getResponseLock() {
        return responseLock;
    }

    public Future<IncomingResponse> getFuture() {
        return new Future<IncomingResponse>() {
            private volatile boolean cancelled;

            public boolean cancel(boolean mayInterruptIfRunning) {
                if (cancelled) return false;

                if (!deferred.isPending()) return false;

                if (!mayInterruptIfRunning) return false;

                cancelled = true;
                getHttpConnection().cancel();
                return true;
            }

            public boolean isCancelled() {
                return cancelled;
            }

            public boolean isDone() {
                return cancelled || !deferred.isPending();
            }

            public IncomingResponse get() throws InterruptedException, ExecutionException {
                try {
                    return get(0, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    throw new ExecutionException(e);
                }
            }

            public IncomingResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                    TimeoutException {
                final long startTime = System.currentTimeMillis();

                while (!(cancelled && deferred.isRejected() && rawResponse != null)) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    responseHeaderLock.await(unit.toMillis(timeout - elapsed));

                    elapsed = System.currentTimeMillis() - startTime;
                    if (timeout > 0 && elapsed >= timeout) {
                        throw new TimeoutException("The timeout of " + timeout + "ms has expired.");
                    }
                }

                checkInvalidStates();

                return rawResponse.getIncomingResponse();
            }

            private void checkInvalidStates() throws ExecutionException {
                if (cancelled) {
                    throw new CancellationException("Future was cancelled.");
                }

                if (deferred.isRejected()) {
                    throw new ExecutionException(deferred.rejectResult);
                }
            }
        };
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }

    protected DeferredRequest<T> replicate() {
        return new DeferredRequest<T>(request, asyncRunner, deferred.replicate(), noAbortCallbackRegistered,
                noCancelCallbackRegistered, noErrorCallbackRegistered, noTimeoutCallbackRegistered);
    }
}
