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

    private final String charset;
    private final Set<String> fields;
    private final Class<?> rawType;
    private final Class<?>[] parameterizedTypes;

    protected SerializationContext(String charset, Set<String> fields, Class<?> rawType,
                                   Class<?>... parameterizedTypes) {
        this.rawType = rawType;
        this.parameterizedTypes = parameterizedTypes;
        this.charset = charset;
        this.fields = fields;
    }

    public abstract <T> T getInstance(Class<T> type);

    public abstract boolean hasProvider(Class<?> type);

    public Class<?> getRawType() {
        return rawType;
    }

    public Class<?>[] getParameterizedTypes() {
        return parameterizedTypes;
    }

    public String getCharset() {
        return charset;
    }

    public Set<String> getFields() {
        return fields;
    }
}
