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

import java.util.Set;

/**
 * Context of serialization.
 *
 * @author Danilo Reinert
 */
public abstract class SerializationContext {

    private final Class<?> rawType;
    private final Class<?> parameterizedType;
    private final String charset;
    private final Set<String> fields;

    protected SerializationContext(Class<?> rawType, String charset, Set<String> fields) {
        this(rawType, null, charset, fields);
    }

    protected SerializationContext(Class<?> rawType, Class<?> parameterizedType, String charset,
                                   Set<String> fields) {
        this.rawType = rawType;
        this.parameterizedType = parameterizedType;
        this.charset = charset;
        this.fields = fields;
    }

    public abstract <T> T getInstance(Class<T> type);

    public Class<?> getRawType() {
        return rawType;
    }

    public Class<?> getParameterizedType() {
        return parameterizedType;
    }

    public String getCharset() {
        return charset;
    }

    public Set<String> getFields() {
        return fields;
    }
}
