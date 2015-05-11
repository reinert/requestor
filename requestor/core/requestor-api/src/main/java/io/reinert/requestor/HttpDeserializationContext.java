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
package io.reinert.requestor;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * Context of HTTP deserialization.
 *
 * @author Danilo Reinert
 */
public class HttpDeserializationContext extends DeserializationContext {

    private final Request request;
    private final SerializedResponse response;
    private final ProviderManager providerManager;

    protected HttpDeserializationContext(Request request, SerializedResponse response, Class<?> requestedType,
                                         ProviderManager providerManager) {
        super(requestedType);
        this.request = request;
        this.response = response;
        this.providerManager = providerManager;
    }

    public <T> T getInstance(Class<T> type) {
        final Provider<T> factory = providerManager.get(type);
        if (factory == null)
            throw new UnableToDeserializeException("Could not get instance because there is no provider " +
                    "for the type " + type.getName() + " registered in the Requestor.");
        return factory.getInstance();
    }

    public Request getRequest() {
        return request;
    }

    public SerializedResponse getResponse() {
        return response;
    }
}
