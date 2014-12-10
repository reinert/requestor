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

import io.reinert.requestor.deferred.Promise;

/**
 * Default implementation for {@link RequestInvoker}.
 * <p/>
 *
 * NOTE: This class is removed from classpath when assembling the jar.
 * Implementers should provide an own implementation of its own {@link RequestInvoker}.
 *
 * @see RequestInvoker
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
    public Promise<Void> get() {
        return send("GET", Void.class);
    }

    @Override
    public <T> Promise<T> get(Class<T> resultType) {
        return send("GET", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> get(Class<T> resultType, Class<C> containerType) {
        return send("GET", resultType, containerType);
    }

    @Override
    public Promise<Void> post() {
        return send("POST", Void.class);
    }

    @Override
    public <T> Promise<T> post(Class<T> resultType) {
        return send("POST", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> post(Class<T> resultType, Class<C> containerType) {
        return send("POST", resultType, containerType);
    }

    @Override
    public Promise<Void> put() {
        return send("PUT", Void.class);
    }

    @Override
    public <T> Promise<T> put(Class<T> resultType) {
        return send("PUT", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> put(Class<T> resultType, Class<C> containerType) {
        return send("PUT", resultType, containerType);
    }

    @Override
    public Promise<Void> delete() {
        return send("DELETE", Void.class);
    }

    @Override
    public <T> Promise<T> delete(Class<T> resultType) {
        return send("DELETE", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> delete(Class<T> resultType,
                                                                          Class<C> containerType) {
        return send("DELETE", resultType, containerType);
    }

    @Override
    public Promise<Void> patch() {
        return send("PATCH", Void.class);
    }

    @Override
    public <T> Promise<T> patch(Class<T> resultType) {
        return send("PATCH", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> patch(Class<T> resultType,
                                                                         Class<C> containerType) {
        return send("PATCH", resultType, containerType);
    }

    @Override
    public Promise<Void> head() {
        return send("HEAD", Void.class);
    }

    @Override
    public <T> Promise<T> head(Class<T> resultType) {
        return send("HEAD", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> head(Class<T> resultType, Class<C> containerType) {
        return send("HEAD", resultType, containerType);
    }

    @Override
    public Promise<Void> options() {
        return send("OPTIONS", Void.class);
    }

    @Override
    public <T> Promise<T> options(Class<T> resultType) {
        return send("OPTIONS", resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> options(Class<T> resultType,
                                                                           Class<C> containerType) {
        return send("OPTIONS", resultType, containerType);
    }
}
