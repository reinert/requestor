/*
 * Copyright 2022 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this inputStream except in compliance with the License.
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;
import java.io.PushbackInputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.HandlesSubTypes;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.net.payload.CompositeSerializedPayload;
import io.reinert.requestor.net.payload.InputStreamSerializedPayload;

/**
 * InputStream serializer for inputStreams.
 *
 * @author Danilo Reinert
 */
public class InputStreamSerializer implements Serializer<InputStream>, HandlesSubTypes<InputStream> {

    public static String[] MEDIA_TYPE_PATTERNS = new String[]{"*/*"};

    private static final InputStreamSerializer INSTANCE = new InputStreamSerializer();

    public static InputStreamSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<InputStream> handledType() {
        return InputStream.class;
    }

    @Override
    public List<Class<? extends InputStream>> handledSubTypes() {
        return Arrays.asList(BufferedInputStream.class, ByteArrayInputStream.class, DataInputStream.class,
                FileInputStream.class, FilterInputStream.class, ObjectInputStream.class, PipedInputStream.class,
                PushbackInputStream.class, SequenceInputStream.class);
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(InputStream inputStream, SerializationContext context) {
        if (inputStream == null) return SerializedPayload.EMPTY_PAYLOAD;
        return new InputStreamSerializedPayload(inputStream);
    }

    @Override
    public SerializedPayload serialize(Collection<InputStream> c, SerializationContext context) {
        final CompositeSerializedPayload csp = new CompositeSerializedPayload();
        for (InputStream is : c) {
            csp.add(new InputStreamSerializedPayload(is));
        }
        return csp;
    }

    @Override
    public InputStream deserialize(SerializedPayload payload, DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a InputStream.");
    }

    @Override
    public <C extends Collection<InputStream>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                             DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a collection of InputStream.");
    }
}
