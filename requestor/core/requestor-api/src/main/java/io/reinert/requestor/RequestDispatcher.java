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

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.deferred.Deferred;
import io.reinert.requestor.deferred.Promise;

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public abstract class RequestDispatcher {

    private final ResponseProcessor processor;
    private final DeferredFactory deferredFactory;

    public RequestDispatcher(ResponseProcessor processor, DeferredFactory deferredFactory) {
        this.processor = processor;
        this.deferredFactory = deferredFactory;
    }

    /**
     * Sends the request through the wire and resolves (or rejects) the deferred when completed.
     * The success result must be a instance of #resultType.
     * <p/>
     * Implementations must execute an HTTP Request with given values and resolve/reject the deferred when the request
     * is finished. It is recommended that progress events be sent to
     * {@link Deferred#notifyDownload(RequestProgress)} and
     * {@link Deferred#notifyUpload(RequestProgress)}.
     * <p/>
     * All possible exceptions should be caught and sent to {@link Deferred#reject(RequestException)}
     * wrapped in a {@link RequestException} or any of its children. This will avoid breaking code flow when some
     * exception occurs.
     *
     * @param request    The request to be sent
     * @param deferred   The deferred to resolve or reject when completed
     * @param resultType The class of the expected result
     * @param <T>        The expected type of the promise
     */
    protected abstract <T> void send(RequestOrder request, Deferred<T> deferred, Class<T> resultType);

    /**
     * Sends the request through the wire and resolves (or rejects) the deferred when completed.
     * The success result must be a containerType of resultType.
     * <p/>
     * Implementations must execute an HTTP Request with given values and resolve/reject the deferred when the request
     * is finished. It is recommended that progress events be sent to
     * {@link Deferred#notifyDownload(RequestProgress)} and
     * {@link Deferred#notifyUpload(RequestProgress)}.
     * <p/>
     * All possible exceptions should be caught and sent to {@link Deferred#reject(RequestException)}
     * wrapped in a {@link RequestException} or any of its children. This will avoid breaking code flow when some
     * exception occurs.
     *
     * @param request       The request to be sent
     * @param deferred      The deferred to resolve or reject when completed
     * @param <T>           The expected type of the promise
     * @param resultType    The class of the expected result
     * @param containerType The class of the container which will hold the result
     * @param <C>           The expected collection subtype which will hold the result
     */
    protected abstract <T, C extends Collection> void send(RequestOrder request, Deferred<C> deferred,
                                                           Class<T> resultType, Class<C> containerType);

    /**
     * Sends the request and return an instance of {@link Promise} expecting a sole result.
     *
     * @param request       The built request
     * @param resultType    The class instance of the expected type in response payload
     * @param <T>           The expected type in response payload
     *
     * @return              The promise for the dispatched request
     */
    public <T> Promise<T> dispatch(SerializedRequest request, final Class<T> resultType) {
        final Deferred<T> deferred = deferredFactory.getDeferred();

        final Authentication auth = request.getAuth();
        auth.authenticate(new AbstractRequestOrder(request) {
            @Override
            public void doSend() {
                try {
                    RequestDispatcher.this.send(this, deferred, resultType);
                } catch (Exception e) {
                    deferred.reject(new RequestDispatchException(
                            "Some non-caught exception occurred while dispatching the request", e));
                }
            }
        });

        return deferred;
    }

    /**
     * Sends the request and return an instance of {@link Promise} expecting a collection result.
     *
     * @param request       The built request
     * @param resultType    The class instance of the expected type in response payload
     * @param containerType The class instance of the container type which will hold the values
     * @param <T>           The expected type in response payload
     * @param <C>           The collection type to hold the values
     *
     * @return              The promise for the dispatched request
     */
    public <T, C extends Collection> Promise<Collection<T>> dispatch(SerializedRequest request,
                                                                     final Class<T> resultType,
                                                                     final Class<C> containerType) {
        final Deferred<Collection<T>> deferred = deferredFactory.getDeferred();

        final Authentication auth = request.getAuth();
        auth.authenticate(new AbstractRequestOrder(request) {
            @Override
            public void doSend() {
                try {
                    @SuppressWarnings("unchecked")
                    final Class<Collection<T>> collectionClass = (Class<Collection<T>>) containerType;
                    RequestDispatcher.this.send(this, deferred, resultType, collectionClass);
                } catch (Exception e) {
                    deferred.reject(new RequestDispatchException(
                            "Some non-caught exception occurred while dispatching the request", e));
                }
            }
        });

        return deferred;
    }

    protected ResponseProcessor getResponseProcessor() {
        return processor;
    }
}
