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
package io.reinert.requestor;

import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.Timer;

import io.reinert.requestor.payload.type.PayloadType;

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public abstract class RequestDispatcher {

    private static final Logger LOGGER = Logger.getLogger(RequestDispatcher.class.getName());

    private final RequestProcessor requestProcessor;
    private final ResponseProcessor responseProcessor;
    private final DeferredFactory deferredFactory;

    public RequestDispatcher(RequestProcessor requestProcessor, ResponseProcessor responseProcessor,
                             DeferredFactory deferredFactory) {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
        this.deferredFactory = deferredFactory;
    }

    /**
     * Sends the request through the wire and resolves (or rejects) the deferred when completed.
     * The success result must be a instance of #entityType.
     * <p></p>
     * Implementations must execute an HTTP Request with given values and resolve/reject the deferred when the request
     * is finished. It is recommended that progress events be sent to
     * {@link Deferred#notifyDownload(RequestProgress)} and
     * {@link Deferred#notifyUpload(RequestProgress)}.
     * <p></p>
     * All possible exceptions should be caught and sent to {@link Deferred#reject(RequestException)}
     * wrapped in a {@link RequestException} or any of its children. This will avoid breaking code flow when some
     * exception occurs.
     *
     * @param request               The request to be sent
     * @param deferred              The deferred to resolve or reject when completed
     * @param responsePayloadType   The type of the expected response payload
     * @param <R>                   The expected type of the promise
     */
    protected abstract <R> void send(PreparedRequest request, Deferred<R> deferred,
                                     PayloadType responsePayloadType);

    /**
     * Sends the request and return an instance of {@link Promise} expecting a sole result.
     *
     * @param request               The built request
     * @param responsePayloadType   The class instance of the expected type in response payload
     * @param <T>                   The expected type in response payload
     *
     * @return                      The promise for the dispatched request
     */
    public <T> Promise<T> dispatch(MutableSerializedRequest request, PayloadType responsePayloadType) {
        Deferred<T> deferred = deferredFactory.getDeferred();

        scheduleDispatch(request, responsePayloadType, deferred, false, false);

        LOGGER.info(request.getMethod()  + " to " + request.getUri() + " scheduled to dispatch in " +
                request.getDelay() + "ms.");

        return deferred.getPromise();
    }

    /**
     * Sends the request with the respective callback bypassing request processing.
     *
     * @param request               The built request
     * @param responsePayloadType   The expected PayloadType of the response payload
     * @param callback              The callback to be executed when done
     * @param <T>                   The expected type of the response payload
     */
    public <T> void dispatch(MutableSerializedRequest request, PayloadType responsePayloadType,
                             boolean skipAuth, Callback<T, Throwable> callback) {
        final Deferred<T> deferred = new CallbackDeferred<T>(callback);

        // TODO: add a skipAuth option and handle it in RequestInAuthProcess#process and erase the skipAuth flag here.
        scheduleDispatch(request, responsePayloadType, deferred, true, skipAuth);
    }

    private <T> void scheduleDispatch(final MutableSerializedRequest request, final PayloadType responsePayloadType,
                                      final Deferred<T> deferred, final boolean skipProcessing,
                                      final boolean skipAuth) {
        final RequestInAuthProcess<T> requestInAuthProcess = new RequestInAuthProcess<T>(request,
                responsePayloadType, this, deferred);

        final MutableSerializedRequest originalRequest = request.getPollInterval() > 0 ? request.copy() : null;

        // TODO: switch by a native Timer to avoid importing GWT UI module
        new Timer() {
            public void run() {
                try {
                    // Process and send the request
                    if (skipProcessing) {
                        if (skipAuth) {
                            requestInAuthProcess.setAuth(PassThroughAuth.getInstance());
                        }
                        requestInAuthProcess.process();
                    } else {
                        requestProcessor.process(requestInAuthProcess);
                    }

                    // Poll the request
                    if (requestInAuthProcess.getPollInterval() > 0 && (
                            requestInAuthProcess.getPollLimit() <= 0 ||
                            requestInAuthProcess.getPollCounter() < requestInAuthProcess.getPollLimit())) {
                        new Timer() {
                            @Override
                            public void run() {
                                originalRequest.incrementPollCounter();
                                scheduleDispatch(originalRequest, responsePayloadType, deferred.getUnresolvedCopy(),
                                        false, false);
                            }
                        }.schedule(request.getPollInterval());
                    }
                } catch (Exception e) {
                    deferred.reject(new RequestException(requestInAuthProcess, "An error occurred before sending the" +
                            " request. See previous exception.", e));
                }
            }
        }.schedule(request.getDelay());
    }

    /**
     * Evaluates the response and resolves the deferred.
     * This method must be called by implementations after the response is received.
     *
     * @param response  The response received from the request
     * @param <R>       Type of the deferred
     */
    protected <R> void evalResponse(RawResponse response) {
        responseProcessor.process(response);
    }
}
