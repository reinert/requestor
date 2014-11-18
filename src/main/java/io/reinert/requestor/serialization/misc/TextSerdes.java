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
package io.reinert.requestor.serialization.misc;

import java.util.Collection;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerializationContext;

/**
 * Pass-through serdes for plain text and generic media types.
 *
 * @author Danilo Reinert
 */
public class TextSerdes implements Serdes<String> {

    public static String SEPARATOR = "\n";

    public static String[] MEDIA_TYPE_PATTERNS = new String[]{"text/plain", "*/*"};

    private static final TextSerdes INSTANCE = new TextSerdes();

    public static TextSerdes getInstance() {
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
    public String serialize(String s, SerializationContext context) {
        return s;
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
    public String serialize(Collection<String> c, SerializationContext context) {
        StringBuilder sb = new StringBuilder();
        for (String s : c) {
            sb.append(s);
            sb.append(SEPARATOR);
        }
        sb.setLength(sb.length() - SEPARATOR.length());
        return sb.toString();
    }

    @Override
    public String deserialize(String response, DeserializationContext context) {
        return response;
    }

    @Override
    public <C extends Collection<String>> C deserialize(Class<C> collectionType, String response,
                                                        DeserializationContext context) {
        final C col = context.getInstance(collectionType);
        col.add(response);
        return col;
    }
}
