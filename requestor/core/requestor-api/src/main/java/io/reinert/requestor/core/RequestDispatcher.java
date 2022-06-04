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

import java.util.List;
import java.util.logging.Logger;

import io.reinert.requestor.core.callback.DualCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.payload.type.SinglePayloadType;

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public abstract class RequestDispatcher implements RunScheduler {

    public interface Factory {
        RequestDispatcher create(RequestProcessor requestProcessor,
                                 ResponseProcessor responseProcessor,
                                 DeferredPool.Factory deferredFactory);

        void shutdown();

        List<Runnable> shutdownNow();

        boolean isShutdown();

        boolean isTerminated();

        boolean awaitTermination(long timeoutInMillis) throws InterruptedException;
    }

    private static final Logger logger = Logger.getLogger(RequestDispatcher.class.getName());

    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;
    private final DeferredPool.Factory deferredPoolFactory;

    protected RequestDispatcher(RequestProcessor requestProcessor, ResponseProcessor responseProcessor,
                                DeferredPool.Factory deferredPoolFactory) {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
        this.deferredPoolFactory = deferredPoolFactory;
    }

    /**
     * Defers the execution of a {@link Runnable} by the informed delay.
     * This method is used to schedule the dispatches.
     *
     * @param runnable  A callback to be executed later
     * @param delay     The time to postpone the runnable execution
     */
    public abstract void scheduleRun(Runnable runnable, int delay);

    /**
     * Causes the currently executing thread to sleep for the specified number of milliseconds.
     *
     * @param millis the length of time to sleep in milliseconds
     */
    public abstract void sleep(int millis);

    /**
     * Sends the request through the wire and resolves (or rejects) the deferred when completed.
     * The success result must be a instance of #entityType.
     * <p></p>
     * Implementations must execute an HTTP Request with given values and resolve/reject the deferred when the request
     * is finished. It is recommended that progress events be sent to
     * {@link Deferred#notifyDownload(ReadProgress)} and
     * {@link Deferred#notifyUpload(WriteProgress)}.
     * <p></p>
     * All possible exceptions should be caught and sent to {@link Deferred#reject(RequestException)}
     * wrapped in a {@link RequestException} or any of its children. This will avoid breaking code flow when some
     * exception occurs.
     *
     * @param request               The request to be sent
     * @param deferred              The deferred to resolve or reject when completed
     * @param responsePayloadType   The type of the expected response payload
     * @param <R>                   The expected type of the request
     */
    protected abstract <R> void send(PreparedRequest request, Deferred<R> deferred, PayloadType responsePayloadType);

    /**
     * Evaluates the response and resolves the deferred.
     * This method must be called by implementations after the response is received.
     *
     * @param response  The response received from the request
     */
    protected final void evalResponse(RawResponse response) {
        responseProcessor.process(response);
    }

    /**
     * Sends the request and return an instance of {@link Request} expecting a sole result.
     *
     * @param request               The built request
     * @param responsePayloadType   The class instance of the expected type in response payload
     * @param <T>                   The expected type in response payload
     *
     * @return                      The request for the dispatched request
     */
    public <T> PollingRequest<T> dispatch(MutableSerializedRequest request, PayloadType responsePayloadType) {
        DeferredPool<T> deferredPool = deferredPoolFactory.create(request);

        PollingRequest<T> deferredRequest = deferredPool.getRequest();

        if (isLongPolling(request)) {
            deferredRequest.onLoad(getLongPollingCallback(request, responsePayloadType, deferredPool));
        }

        scheduleDispatch(request, responsePayloadType, deferredPool, false, false, false);

        logger.info(request.getMethod()  + " to " + request.getUri() + " scheduled to dispatch in " +
                request.getDelay() + "ms.");

        return deferredRequest;
    }

    /**
     * Sends the request with the respective callback bypassing request processing and polling.
     *
     * @param request               The built request
     * @param callback              The callback to be executed when done
     * @param <T>                   The expected type of the response payload
     */
    public <T> void dispatch(MutableSerializedRequest request, boolean skipAuth, DualCallback callback) {
        final CallbackDeferred deferred = new CallbackDeferred(callback);
        final PayloadType responsePayloadType = new SinglePayloadType<Response>(Response.class);

        // TODO: add a skipAuth option and handle it in RequestInAuthProcess#process and erase the skipAuth flag here.
        scheduleDispatch(request, responsePayloadType, deferred, true, skipAuth, true);
    }

    private <T> void scheduleDispatch(final MutableSerializedRequest request,
                                      final PayloadType responsePayloadType,
                                      final DeferredPool<T> deferredPool,
                                      final boolean skipProcessing,
                                      final boolean skipAuth,
                                      final boolean skipPolling) {
        final Deferred<T> deferred = deferredPool.newDeferred();

        // TODO: create pollingOptions outside request?
        request.incrementPollingCount();

        setHttpConnection(request, deferred);

        final MutableSerializedRequest nextRequest =
                !skipPolling && isShortPolling(request) ? request.replicate() : null;

        final RequestInAuthProcess<T> requestInAuthProcess = new RequestInAuthProcess<T>(request, responsePayloadType,
                this, deferred);

        scheduleRun(new Runnable() {
            @Override
            public void run() {
                try {
                    // Process and send the request
                    if (skipAuth) {
                        requestInAuthProcess.setAuth((Auth.Provider) null);
                    }

                    if (skipProcessing) {
                        requestInAuthProcess.process();
                    } else {
                        requestProcessor.process(requestInAuthProcess);
                    }

                    // Poll the request
                    if (nextRequest != null) {
                        schedulePollingRequest(nextRequest, responsePayloadType, deferredPool);
                    }
                } catch (Exception e) {
                    // TODO: check if this try-catch block is really necessary
                    if (deferred.isPending()) {
                        deferred.reject(new RequestAbortException(requestInAuthProcess,
                                "An error occurred before sending the request. See previous exception.", e));
                    }
                }
            }
        }, request.getDelay());
    }

    private <T> void setHttpConnection(final MutableSerializedRequest request, final Deferred<T> deferred) {
        deferred.setHttpConnection(new HttpConnection() {

            @Override
            public void cancel() {
                if (isPending()) {
                    deferred.reject(new RequestAbortException(request, "Request was cancelled before being sent" +
                            " through the HttpConnection."));
                }
            }

            @Override
            public boolean isPending() {
                return deferred.isPending();
            }
        });
    }

    private <T> ResponseCallback getLongPollingCallback(final MutableSerializedRequest request,
                                                        final PayloadType responsePayloadType,
                                                        final DeferredPool<T> deferredPool) {
        final MutableSerializedRequest originalRequest = request.replicate();
        return new ResponseCallback() {
            @Override
            public void execute(Response response) {
                if (isLongPolling(originalRequest)) {
                    schedulePollingRequest(originalRequest.replicate(), responsePayloadType, deferredPool);
                }
            }
        };
    }

    private <T> void schedulePollingRequest(final MutableSerializedRequest nextRequest,
                                            final PayloadType responsePayloadType,
                                            final DeferredPool<T> deferredPool) {
        scheduleRun(new Runnable() {
            @Override
            public void run() {
                if (nextRequest.isPolling()) {
                    scheduleDispatch(nextRequest, responsePayloadType, deferredPool, false, false, false);
                }
            }
        }, nextRequest.getPollingInterval());
    }

    private boolean isLongPolling(MutableSerializedRequest request) {
        return request.isPolling() && request.getPollingStrategy() == PollingStrategy.LONG;
    }

    private boolean isShortPolling(MutableSerializedRequest request) {
        return request.isPolling() && request.getPollingStrategy() == PollingStrategy.SHORT;
    }
}
