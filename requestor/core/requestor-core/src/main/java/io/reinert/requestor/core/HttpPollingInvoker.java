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
package io.reinert.requestor.core;

import java.util.Collection;
import java.util.Map;

/**
 * Defines HTTP invocation methods to polling requests.
 *
 * @see PollingRequestInvoker
 *
 * @author Danilo Reinert
 */
public interface HttpPollingInvoker extends HttpInvoker {

    @Override
    PollingRequest<Void> get();

    @Override
    <T> PollingRequest<T> get(Class<T> entityType);

    @Override
    <T, C extends Collection> PollingRequest<Collection<T>> get(Class<C> collectionType, Class<T> entityType);

    @Override
    <V, K, M extends Map> PollingRequest<Map<K, V>> get(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    @Override
    PollingRequest<Void> post();

    @Override
    <T> PollingRequest<T> post(Class<T> entityType);

    @Override
    <T, C extends Collection> PollingRequest<Collection<T>> post(Class<C> collectionType, Class<T> entityType);

    @Override
    <V, K, M extends Map> PollingRequest<Map<K, V>> post(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    @Override
    PollingRequest<Void> put();

    @Override
    <T> PollingRequest<T> put(Class<T> entityType);

    @Override
    <T, C extends Collection> PollingRequest<Collection<T>> put(Class<C> collectionType, Class<T> entityType);

    @Override
    <V, K, M extends Map> PollingRequest<Map<K, V>> put(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    @Override
    PollingRequest<Void> delete();

    @Override
    <T> PollingRequest<T> delete(Class<T> entityType);

    @Override
    <T, C extends Collection> PollingRequest<Collection<T>> delete(Class<C> collectionType, Class<T> entityType);

    @Override
    <V, K, M extends Map> PollingRequest<Map<K, V>> delete(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    @Override
    PollingRequest<Void> patch();

    @Override
    <T> PollingRequest<T> patch(Class<T> entityType);

    @Override
    <T, C extends Collection> PollingRequest<Collection<T>> patch(Class<C> collectionType, Class<T> entityType);

    @Override
    <V, K, M extends Map> PollingRequest<Map<K, V>> patch(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    @Override
    PollingRequest<Headers> head();

    @Override
    PollingRequest<Headers> options();
}
