/*
 * Copyright 2014-2022 Danilo Reinert
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
import java.util.Map;

import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.type.CollectionPayloadType;
import io.reinert.requestor.core.payload.type.MapPayloadType;
import io.reinert.requestor.core.payload.type.SinglePayloadType;
import io.reinert.requestor.core.uri.Uri;

/**
 * Abstract class for {@link RequestInvoker} interface.
 * It has reusable dispatch methods.
 *
 * @author Danilo Reinert
 */
abstract class AbstractRequestInvoker extends RequestBuilderImpl implements PollingRequestInvoker {

    protected final RequestDispatcher dispatcher;

    public AbstractRequestInvoker(Session session, Uri uri, LeafStore store, RequestDispatcher dispatcher) {
        super(session, uri, store);

        this.dispatcher = dispatcher;
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public AbstractRequestInvoker accept(String mediaType) {
        super.accept(mediaType);
        return this;
    }

    @Override
    public AbstractRequestInvoker contentType(String mediaType) {
        super.contentType(mediaType);
        return this;
    }

    @Override
    public AbstractRequestInvoker header(String header, String value) {
        super.header(header, value);
        return this;
    }

    @Override
    public AbstractRequestInvoker header(Header header) {
        super.header(header);
        return this;
    }

    @Override
    public AbstractRequestInvoker auth(Auth auth) {
        super.auth(auth);
        return this;
    }

    @Override
    public AbstractRequestInvoker auth(Auth.Provider authProvider) {
        super.auth(authProvider);
        return this;
    }

    @Override
    public AbstractRequestInvoker payload(Object payload, String... fields) {
        super.payload(payload, fields);
        return this;
    }

    @Override
    public AbstractRequestInvoker timeout(int timeoutMillis) {
        super.timeout(timeoutMillis);
        return this;
    }

    @Override
    public AbstractRequestInvoker delay(int delayMillis) {
        super.delay(delayMillis);
        return this;
    }

    @Override
    public AbstractRequestInvoker charset(String charset) {
        super.charset(charset);
        return this;
    }

    @Override
    public AbstractRequestInvoker retry(int[] delaysMillis, RequestEvent... events) {
        super.retry(delaysMillis, events);
        return this;
    }

    @Override
    public AbstractRequestInvoker retry(RetryPolicy retryPolicy) {
        super.retry(retryPolicy);
        return this;
    }

    @Override
    public AbstractRequestInvoker retry(RetryPolicy.Provider retryPolicyProvider) {
        super.retry(retryPolicyProvider);
        return this;
    }

    @Override
    public AbstractRequestInvoker skip(Process... processes) {
        super.skip(processes);
        return this;
    }

    @Override
    public AbstractRequestInvoker save(String key, Object value, Level level) {
        super.save(key, value, level);
        return this;
    }

    @Override
    public AbstractRequestInvoker save(String key, Object value) {
        super.save(key, value);
        return this;
    }

    @Override
    public AbstractRequestInvoker poll(PollingStrategy strategy) {
        super.poll(strategy);
        return this;
    }

    @Override
    public AbstractRequestInvoker poll(PollingStrategy strategy, int intervalMillis) {
        super.poll(strategy, intervalMillis);
        return this;
    }

    @Override
    public AbstractRequestInvoker poll(PollingStrategy strategy, int intervalMillis, int limit) {
        super.poll(strategy, intervalMillis, limit);
        return this;
    }

    //===================================================================
    // Internal methods
    //===================================================================

    protected <T> PollingRequest<T> send(HttpMethod method, Class<T> entityType) {
        setMethod(method);
        return dispatcher.dispatch(build(), new SinglePayloadType<T>(entityType));
    }

    protected <T, C extends Collection<T>> PollingRequest<Collection<T>> send(HttpMethod method,
                                                                              Class<T> entityType,
                                                                              Class<C> collectionType) {
        setMethod(method);
        return dispatcher.dispatch(build(), new CollectionPayloadType<T>(collectionType,
                new SinglePayloadType<T>(entityType)));
    }

    protected <V, K, M extends Map<K, V>> PollingRequest<Map<K, V>> send(HttpMethod method,
                                                                         Class<V> valueType,
                                                                         Class<K> keyType,
                                                                         Class<M> mapType) {
        setMethod(method);
        return dispatcher.dispatch(build(), new MapPayloadType<V, K>(new SinglePayloadType<V>(valueType), keyType,
                mapType));
    }
}
