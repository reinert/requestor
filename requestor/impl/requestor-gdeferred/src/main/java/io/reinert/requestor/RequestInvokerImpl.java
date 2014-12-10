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
        return send("GET", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> get(Class<T> responseType) {
        return send("GET", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> get(Class<T> responseType,
                                                                                Class<C> containerType) {
        return send("GET", responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> post() {
        return send("POST", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> post(Class<T> responseType) {
        return send("POST", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> post(Class<T> responseType,
                                                                                 Class<C> containerType) {
        return send("POST", responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> put() {
        return send("PUT", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> put(Class<T> responseType) {
        return send("PUT", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> put(Class<T> responseType,
                                                                                Class<C> containerType) {
        return send("PUT", responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> delete() {
        return send("DELETE", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> delete(Class<T> responseType) {
        return send("DELETE", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> delete(Class<T> responseType,
                                                                                   Class<C> containerType) {
        return send("DELETE", responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> patch() {
        return send("PATCH", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> patch(Class<T> responseType) {
        return send("PATCH", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> patch(Class<T> responseType,
                                                                                  Class<C> containerType) {
        return send("PATCH", responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> head() {
        return send("HEAD", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> head(Class<T> responseType) {
        return send("HEAD", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> head(Class<T> responseType,
                                                                                 Class<C> containerType) {
        return send("HEAD", responseType, containerType);
    }

    @Override
    public GDeferredRequestPromise<Void> options() {
        return send("OPTIONS", Void.class);
    }

    @Override
    public <T> GDeferredRequestPromise<T> options(Class<T> responseType) {
        return send("OPTIONS", responseType);
    }

    @Override
    public <T, C extends Collection> GDeferredRequestPromise<Collection<T>> options(Class<T> responseType,
                                                                                    Class<C> containerType) {
        return send("OPTIONS", responseType, containerType);
    }
}
