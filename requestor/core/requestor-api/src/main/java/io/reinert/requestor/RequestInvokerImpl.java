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

import java.util.Collection;

import io.reinert.requestor.uri.Uri;

/**
 * Default implementation for {@link RequestInvoker}.
 * <p/>
 *
 * @see RequestInvoker
 *
 * @author Danilo Reinert
 */
class RequestInvokerImpl extends AbstractRequestInvoker {

    public RequestInvokerImpl(Uri uri, VolatileStorage storage, RequestDispatcher dispatcher) {
        super(uri, storage, dispatcher);
    }

    //===================================================================
    // RequestInvoker methods
    //===================================================================

    @Override
    public Promise<Void> get() {
        return send(HttpMethod.GET, Void.class);
    }

    @Override
    public <T> Promise<T> get(Class<T> resultType) {
        return send(HttpMethod.GET, resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> get(Class<T> resultType, Class<C> containerType) {
        return send(HttpMethod.GET, resultType, containerType);
    }

    @Override
    public Promise<Void> post() {
        return send(HttpMethod.POST, Void.class);
    }

    @Override
    public <T> Promise<T> post(Class<T> resultType) {
        return send(HttpMethod.POST, resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> post(Class<T> resultType, Class<C> containerType) {
        return send(HttpMethod.POST, resultType, containerType);
    }

    @Override
    public Promise<Void> put() {
        return send(HttpMethod.PUT, Void.class);
    }

    @Override
    public <T> Promise<T> put(Class<T> resultType) {
        return send(HttpMethod.PUT, resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> put(Class<T> resultType, Class<C> containerType) {
        return send(HttpMethod.PUT, resultType, containerType);
    }

    @Override
    public Promise<Void> delete() {
        return send(HttpMethod.DELETE, Void.class);
    }

    @Override
    public <T> Promise<T> delete(Class<T> resultType) {
        return send(HttpMethod.DELETE, resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> delete(Class<T> resultType,
                                                                   Class<C> containerType) {
        return send(HttpMethod.DELETE, resultType, containerType);
    }

    @Override
    public Promise<Void> patch() {
        return send(HttpMethod.PATCH, Void.class);
    }

    @Override
    public <T> Promise<T> patch(Class<T> resultType) {
        return send(HttpMethod.PATCH, resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> patch(Class<T> resultType,
                                                                  Class<C> containerType) {
        return send(HttpMethod.PATCH, resultType, containerType);
    }

    @Override
    public Promise<Void> options() {
        return send(HttpMethod.OPTIONS, Void.class);
    }

    @Override
    public <T> Promise<T> options(Class<T> resultType) {
        return send(HttpMethod.OPTIONS, resultType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> options(Class<T> resultType,
                                                                    Class<C> containerType) {
        return send(HttpMethod.OPTIONS, resultType, containerType);
    }

    @Override
    public Promise<Headers> head() {
        return send(HttpMethod.HEAD, Headers.class);
    }
}
