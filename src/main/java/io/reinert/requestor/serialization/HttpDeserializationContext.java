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
package io.reinert.requestor.serialization;

import io.reinert.requestor.Headers;
import io.reinert.requestor.ProviderManager;

/**
 * Context of HTTP deserialization.
 *
 * @author Danilo Reinert
 */
public class HttpDeserializationContext extends DeserializationContext {

    private final Headers headers;

    public HttpDeserializationContext(Headers headers, ProviderManager providerManager, Class<?> requestedType) {
        super(providerManager, requestedType);
        this.headers = headers;
    }

    public Headers getHeaders() {
        return headers;
    }
}
