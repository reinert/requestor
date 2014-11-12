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

import com.google.gwt.http.client.Header;

import io.reinert.requestor.header.AcceptHeader;

/**
 * A {@link Request} with dispatching capabilities.
 */
public interface RequestDispatcher extends Request {

    @Override
    RequestDispatcher contentType(String contentType);

    @Override
    RequestDispatcher accept(String contentType);

    @Override
    RequestDispatcher accept(AcceptHeader acceptHeader);

    @Override
    RequestDispatcher header(String header, String value);

    @Override
    RequestDispatcher header(Header header);

    @Override
    RequestDispatcher user(String user);

    @Override
    RequestDispatcher password(String password);

    @Override
    RequestDispatcher timeout(int timeoutMillis);

    @Override
    RequestDispatcher payload(Object object) throws IllegalArgumentException;

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

    RequestPromise<Void> head();

    <T> RequestPromise<T> head(Class<T> responseType);

    <T, C extends Collection> RequestPromise<Collection<T>> head(Class<T> responseType, Class<C> containerType);
}
