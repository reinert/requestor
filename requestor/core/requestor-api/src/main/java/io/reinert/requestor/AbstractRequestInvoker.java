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

import io.reinert.requestor.auth.Auth;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.uri.Uri;

/**
 * Abstract class for {@link RequestInvoker} interface.
 * It has reusable dispatch methods.
 *
 * @author Danilo Reinert
 */
abstract class AbstractRequestInvoker extends RequestBuilderImpl implements RequestInvoker {

    protected final RequestDispatcher dispatcher;
    protected final RequestProcessor processor;

    public AbstractRequestInvoker(Uri uri, RequestDispatcher dispatcher, RequestProcessor processor) {
        super(uri);

        this.dispatcher = dispatcher;
        this.processor = processor;
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
    public RequestInvoker payload(Object payload) throws IllegalArgumentException {
        super.payload(payload);
        return this;
    }

    @Override
    public RequestInvoker timeout(int timeoutMillis) {
        super.timeout(timeoutMillis);
        return this;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected <T> Promise<T> send(HttpMethod method, Class<T> resultType) {
        setMethod(method);
        return dispatcher.dispatch(processor.process(build()), resultType);
    }

    protected <T, C extends Collection> Promise<Collection<T>> send(HttpMethod method, Class<T> resultType,
                                                                    Class<C> containerType) {
        setMethod(method);
        return dispatcher.dispatch(processor.process(build()), resultType, containerType);
    }
}
