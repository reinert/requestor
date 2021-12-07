/*
 * Copyright 2014-2021 Danilo Reinert
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

    private final RequestOptions requestOptions;

    protected HttpSerializationContext(RequestOptions requestOptions, Class<?> requestedType, String... fields) {
        super(requestedType, fields);

        this.requestOptions = requestOptions;
    }

    protected HttpSerializationContext(RequestOptions requestOptions, Class<?> requestedType, Class<?> parametrizedType,
                                       String... fields) {
        super(requestedType, parametrizedType, fields);

        this.requestOptions = requestOptions;
    }

    public RequestOptions getRequestOptions() {
        return requestOptions;
    }
}
