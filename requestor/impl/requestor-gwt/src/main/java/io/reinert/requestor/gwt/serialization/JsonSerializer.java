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
package io.reinert.requestor.gwt.serialization;

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;

/**
 * Base class for all Serializers that manipulates serialized JSON.
 *
 * @param <T>   Type of the object to be serialized/deserialized
 *
 * @author Danilo Reinert
 */
public abstract class JsonSerializer<T> implements Serializer<T> {

    public static String[] MEDIA_TYPE_PATTERNS = new String[] { "application/json", "application/javascript" };

    private final Class<T> handledType;

    protected JsonSerializer(Class<T> handledType) {
        this.handledType = handledType;
    }

    @Override
    public Class<T> handledType() {
        return handledType;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(Collection<T> c, SerializationContext context) {
        StringBuilder serialized = new StringBuilder("[");
        for (T t : c) {
            serialized.append(serialize(t, context).asText()).append(',');
        }
        serialized.setCharAt(serialized.length() - 1, ']');
        return new SerializedPayload(serialized.toString());
    }

    protected boolean isArray(String text) {
        final String trim = text.trim();
        return trim.startsWith("[") && trim.endsWith("]");
    }

    // Used by JsonAutoBeanGenerator
    public static native String stringify(JavaScriptObject jso) /*-{
        return JSON.stringify(jso);
    }-*/;
}
