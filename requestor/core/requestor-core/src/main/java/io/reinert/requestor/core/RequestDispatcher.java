/*
 * Copyright 2014-2022 Danilo Reinert
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

import io.reinert.requestor.core.callback.DualCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.payload.type.PayloadType;
import io.reinert.requestor.core.payload.type.SinglePayloadType;

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public abstract class RequestDispatcher implements AsyncRunner {

    public static int SLEEP_TIME_BEFORE_ABORTING = 50;

    public interface Factory {
        RequestDispatcher create(AsyncRunner asyncRunner,
                                 RequestProcessor requestProcessor,
                                 ResponseProcessor responseProcessor,
                                 DeferredPool.Factory deferredFactory,
                                 RequestLogger logger);
    }

    private final AsyncRunner asyncRunner;
    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;
    private final DeferredPool.Factory deferredPoolFactory;
    private final RequestLogger logger;

    protected RequestDispatcher(AsyncRunner asyncRunner, RequestProcessor requestProcessor,
                                ResponseProcessor responseProcessor, DeferredPool.Factory deferredPoolFactory,
                                RequestLogger logger) {
        this.asyncRunner = asyncRunner;
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
        this.deferredPoolFactory = deferredPoolFactory;
        this.logger = logger;
    }

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

    public void run(Runnable runnable, long delayMillis) {
        asyncRunner.run(runnable, delayMillis);
    }

    public void sleep(long millis) {
        asyncRunner.sleep(millis);
    }

    public void shutdown() {
        asyncRunner.shutdown();
    }

    public boolean isShutdown() {
        return asyncRunner.isShutdown();
    }

    public Lock getLock() {
        return asyncRunner.getLock();
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
        DeferredPool<T> deferredPool = deferredPoolFactory.create(request, this);

        PollingRequest<T> deferredRequest = deferredPool.getRequest();

        if (isLongPolling(request)) {
            deferredRequest.onLoad(getLongPollingCallback(request, responsePayloadType, deferredPool));
        }

        scheduleDispatch(request, responsePayloadType, deferredPool, false);

        return deferredRequest;
    }

    /**
     * Sends the request with the respective callback bypassing request processing and polling.
     *
     * @param request               The built request
     * @param callback              The callback to be executed when done
     * @param <T>                   The expected type of the response payload
     */
    public <T> void dispatch(MutableSerializedRequest request, DualCallback callback) {
        final CallbackDeferred deferred = new CallbackDeferred(callback, request);
        final PayloadType responsePayloadType = new SinglePayloadType<Response>(Response.class);

        // TODO: add a skipAuth option and handle it in RequestInAuthProcess#process and erase the skipAuth flag here.
        scheduleDispatch(request, responsePayloadType, deferred, true);
    }

    private <T> void scheduleDispatch(final MutableSerializedRequest request,
                                      final PayloadType responsePayloadType,
                                      final DeferredPool<T> deferredPool,
                                      final boolean skipPolling) {
        final Deferred<T> deferred = deferredPool.getDeferred();

        // TODO: create pollingOptions outside request?
        request.incrementPollingCount();

        setHttpConnection(request, deferred);

        final MutableSerializedRequest nextRequest =
                !skipPolling && isShortPolling(request) ? request.replicate() : null;

        final RequestInAuthProcess<T> requestInAuthProcess = new RequestInAuthProcess<T>(request, responsePayloadType,
                this, deferred);

        logger.log(request);

        run(new Runnable() {
            @Override
            public void run() {
                try {
                    requestProcessor.process(requestInAuthProcess);

                    // Poll the request
                    if (nextRequest != null) {
                        schedulePollingRequest(nextRequest, responsePayloadType, deferredPool);
                    }
                } catch (Exception e) {
                    if (deferred.isPending()) {
                        sleep(SLEEP_TIME_BEFORE_ABORTING);
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
                    sleep(SLEEP_TIME_BEFORE_ABORTING);
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
        run(new Runnable() {
            @Override
            public void run() {
                if (nextRequest.isPolling()) {
                    scheduleDispatch(nextRequest, responsePayloadType, deferredPool, false);
                }
            }
        }, Math.max(10, nextRequest.getPollingInterval()));
    }

    private boolean isLongPolling(MutableSerializedRequest request) {
        return request.isPolling() && request.getPollingStrategy() == PollingStrategy.LONG;
    }

    private boolean isShortPolling(MutableSerializedRequest request) {
        return request.isPolling() && request.getPollingStrategy() == PollingStrategy.SHORT;
    }
}
