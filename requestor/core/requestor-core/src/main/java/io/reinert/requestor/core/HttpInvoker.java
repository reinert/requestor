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

/**
 * Defines HTTP request invocation methods.
 *
 * @see RequestInvoker
 *
 * @author Danilo Reinert
 */
public interface HttpInvoker {

    Request<Void> get();

    <T> Request<T> get(Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> get(Class<C> collectionType, Class<T> entityType);

    <V, K, M extends Map> Request<Map<K, V>> get(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    Request<Void> post();

    <T> Request<T> post(Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> post(Class<C> collectionType, Class<T> entityType);

    <V, K, M extends Map> Request<Map<K, V>> post(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    Request<Void> put();

    <T> Request<T> put(Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> put(Class<C> collectionType, Class<T> entityType);

    <V, K, M extends Map> Request<Map<K, V>> put(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    Request<Void> delete();

    <T> Request<T> delete(Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> delete(Class<C> collectionType, Class<T> entityType);

    <V, K, M extends Map> Request<Map<K, V>> delete(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    Request<Void> patch();

    <T> Request<T> patch(Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> patch(Class<C> collectionType, Class<T> entityType);

    <V, K, M extends Map> Request<Map<K, V>> patch(Class<M> mapType, Class<K> keyType, Class<V> valueType);

    Request<Headers> head();

    Request<Headers> options();
}
