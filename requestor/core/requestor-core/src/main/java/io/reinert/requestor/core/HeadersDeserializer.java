/*
 * Copyright 2022 Danilo Reinert
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

import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.Deserializer;

/**
 * Serializer to retrieve the Headers from a response.
 *
 * @author Danilo Reinert
 */
public class HeadersDeserializer implements Deserializer<Headers> {

    public static final String MEDIA_TYPE = "*/*";

    private static final HeadersDeserializer INSTANCE = new HeadersDeserializer();

    public static HeadersDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<Headers> handledType() {
        return Headers.class;
    }

    @Override
    public String[] mediaType() {
        return new String[] { MEDIA_TYPE };
    }

    @Override
    public Headers deserialize(SerializedPayload payload, DeserializationContext context) {
        HttpDeserializationContext httpCtx = (HttpDeserializationContext) context;
        return httpCtx.getResponse().getHeaders();
    }

    @Override
    public <C extends Collection<Headers>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                         DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a collection of Headers.");
    }

}
