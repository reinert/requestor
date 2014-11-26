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
 * Default implementation for {@link RequestInvoker}.
 *
 * @author Danilo Reinert
 */
public class RequestInvokerImpl extends RequestBuilderImpl implements RequestInvoker {

    private final RequestProcessor processor;
    private final RequestDispatcher dispatcher;

    public RequestInvokerImpl(String url, RequestProcessor processor, RequestDispatcher dispatcher) {
        super(url);
        this.processor = processor;
        this.dispatcher = dispatcher;
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public RequestInvoker accept(String mediaType) {
        super.accept(mediaType);
        return this;
    }

    @Override
    public RequestInvoker contentType(String mediaType) {
        super.contentType(mediaType);
        return this;
    }

    @Override
    public RequestInvoker header(String header, String value) {
        super.header(header, value);
        return this;
    }

    @Override
    public RequestInvoker header(Header header) {
        super.header(header);
        return this;
    }

    @Override
    public RequestInvoker password(String password) {
        super.password(password);
        return this;
    }

    @Override
    public RequestInvoker payload(Object object) throws IllegalArgumentException {
        super.payload(object);
        return this;
    }

    @Override
    public RequestInvoker timeout(int timeoutMillis) {
        super.timeout(timeoutMillis);
        return this;
    }

    @Override
    public RequestInvoker user(String user) {
        super.user(user);
        return this;
    }

    @Override
    public RequestInvoker responseType(ResponseType responseType) {
        super.responseType(responseType);
        return this;
    }

    //===================================================================
    // RequestInvoker methods
    //===================================================================

    @Override
    public RequestPromise<Void> get() {
        return send("GET", Void.class);
    }

    @Override
    public <T> RequestPromise<T> get(Class<T> responseType) {
        return send("GET", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> get(Class<T> responseType, Class<C> containerType) {
        return send("GET", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> post() {
        return send("POST", Void.class);
    }

    @Override
    public <T> RequestPromise<T> post(Class<T> responseType) {
        return send("POST", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> post(Class<T> responseType, Class<C> containerType) {
        return send("POST", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> put() {
        return send("PUT", Void.class);
    }

    @Override
    public <T> RequestPromise<T> put(Class<T> responseType) {
        return send("PUT", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> put(Class<T> responseType, Class<C> containerType) {
        return send("PUT", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> delete() {
        return send("DELETE", Void.class);
    }

    @Override
    public <T> RequestPromise<T> delete(Class<T> responseType) {
        return send("DELETE", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> delete(Class<T> responseType,
                                                                          Class<C> containerType) {
        return send("DELETE", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> patch() {
        return send("PATCH", Void.class);
    }

    @Override
    public <T> RequestPromise<T> patch(Class<T> responseType) {
        return send("PATCH", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> patch(Class<T> responseType,
                                                                         Class<C> containerType) {
        return send("PATCH", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> head() {
        return send("HEAD", Void.class);
    }

    @Override
    public <T> RequestPromise<T> head(Class<T> responseType) {
        return send("HEAD", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> head(Class<T> responseType, Class<C> containerType) {
        return send("HEAD", responseType, containerType);
    }

    @Override
    public RequestPromise<Void> options() {
        return send("OPTIONS", Void.class);
    }

    @Override
    public <T> RequestPromise<T> options(Class<T> responseType) {
        return send("OPTIONS", responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> options(Class<T> responseType,
                                                                           Class<C> containerType) {
        return send("OPTIONS", responseType, containerType);
    }

    //===================================================================
    // Internal methods
    //===================================================================

    private <T> RequestPromise<T> send(String method, Class<T> responseType) {
        setMethod(method);
        return dispatcher.dispatch(processor.process(build()), responseType);
    }

    private <T, C extends Collection> RequestPromise<Collection<T>> send(String method, Class<T> responseType,
                                                                         Class<C> containerType) {
        setMethod(method);
        return dispatcher.dispatch(processor.process(build()), responseType, containerType);
    }
}
