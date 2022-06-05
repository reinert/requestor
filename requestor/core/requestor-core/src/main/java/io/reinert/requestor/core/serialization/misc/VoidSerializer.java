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
package io.reinert.requestor.core.serialization.misc;

import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;

/**
 * Serializer for Void type.
 * Returns null for every method.
 *
 * @author Danilo Reinert
 */
public class VoidSerializer implements Serializer<Void> {

    public static final String[] MEDIA_TYPE_PATTERNS = new String[] {"*/*"};

    private static final VoidSerializer INSTANCE = new VoidSerializer();

    public static VoidSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<Void> handledType() {
        return Void.class;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public Void deserialize(SerializedPayload payload, DeserializationContext context) {
        return null;
    }

    @Override
    public <C extends Collection<Void>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                      DeserializationContext context) {
        return null;
    }

    @Override
    public SerializedPayload serialize(Void v, SerializationContext context) {
        return SerializedPayload.EMPTY_PAYLOAD;
    }

    @Override
    public SerializedPayload serialize(Collection<Void> c, SerializationContext context) {
        return SerializedPayload.EMPTY_PAYLOAD;
    }
}
