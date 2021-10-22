/*
 * Copyright 2015 Danilo Reinert
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

import javax.annotation.Nullable;

/**
 * Context of serialization.
 *
 * @author Danilo Reinert
 */
public abstract class SerializationContext {

    private final Class<?> requestedType;
    private final Class<?> parametrizedType;

    protected SerializationContext(Class<?> requestedType) {
        this(requestedType, null);
    }

    protected SerializationContext(Class<?> requestedType, Class<?> parametrizedType) {
        this.requestedType = requestedType;
        this.parametrizedType = parametrizedType;
    }

    public Class<?> getRequestedType() {
        return requestedType;
    }

    @Nullable
    public Class<?> getParametrizedType() {
        return parametrizedType;
    }
}
