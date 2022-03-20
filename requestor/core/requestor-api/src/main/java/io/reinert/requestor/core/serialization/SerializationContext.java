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
package io.reinert.requestor.core.serialization;

/**
 * Context of serialization.
 *
 * @author Danilo Reinert
 */
public abstract class SerializationContext {

    private final Class<?> requestedType;
    private final Class<?> parametrizedType;
    private final String charset;
    private final String[] fields;

    protected SerializationContext(Class<?> requestedType, String charset, String... fields) {
        this(requestedType, null, charset, fields);
    }

    protected SerializationContext(Class<?> requestedType, Class<?> parametrizedType, String charset,
                                   String... fields) {
        this.requestedType = requestedType;
        this.parametrizedType = parametrizedType;
        this.charset = charset;
        this.fields = fields;
    }

    public Class<?> getRequestedType() {
        return requestedType;
    }

    public Class<?> getParametrizedType() {
        return parametrizedType;
    }

    public String getCharset() {
        return charset;
    }

    public String[] getFields() {
        return fields;
    }
}
