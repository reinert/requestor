/*
 * Copyright 2014-2021 Danilo Reinert
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

import java.util.Collection;
import java.util.Map;

import io.reinert.requestor.core.uri.Uri;

/**
 * Default implementation for {@link RequestInvoker}.
 *
 * @see RequestInvoker
 *
 * @author Danilo Reinert
 */
class RequestInvokerImpl extends AbstractRequestInvoker {

    public RequestInvokerImpl(Session session, Uri uri, LeafStore store, RequestDispatcher dispatcher) {
        super(session, uri, store, dispatcher);
    }

    //===================================================================
    // RequestInvoker methods
    //===================================================================

    @Override
    public PollingRequest<Void> get() {
        return send(HttpMethod.GET, Void.class);
    }

    @Override
    public <T> PollingRequest<T> get(Class<T> entityType) {
        return send(HttpMethod.GET, entityType);
    }

    @Override
    public <T, C extends Collection> PollingRequest<Collection<T>> get(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.GET, entityType, collectionType);
    }

    @Override
    public <V, K, M extends Map> PollingRequest<Map<K, V>> get(Class<M> mapType, Class<K> keyType, Class<V> valueType) {
        return send(HttpMethod.GET, valueType, keyType, mapType);
    }

    @Override
    public PollingRequest<Void> post() {
        return send(HttpMethod.POST, Void.class);
    }

    @Override
    public <T> PollingRequest<T> post(Class<T> entityType) {
        return send(HttpMethod.POST, entityType);
    }

    @Override
    public <T, C extends Collection> PollingRequest<Collection<T>> post(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.POST, entityType, collectionType);
    }

    @Override
    public <V, K, M extends Map> PollingRequest<Map<K, V>> post(Class<M> mapType, Class<K> keyType,
                                                                Class<V> valueType) {
        return send(HttpMethod.POST, valueType, keyType, mapType);
    }

    @Override
    public PollingRequest<Void> put() {
        return send(HttpMethod.PUT, Void.class);
    }

    @Override
    public <T> PollingRequest<T> put(Class<T> entityType) {
        return send(HttpMethod.PUT, entityType);
    }

    @Override
    public <T, C extends Collection> PollingRequest<Collection<T>> put(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.PUT, entityType, collectionType);
    }

    @Override
    public <V, K, M extends Map> PollingRequest<Map<K, V>> put(Class<M> mapType, Class<K> keyType, Class<V> valueType) {
        return send(HttpMethod.PUT, valueType, keyType, mapType);
    }

    @Override
    public PollingRequest<Void> delete() {
        return send(HttpMethod.DELETE, Void.class);
    }

    @Override
    public <T> PollingRequest<T> delete(Class<T> entityType) {
        return send(HttpMethod.DELETE, entityType);
    }

    @Override
    public <T, C extends Collection> PollingRequest<Collection<T>> delete(Class<C> collectionType,
                                                                          Class<T> entityType) {
        return send(HttpMethod.DELETE, entityType, collectionType);
    }

    @Override
    public <V, K, M extends Map> PollingRequest<Map<K, V>> delete(Class<M> mapType, Class<K> keyType,
                                                                  Class<V> valueType) {
        return send(HttpMethod.DELETE, valueType, keyType, mapType);
    }

    @Override
    public PollingRequest<Void> patch() {
        return send(HttpMethod.PATCH, Void.class);
    }

    @Override
    public <T> PollingRequest<T> patch(Class<T> entityType) {
        return send(HttpMethod.PATCH, entityType);
    }

    @Override
    public <T, C extends Collection> PollingRequest<Collection<T>> patch(Class<C> collectionType, Class<T> entityType) {
        return send(HttpMethod.PATCH, entityType, collectionType);
    }

    @Override
    public <V, K, M extends Map> PollingRequest<Map<K, V>> patch(Class<M> mapType, Class<K> keyType,
                                                                 Class<V> valueType) {
        return send(HttpMethod.PATCH, valueType, keyType, mapType);
    }

    @Override
    public PollingRequest<Headers> head() {
        return send(HttpMethod.HEAD, Headers.class);
    }

    @Override
    public PollingRequest<Headers> options() {
        return send(HttpMethod.OPTIONS, Headers.class);
    }
}
