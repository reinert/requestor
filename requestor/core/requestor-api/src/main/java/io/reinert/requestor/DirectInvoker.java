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

/**
 * Allows direct invocation of HTTP requests.
 *
 * @author Danilo Reinert
 */
public interface DirectInvoker {

    Promise<Void> get(String uri);

    <T> Promise<T> get(String uri, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> get(String uri, Class<T> entityType, Class<C> collectionType);

    Promise<Void> post(String uri);

    Promise<Void> post(String uri, Object payload);

    <T> Promise<T> post(String uri, Object payload, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> post(String uri, Object payload, Class<T> entityType,
                                                          Class<C> collectionType);

    <T> Promise<T> post(String uri, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> post(String uri, Class<T> entityType, Class<C> collectionType);

    Promise<Void> put(String uri);

    Promise<Void> put(String uri, Object payload);

    <T> Promise<T> put(String uri, Object payload, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> put(String uri, Object payload, Class<T> entityType,
                                                         Class<C> collectionType);

    <T> Promise<T> put(String uri, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> put(String uri, Class<T> entityType, Class<C> collectionType);

    Promise<Void> delete(String uri);

    <T> Promise<T> delete(String uri, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> delete(String uri, Class<T> entityType, Class<C> collectionType);

    Promise<Void> patch(String uri);

    Promise<Void> patch(String uri, Object payload);

    <T> Promise<T> patch(String uri, Object payload, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> patch(String uri, Object payload, Class<T> entityType,
                                                           Class<C> collectionType);

    <T> Promise<T> patch(String uri, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> patch(String uri, Class<T> entityType, Class<C> collectionType);

    Promise<Void> options(String uri);

    <T> Promise<T> options(String uri, Class<T> entityType);

    <T, C extends Collection> Promise<Collection<T>> options(String uri, Class<T> entityType, Class<C> collectionType);

    Promise<Headers> head(String uri);
}
