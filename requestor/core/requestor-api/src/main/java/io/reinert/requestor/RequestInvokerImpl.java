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
 * <p></p>
 *
 * @see RequestInvoker
 *
 * @author Danilo Reinert
 */
class RequestInvokerImpl extends AbstractRequestInvoker {

    public RequestInvokerImpl(Uri uri, TransientStore store, RequestDispatcher dispatcher) {
        super(uri, store, dispatcher);
    }

    //===================================================================
    // RequestInvoker methods
    //===================================================================

    @Override
    public Promise<Void> get() {
        return send(HttpMethod.GET, Void.class);
    }

    @Override
    public <T> Promise<T> get(Class<T> entityType) {
        return send(HttpMethod.GET, entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> get(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.GET, entityType, collectionType);
    }

    @Override
    public Promise<Void> post() {
        return send(HttpMethod.POST, Void.class);
    }

    @Override
    public <T> Promise<T> post(Class<T> entityType) {
        return send(HttpMethod.POST, entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> post(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.POST, entityType, collectionType);
    }

    @Override
    public Promise<Void> put() {
        return send(HttpMethod.PUT, Void.class);
    }

    @Override
    public <T> Promise<T> put(Class<T> entityType) {
        return send(HttpMethod.PUT, entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> put(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.PUT, entityType, collectionType);
    }

    @Override
    public Promise<Void> delete() {
        return send(HttpMethod.DELETE, Void.class);
    }

    @Override
    public <T> Promise<T> delete(Class<T> entityType) {
        return send(HttpMethod.DELETE, entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> delete(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.DELETE, entityType, collectionType);
    }

    @Override
    public Promise<Void> patch() {
        return send(HttpMethod.PATCH, Void.class);
    }

    @Override
    public <T> Promise<T> patch(Class<T> entityType) {
        return send(HttpMethod.PATCH, entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> patch(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.PATCH, entityType, collectionType);
    }

    @Override
    public Promise<Void> options() {
        return send(HttpMethod.OPTIONS, Void.class);
    }

    @Override
    public <T> Promise<T> options(Class<T> entityType) {
        return send(HttpMethod.OPTIONS, entityType);
    }

    @Override
    public <T, C extends Collection> Promise<Collection<T>> options(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.OPTIONS, entityType, collectionType);
    }

    @Override
    public Promise<Headers> head() {
        return send(HttpMethod.HEAD, Headers.class);
    }
}
