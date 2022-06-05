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
package io.reinert.requestor.core.serialization.misc;

import java.util.Arrays;
import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;

/**
 * Pass-through serializer for plain text and generic media types.
 *
 * @author Danilo Reinert
 */
public class TextSerializer implements Serializer<String> {

    public static String SEPARATOR = "\n";

    public static String[] MEDIA_TYPE_PATTERNS = new String[]{"*/*"};

    private static final TextSerializer INSTANCE = new TextSerializer();

    public static TextSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(String s, SerializationContext context) {
        if (s.length() == 0) return SerializedPayload.EMPTY_PAYLOAD;
        return new TextSerializedPayload(s, context.getCharset());
    }

    /**
     * Serialize a collection of strings separating them with the modifiable TextDeserializer#SEPARATOR string.
     *
     * @param c        The collection of the object to be serialized
     * @param context   Context of the serialization
     *
     * @return  The serialized string
     */
    @Override
    public SerializedPayload serialize(Collection<String> c, SerializationContext context) {
        StringBuilder sb = new StringBuilder();
        for (String s : c) {
            sb.append(s);
            sb.append(SEPARATOR);
        }
        sb.setLength(sb.length() - SEPARATOR.length());
        return new TextSerializedPayload(sb.toString(), context.getCharset());
    }

    @Override
    public String deserialize(SerializedPayload payload, DeserializationContext context) {
        return payload.asString();
    }

    @Override
    public <C extends Collection<String>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                        DeserializationContext context) {
        final C col = context.getInstance(collectionType);
        col.addAll(Arrays.asList(payload.asString().split(SEPARATOR)));
        return col;
    }
}
