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
package io.reinert.requestor.java.serialization;

import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.java.payload.BinarySerializedPayload;

/**
 * Pass-through serializer for byte array and generic media types.
 *
 * @author Danilo Reinert
 */
public class BinarySerializer implements Serializer<byte[]> {

    public static String[] MEDIA_TYPE_PATTERNS = new String[]{"*/*"};

    private static final BinarySerializer INSTANCE = new BinarySerializer();

    public static BinarySerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<byte[]> handledType() {
        return byte[].class;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(byte[] bytes, SerializationContext context) {
        if (bytes.length == 0) return SerializedPayload.EMPTY_PAYLOAD;
        return new BinarySerializedPayload(bytes);
    }

    @Override
    public SerializedPayload serialize(Collection<byte[]> c, SerializationContext context) {
        throw new UnsupportedOperationException("Cannot serialize a collection of byte[].");
    }

    @Override
    public byte[] deserialize(SerializedPayload payload, DeserializationContext context) {
        return payload.asBytes();
    }

    @Override
    public <C extends Collection<byte[]>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                        DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a collection of byte[].");
    }
}
