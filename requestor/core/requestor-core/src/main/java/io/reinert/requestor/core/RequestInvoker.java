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

import io.reinert.requestor.core.header.Header;

/**
 * A {@link RequestBuilder} capable of sending itself.
 *
 * @see RequestInvokerImpl
 *
 * @author Danilo Reinert
 */
public interface RequestInvoker extends RequestBuilder, HttpInvoker {

    @Override
    RequestInvoker contentType(String mediaType);

    @Override
    RequestInvoker accept(String mediaType);

    @Override
    RequestInvoker header(String header, String value);

    @Override
    RequestInvoker header(Header header);

    @Override
    RequestInvoker charset(String charset);

    @Override
    RequestInvoker auth(Auth auth);

    @Override
    RequestInvoker auth(Auth.Provider authProvider);

    @Override
    RequestInvoker timeout(int timeoutMillis);

    @Override
    RequestInvoker delay(int delayMillis);

    @Override
    RequestInvoker retry(int[] delaysMillis, RequestEvent... events);

    @Override
    RequestInvoker retry(RetryPolicy retryPolicy);

    @Override
    RequestInvoker retry(RetryPolicy.Provider retryPolicyProvider);

    @Override
    RequestInvoker payload(Object payload, String... fields) throws IllegalArgumentException;

    @Override
    RequestInvoker skip(Process... processes);

    @Override
    RequestInvoker save(String key, Object value);

    @Override
    PollingRequestInvoker poll(PollingStrategy strategy);

    @Override
    PollingRequestInvoker poll(PollingStrategy strategy, int intervalMillis);

    @Override
    PollingRequestInvoker poll(PollingStrategy strategy, int intervalMillis, int limit);

}
