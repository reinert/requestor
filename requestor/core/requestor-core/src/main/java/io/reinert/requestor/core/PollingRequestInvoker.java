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
package io.reinert.requestor.core;

import io.reinert.requestor.core.header.Header;

/**
 * A {@link RequestBuilder} capable of sending itself.
 *
 * @see RequestInvokerImpl
 *
 * @author Danilo Reinert
 */
public interface PollingRequestInvoker extends PollingRequestBuilder, HttpPollingInvoker, RequestInvoker {

    @Override
    PollingRequestInvoker contentType(String mediaType);

    @Override
    PollingRequestInvoker accept(String mediaType);

    @Override
    PollingRequestInvoker header(String header, String value);

    @Override
    PollingRequestInvoker header(Header header);

    @Override
    PollingRequestInvoker auth(Auth auth);

    @Override
    PollingRequestInvoker auth(Auth.Provider authProvider);

    @Override
    PollingRequestInvoker timeout(int timeoutMillis);

    @Override
    PollingRequestInvoker delay(int delayMillis);

    @Override
    PollingRequestInvoker retry(int[] delaysMillis, RequestEvent... events);

    @Override
    PollingRequestInvoker payload(Object payload, String... fields) throws IllegalArgumentException;

    @Override
    PollingRequestInvoker save(String key, Object value);

    @Override
    PollingRequestInvoker poll(PollingStrategy strategy);

    @Override
    PollingRequestInvoker poll(PollingStrategy strategy, int intervalMillis);

    @Override
    PollingRequestInvoker poll(PollingStrategy strategy, int intervalMillis, int limit);

}
