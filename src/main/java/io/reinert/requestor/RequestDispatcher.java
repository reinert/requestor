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

/**
 * This class dispatches the requests and return promises.
 *
 * @author Danilo Reinert
 */
public interface RequestDispatcher {

    /**
     * Sends the request and return an instance of {@link RequestPromise} expecting a sole result.
     *
     * @param request       The request under construction
     * @param responseType  The class instance of the expected type in response payload
     * @param <T>           The expected type in response payload
     * @return              The promise for the dispatched request
     */
    <T> RequestPromise<T> send(RequestBuilder request, Class<T> responseType);

    /**
     * Sends the request and return an instance of {@link RequestPromise} expecting a collection result.
     *
     * @param request       The request under construction
     * @param responseType  The class instance of the expected type in response payload
     * @param containerType The class instance of the container type which will hold the values
     * @param <T>           The expected type in response payload
     * @param <C>           The collection type to hold the values
     * @return              The promise for the dispatched request
     */
    <T, C extends Collection> RequestPromise<Collection<T>> send(RequestBuilder request,
                                                                        Class<T> responseType,
                                                                        Class<C> containerType);
}
