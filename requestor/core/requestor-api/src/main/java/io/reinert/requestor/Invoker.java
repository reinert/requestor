/*
 * Copyright 2015 Danilo Reinert
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
 * Defines HTTP request invocation methods.
 *
 * @see RequestInvoker
 *
 * @author Danilo Reinert
 */
public interface Invoker {

    Promise<Void> get();

    <T> Promise<T> get(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> get(Class<T> resultType, Class<C> containerType);

    Promise<Void> post();

    <T> Promise<T> post(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> post(Class<T> resultType, Class<C> containerType);

    Promise<Void> put();

    <T> Promise<T> put(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> put(Class<T> resultType, Class<C> containerType);

    Promise<Void> delete();

    <T> Promise<T> delete(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> delete(Class<T> resultType, Class<C> containerType);

    Promise<Void> patch();

    <T> Promise<T> patch(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> patch(Class<T> resultType, Class<C> containerType);

    Promise<Void> options();

    <T> Promise<T> options(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> options(Class<T> resultType, Class<C> containerType);

    Promise<Headers> head();
}
