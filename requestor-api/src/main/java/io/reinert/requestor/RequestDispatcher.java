/*
 * Copyright 2014 Danilo Reinert
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

import java.util.Collection;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.deferred.DeferredRequest;
import io.reinert.requestor.deferred.RequestPromise;

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public abstract class RequestDispatcher {

    private final ResponseProcessor processor;
    private final DeferredRequestFactory deferredFactory = GWT.create(DeferredRequestFactory.class);

    public RequestDispatcher(ResponseProcessor processor) {
        this.processor = processor;
    }

    /**
     * Sends the request through the wire and resolves (or rejects) the deferred when completed.
     * <p/>
     * Implementations must execute an HTTP Request with given values and resolve/reject the deferred when the request
     * is finished. It is recommended that progress events be sent to
     * {@link DeferredRequest#notifyDownload(RequestProgress)} and
     * {@link DeferredRequest#notifyUpload(RequestProgress)}.
     * <p/>
     * All possible exceptions should be caught and sent to {@link DeferredRequest#reject(Throwable)} wrapped in a
     * {@link RequestException} or any of its children. This will avoid breaking code flow when some exception occurs.
     *
     * @param request   The request to be sent
     * @param deferred  The deferred to resolve or reject when completed
     * @param <D>       The expected type of the promise
     */
    protected abstract <D> void send(final SerializedRequest request, final DeferredRequest<D> deferred);

    /**
     * Sends the request and return an instance of {@link RequestPromise} expecting a sole result.
     *
     * @param request       The built request
     * @param responseType  The class instance of the expected type in response payload
     * @param <T>           The expected type in response payload
     * @return              The promise for the dispatched request
     */
    public <T> RequestPromise<T> dispatch(SerializedRequest request, Class<T> responseType) {
        final DeferredRequest<T> deferred = deferredFactory.getDeferredRequest(processor, responseType);
        try {
            send(request, deferred);
        } catch (Exception e) {
            deferred.doReject(new RequestDispatchException(
                    "Some non-caught exception occurred while dispatching the request", e));
        }
        return deferred;
    }

    /**
     * Sends the request and return an instance of {@link RequestPromise} expecting a collection result.
     *
     * @param request       The built request
     * @param responseType  The class instance of the expected type in response payload
     * @param containerType The class instance of the container type which will hold the values
     * @param <T>           The expected type in response payload
     * @param <C>           The collection type to hold the values
     * @return              The promise for the dispatched request
     */
    public <T, C extends Collection> RequestPromise<Collection<T>> dispatch(SerializedRequest request,
                                                                            Class<T> responseType,
                                                                            Class<C> containerType) {
        final DeferredRequest<Collection<T>> deferred = deferredFactory.getDeferredRequest(processor, responseType,
                containerType);
        try {
            send(request, deferred);
        } catch (Exception e) {
            deferred.doReject(new RequestDispatchException(
                    "Some non-caught exception occurred while dispatching the request", e));
        }
        return deferred;
    }
}
