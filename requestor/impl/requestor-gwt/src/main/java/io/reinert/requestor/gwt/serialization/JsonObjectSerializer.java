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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;

/**
 * Base class for all Serializers that manipulates serialized JSON objects.
 *
 * @param <T>   Type of the object to serialize/deserialize.
 *
 * @author Danilo Reinert
 */
public abstract class JsonObjectSerializer<T> extends JsonSerializer<T> {

    public JsonObjectSerializer(Class<T> handledType) {
        super(handledType);
    }

    /**
     * Verifies if the deserializer should evaluate the response safely.
     * <p></p>
     * If this method returns <code>true</code>, then the deserializer will evaluate the response using
     * {@link com.google.gwt.core.client.JsonUtils#safeEval(String)}, otherwise it will use
     * {@link com.google.gwt.core.client.JsonUtils#unsafeEval(String)}.
     * <p></p>
     * If you are completely sure you'll will always receive safe contents, then you can override it
     * to return <code>false</code> and you'll benefit a faster deserialization.
     * <p></p>
     * The default implementation is <code>true</code>.
     *
     * @return  <code>true</code> if you want to evaluate response safely,
     *          or <code>false</code> to evaluate unsafely
     */
    public boolean useSafeEval() {
        return true;
    }

    /**
     * Recover an instance of T from deserialized JSON.
     *
     * @param reader    The evaluated response
     * @param context   Context of the deserialization
     *
     * @return The object deserialized
     */
    public abstract T readJson(JsonRecordReader reader, DeserializationContext context);

    /**
     * Build a JSON using {@link JsonRecordWriter}.
     * Later this JSON will be serialized using JSON#stringify.
     *
     * @param t         The object to be serialized
     * @param writer    The serializing JSON
     * @param context   Context of the serialization
     */
    public abstract void writeJson(T t, JsonRecordWriter writer, SerializationContext context);

    @Override
    public T deserialize(String response, DeserializationContext context) {
        if (!isObject(response))
            throw new UnableToDeserializeException("Response content is not an object");

        final JavaScriptObject deserialized = eval(response);
        return readJson((JsonRecordReader) deserialized, context);
    }

    @Override
    public <C extends Collection<T>> C deserialize(Class<C> collectionType, String response,
                                                   DeserializationContext context) {
        if (!isArray(response))
            throw new UnableToDeserializeException("Response content is not an array.");

        C col = context.getInstance(collectionType);
        @SuppressWarnings("unchecked")
        JsArray<JavaScriptObject> jsArray = (JsArray<JavaScriptObject>) eval(response);
        for (int i = 0; i < jsArray.length(); i++) {
            JavaScriptObject jso = jsArray.get(i);
            col.add(readJson((JsonRecordReader) jso, context));
        }
        return col;
    }

    @Override
    public String serialize(T t, SerializationContext context) {
        final JsonRecordWriter writer = JsonRecordWriter.create();
        writeJson(t, writer, context);
        return stringify(writer);
    }

    /**
     * Checks if the serialized content is a JSON Object.
     *
     * @param text Serialized response
     *
     * @return {@code true} if argument is a JSON object, {@code false} otherwise
     */
    protected boolean isObject(String text) {
        final String trim = text.trim();
        return trim.startsWith("{") && trim.endsWith("}");
    }

    /**
     * Performs evaluation of serialized response obeying the #useSafeEval configuration.
     * <p></p>
     *
     * If #useSafeEval is {@code true} then the eval is performed using {@link JsonUtils#safeEval},
     * otherwise then content will be loosely evaluated by {@link JsonUtils#unsafeEval}.
     *
     * @param response The serialized content
     *
     * @return The converted JavaScriptObject
     */
    protected JavaScriptObject eval(String response) {
        return useSafeEval() ? JsonUtils.safeEval(response) : JsonUtils.unsafeEval(response);
    }
}
