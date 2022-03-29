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
package io.reinert.requestor.net.serialization;

import java.util.Collection;
import java.util.Collections;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.net.payload.BinarySerializedPayload;

/**
 * Pass-through serializer for byte array and generic media types.
 *
 * @author Danilo Reinert
 */
public class ByteSerializer implements Serializer<Byte> {

    public static String[] MEDIA_TYPE_PATTERNS = new String[]{"*/*"};

    private static final ByteSerializer INSTANCE = new ByteSerializer();

    public static ByteSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<Byte> handledType() {
        return Byte.class;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(Byte b, SerializationContext context) {
        return new BinarySerializedPayload(new byte[]{b});
    }

    @Override
    public SerializedPayload serialize(Collection<Byte> c, SerializationContext context) {
        final byte[] bytes = new byte[c.size()];
        int i = 0;
        for (Byte b : c) bytes[i++] = b;
        return new BinarySerializedPayload(bytes);
    }

    @Override
    public Byte deserialize(SerializedPayload payload, DeserializationContext context) {
        if (payload.isEmpty()) return null;
        return payload.asBytes()[0];
    }

    @Override
    public <C extends Collection<Byte>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                      DeserializationContext context) {
        if (payload.isEmpty()) return (C) Collections.<Byte>emptyList();
        final C c = context.getInstance(collectionType);
        for (byte b : payload.asBytes()) c.add(b);
        return c;
    }
}
