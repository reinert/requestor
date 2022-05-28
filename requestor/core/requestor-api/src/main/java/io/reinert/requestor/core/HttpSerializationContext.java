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

import java.util.Set;

import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.UnableToSerializeException;

/**
 * Context of HTTP serialization.
 *
 * @author Danilo Reinert
 */
public class HttpSerializationContext extends SerializationContext {

    private final SerializableRequest request;
    private final ProviderManagerImpl providerManager;

    protected HttpSerializationContext(SerializableRequest request, ProviderManagerImpl providerManager,
                                       Set<String> fields, Class<?> rawType) {
        super(rawType, request.getCharset(), fields);

        this.request = request;
        this.providerManager = providerManager;
    }

    protected HttpSerializationContext(SerializableRequest request, ProviderManagerImpl providerManager,
                                       Set<String> fields, Class<?> rawType, Class<?> parametrizedType) {
        super(rawType, parametrizedType, request.getCharset(), fields);

        this.request = request;
        this.providerManager = providerManager;
    }

    public SerializableRequest getRequest() {
        return request;
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        final Provider<T> provider = providerManager.get(type);
        if (provider == null)
            throw new UnableToSerializeException("Could not get instance because there is no provider " +
                    "for the type " + type.getName() + " registered in the Session.");
        return provider.getInstance();
    }
}
