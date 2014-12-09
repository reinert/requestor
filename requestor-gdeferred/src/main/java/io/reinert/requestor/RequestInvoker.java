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

import io.reinert.requestor.header.Header;

/**
 * A {@link io.reinert.requestor.Request} with dispatching capabilities.
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

    RequestPromise<Void> get();

    <T> RequestPromise<T> get(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> get(Class<T> responseType, Class<C> containerType);

    RequestPromise<Void> post();

    <T> RequestPromise<T> post(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> post(Class<T> responseType, Class<C> containerType);

    RequestPromise<Void> put();

    <T> RequestPromise<T> put(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> put(Class<T> responseType, Class<C> containerType);

    RequestPromise<Void> delete();

    <T> RequestPromise<T> delete(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> delete(Class<T> responseType, Class<C> containerType);

    RequestPromise<Void> patch();

    <T> RequestPromise<T> patch(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> patch(Class<T> responseType, Class<C> containerType);

    RequestPromise<Void> head();

    <T> RequestPromise<T> head(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> head(Class<T> responseType, Class<C> containerType);

    RequestPromise<Void> options();

    <T> RequestPromise<T> options(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> options(Class<T> responseType, Class<C> containerType);
}
