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
import io.reinert.requestor.serialization.Deserializer;

/**
 * Pass-through deserializer for plain text and generic stuff.
 *
 * @author Danilo Reinert
 */
public class TextDeserializer implements Deserializer<String> {

    public static String[] ACCEPT_PATTERNS = new String[]{"text/plain", "*/*"};

    private static final TextDeserializer INSTANCE = new TextDeserializer();

    public static TextDeserializer getInstance() {
        return INSTANCE;
    }

    /**
     * Method for accessing type of Objects this deserializer can handle.
     *
     * @return The class which this deserializer can deserialize
     */
    @Override
    public Class<String> handledType() {
        return String.class;
    }

    /**
     * Informs the content type this serializer handle.
     *
     * @return The content type handled by this serializer.
     */
    @Override
    public String[] accept() {
        return ACCEPT_PATTERNS;
    }

    /**
     * Deserialize the plain text into an object of type T.
     *
     * @param response Http response body content
     * @param context  Context of deserialization
     *
     * @return The object deserialized
     */
    @Override
    public String deserialize(String response, DeserializationContext context) {
        return response;
    }

    /**
     * Deserialize the plain text into an object of type T.
     *
     * @param collectionType The class of the collection
     * @param response       Http response body content
     * @param context        Context of deserialization
     *
     * @return The object deserialized
     */
    @Override
    public <C extends Collection<String>> C deserializeAsCollection(Class<C> collectionType, String response,
                                                                    DeserializationContext context) {
        final C col = context.getContainerInstance(collectionType);
        col.add(response);
        return col;
    }
}
