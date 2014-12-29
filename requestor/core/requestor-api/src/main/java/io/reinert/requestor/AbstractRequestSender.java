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

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.deferred.Promise;
import io.reinert.requestor.header.Header;

/**
 * Abstract class for {@link RequestSender} interface.
 * It has ready {@link io.reinert.requestor.deferred.Promise} auto-casting dispatch methods.
 * Requestor API implementers should inherit this class to dispatch the requests.
 *
 * @author Danilo Reinert
 */
abstract class AbstractRequestSender extends RequestBuilderImpl implements RequestSender {

    protected final RequestDispatcher dispatcher;
    protected final RequestProcessor processor;

    public AbstractRequestSender(String url, RequestDispatcher dispatcher, RequestProcessor processor) {
        super(url);
        this.dispatcher = dispatcher;
        this.processor = processor;
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public RequestSender accept(String mediaType) {
        super.accept(mediaType);
        return this;
    }

    @Override
    public RequestSender contentType(String mediaType) {
        super.contentType(mediaType);
        return this;
    }

    @Override
    public RequestSender header(String header, String value) {
        super.header(header, value);
        return this;
    }

    @Override
    public RequestSender header(Header header) {
        super.header(header);
        return this;
    }

    @Override
    public RequestSender auth(Authentication auth) {
        super.auth(auth);
        return this;
    }

    @Override
    public RequestSender payload(Object object) throws IllegalArgumentException {
        super.payload(object);
        return this;
    }

    @Override
    public RequestSender timeout(int timeoutMillis) {
        super.timeout(timeoutMillis);
        return this;
    }

    @Override
    public RequestSender responseType(ResponseType responseType) {
        super.responseType(responseType);
        return this;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    @SuppressWarnings("unchecked")
    protected <T, P extends Promise<T>> P send(HttpMethod method, Class<T> resultType) {
        setMethod(method);
        return (P) dispatcher.dispatch(processor.process(build()), resultType);
    }

    @SuppressWarnings("unchecked")
    protected <T, C extends Collection, P extends Promise<Collection<T>>> P send(HttpMethod method, Class<T> resultType,
                                                                                 Class<C> containerType) {
        setMethod(method);
        return (P) dispatcher.dispatch(processor.process(build()), resultType, containerType);
    }
}
