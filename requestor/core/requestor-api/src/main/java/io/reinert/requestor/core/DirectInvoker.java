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

/**
 * Allows direct invocation of HTTP requests.
 *
 * @author Danilo Reinert
 */
public interface DirectInvoker {

    Request<Void> get(String uri);

    <T> Request<T> get(String uri, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> get(String uri, Class<C> collectionType, Class<T> entityType);

    Request<Void> post(String uri);

    Request<Void> post(String uri, Object payload);

    <T> Request<T> post(String uri, Object payload, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> post(String uri, Object payload, Class<C> collectionType,
                                                          Class<T> entityType);

    <T> Request<T> post(String uri, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> post(String uri, Class<C> collectionType, Class<T> entityType);

    Request<Void> put(String uri);

    Request<Void> put(String uri, Object payload);

    <T> Request<T> put(String uri, Object payload, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> put(String uri, Object payload, Class<C> collectionType,
                                                         Class<T> entityType);

    <T> Request<T> put(String uri, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> put(String uri, Class<C> collectionType, Class<T> entityType);

    Request<Void> delete(String uri);

    <T> Request<T> delete(String uri, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> delete(String uri, Class<C> collectionType, Class<T> entityType);

    Request<Void> patch(String uri);

    Request<Void> patch(String uri, Object payload);

    <T> Request<T> patch(String uri, Object payload, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> patch(String uri, Object payload, Class<C> collectionType,
                                                           Class<T> entityType);

    <T> Request<T> patch(String uri, Class<T> entityType);

    <T, C extends Collection> Request<Collection<T>> patch(String uri, Class<C> collectionType, Class<T> entityType);

    Request<Headers> head(String uri);

    Request<Headers> options(String uri);
}
