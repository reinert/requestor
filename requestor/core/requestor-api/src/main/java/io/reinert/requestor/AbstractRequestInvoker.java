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

import io.reinert.requestor.header.Header;
import io.reinert.requestor.payload.type.CollectionPayloadType;
import io.reinert.requestor.payload.type.SinglePayloadType;
import io.reinert.requestor.uri.Uri;

/**
 * Abstract class for {@link RequestInvoker} interface.
 * It has reusable dispatch methods.
 *
 * @author Danilo Reinert
 */
abstract class AbstractRequestInvoker extends RequestBuilderImpl implements RequestInvoker {

    protected final RequestDispatcher dispatcher;

    public AbstractRequestInvoker(Uri uri, TransientStore store, RequestDispatcher dispatcher) {
        super(uri, store);

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
    public RequestInvoker auth(Auth auth) {
        super.auth(auth);
        return this;
    }

    @Override
    public RequestInvoker payload(Object payload) {
        super.payload(payload);
        return this;
    }

    @Override
    public RequestInvoker timeout(int timeoutMillis) {
        super.timeout(timeoutMillis);
        return this;
    }

    @Override
    public RequestInvoker delay(int delayMillis) {
        super.delay(delayMillis);
        return this;
    }

    @Override
    public RequestInvoker poll(PollingStrategy strategy, int intervalMillis) {
        super.poll(strategy, intervalMillis);
        return this;
    }

    @Override
    public RequestInvoker poll(PollingStrategy strategy, int intervalMillis, int limit) {
        super.poll(strategy, intervalMillis, limit);
        return this;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected <T> Promise<T> send(HttpMethod method, Class<T> entityType) {
        setMethod(method);
        return dispatcher.dispatch(build(), new SinglePayloadType<T>(entityType));
    }

    protected <T, C extends Collection<T>> Promise<Collection<T>> send(HttpMethod method,
                                                                       Class<T> entityType,
                                                                       Class<C> collectionType) {
        setMethod(method);
        return dispatcher.dispatch(build(), new CollectionPayloadType<T>(collectionType,
                new SinglePayloadType<T>(entityType)));
    }
}
