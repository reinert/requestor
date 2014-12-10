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
import io.reinert.requestor.header.Header;

/**
 * A {@link Request} with dispatching capabilities.
 *
 * NOTE: This class is removed from classpath when assembling the jar.
 * Implementers should provide an own implementation exposing a preferred Promises API.
 * Implementers must follow the pattern of this class in order to be officially integrated.
 *
 * @see RequestInvokerImpl
 *
 * @author Danilo Reinert
 */
public interface RequestInvoker extends RequestBuilder {

    @Override
    RequestInvoker contentType(String mediaType);

    @Override
    RequestInvoker accept(String mediaType);

    @Override
    RequestInvoker header(String header, String value);

    @Override
    RequestInvoker header(Header header);

    @Override
    RequestInvoker user(String user);

    @Override
    RequestInvoker password(String password);

    @Override
    RequestInvoker timeout(int timeoutMillis);

    @Override
    RequestInvoker payload(Object object) throws IllegalArgumentException;

    @Override
    RequestInvoker responseType(ResponseType responseType);

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

    Promise<Void> head();

    <T> Promise<T> head(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> head(Class<T> resultType, Class<C> containerType);

    Promise<Void> options();

    <T> Promise<T> options(Class<T> resultType);

    <T, C extends Collection> Promise<Collection<T>> options(Class<T> resultType, Class<C> containerType);
}
