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

import io.reinert.requestor.core.serialization.SerializationContext;

/**
 * Context of HTTP serialization.
 *
 * @author Danilo Reinert
 */
public class HttpSerializationContext extends SerializationContext {

    private final SerializableRequest request;

    protected HttpSerializationContext(SerializableRequest request, Class<?> requestedType, String... fields) {
        super(requestedType, request.getCharset(), fields);

        this.request = request;
    }

    protected HttpSerializationContext(SerializableRequest request, Class<?> requestedType, Class<?> parametrizedType,
                                       String... fields) {
        super(requestedType, parametrizedType, request.getCharset(), fields);

        this.request = request;
    }

    public SerializableRequest getRequest() {
        return request;
    }
}
