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
package io.reinert.requestor.serialization.json;

import java.util.Collection;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * Base class for all SerDes that manipulates serialized JSON.
 *
 * @param <T>   Type of the object to be serialized/deserialized
 *
 * @author Danilo Reinert
 */
public abstract class JsonSerdes<T> implements Serdes<T> {

    public static String[] ACCEPT_PATTERNS = new String[] { "application/json", "application/javascript" };
    public static String[] CONTENT_TYPE_PATTERNS = new String[] { "application/json", "application/javascript" };

    private final Class<T> handledType;

    protected JsonSerdes(Class<T> handledType) {
        this.handledType = handledType;
    }

    @Override
    public Class<T> handledType() {
        return handledType;
    }

    @Override
    public String[] accept() {
        return ACCEPT_PATTERNS;
    }

    @Override
    public String[] contentType() {
        return CONTENT_TYPE_PATTERNS;
    }

    /**
     * Given a collection class, returns a new instance of it.
     *
     * @param collectionType    The class of the collection.
     * @param <C>               The type of the collection.
     *
     * @return A new instance to the collection.
     */
    public <C extends Collection<T>> C getCollectionInstance(DeserializationContext context, Class<C> collectionType) {
        final C col = context.getContainerInstance(collectionType);
        if (col == null)
            throw new UnableToDeserializeException("Could not instantiate the given collection type.");
        return col;
    }

    @Override
    public String serializeFromCollection(Collection<T> c, SerializationContext context) {
        if (c == null) return null;
        StringBuilder serialized = new StringBuilder("[");
        for (T t : c) {
            serialized.append(serialize(t, context)).append(',');
        }
        serialized.setCharAt(serialized.length() - 1, ']');
        return serialized.toString();
    }

    protected boolean isArray(String text) {
        final String trim = text.trim();
        return trim.startsWith("[") && trim.endsWith("]");
    }
}
