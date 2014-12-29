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

import io.reinert.requestor.gdeferred.GDeferredRequestPromise;

/**
 * {@link io.reinert.requestor.RequestInvoker} implementation.
 *
 * @author Danilo Reinert
 */
public class RequestInvokerImpl extends AbstractRequestInvoker {

    public RequestInvokerImpl(String url, RequestProcessor processor, RequestDispatcher dispatcher) {
        super(url, dispatcher, processor);
    }

    //===================================================================
    // RequestInvoker methods
    //===================================================================

    @Override
    public GDeferredRequestPromise<Void> get() {
        return send(HttpMethod.GET, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> get(Class<T> responseType) {
        return send(HttpMethod.GET, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> get(Class<T> responseType,
                                                                                Class<C> containerType) {
        return send(HttpMethod.GET, responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> post() {
        return send(HttpMethod.POST, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> post(Class<T> responseType) {
        return send(HttpMethod.POST, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> post(Class<T> responseType,
                                                                                 Class<C> containerType) {
        return send(HttpMethod.POST, responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> put() {
        return send(HttpMethod.PUT, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> put(Class<T> responseType) {
        return send(HttpMethod.PUT, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> put(Class<T> responseType,
                                                                                Class<C> containerType) {
        return send(HttpMethod.PUT, responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> delete() {
        return send(HttpMethod.DELETE, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> delete(Class<T> responseType) {
        return send(HttpMethod.DELETE, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> delete(Class<T> responseType,
                                                                                   Class<C> containerType) {
        return send(HttpMethod.DELETE, responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> patch() {
        return send(HttpMethod.PATCH, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> patch(Class<T> responseType) {
        return send(HttpMethod.PATCH, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> patch(Class<T> responseType,
                                                                                  Class<C> containerType) {
        return send(HttpMethod.PATCH, responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> head() {
        return send(HttpMethod.HEAD, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> head(Class<T> responseType) {
        return send(HttpMethod.HEAD, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> head(Class<T> responseType,
                                                                                 Class<C> containerType) {
        return send(HttpMethod.HEAD, responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> options() {
        return send(HttpMethod.OPTIONS, Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> options(Class<T> responseType) {
        return send(HttpMethod.OPTIONS, responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> options(Class<T> responseType,
                                                                                    Class<C> containerType) {
        return send(HttpMethod.OPTIONS, responseType, containerType);
    }
}
