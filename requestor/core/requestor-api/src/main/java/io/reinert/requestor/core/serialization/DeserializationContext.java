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
 * Context of deserialization.
 *
 * @author Danilo Reinert
 */
public abstract class DeserializationContext {

    private final Class<?> rawType;
    private final Class<?> parameterizedType;
    private final String charset;

    protected DeserializationContext(Class<?> rawType, String charset) {
        this(rawType, null, charset);
    }

    protected DeserializationContext(Class<?> rawType, Class<?> parameterizedType, String charset) {
        this.rawType = rawType;
        this.parameterizedType = parameterizedType;
        this.charset = charset;
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
}
