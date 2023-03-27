/*
 * Copyright 2023 Danilo Reinert
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
package io.reinert.requestor.gson;

import java.io.StringWriter;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;
import io.reinert.requestor.core.serialization.UnableToSerializeException;

/**
 * Universal serializer supporting any type powered by Gson.
 *
 * @author Danilo Reinert
 */
public class GsonSerializer implements Serializer<Object> {

    private final String[] mediaTypes;
    private Gson gson;

    public GsonSerializer(String... mediaTypes) {
        this(null, mediaTypes);
    }

    public GsonSerializer(Gson gson, String... mediaTypes) {
        this.gson = gson;
        if (mediaTypes.length == 0) {
            this.mediaTypes = new String[]{"*/*"};
        } else {
            this.mediaTypes = mediaTypes;
        }
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Class<Object> handledType() {
        return Object.class;
    }

    @Override
    public String[] mediaType() {
        return mediaTypes;
    }

    @Override
    public SerializedPayload serialize(Object payload, SerializationContext context) {
        try {
            final Gson gsonInstance = getGsonInstance(this.gson, context);
            final StringWriter out = new StringWriter();
            final FilterableJsonWriter fjw = new FilterableJsonWriter(out, context.getFields());
            gsonInstance.toJson(payload, payload.getClass(), fjw);
            return new TextSerializedPayload(out.toString());
        } catch (Exception e) {
            throw new UnableToSerializeException("The GsonSerializer failed to serialize the instance of " +
                    getTypesNames(context.getRawType(), context.getParameterizedTypes()) + ".", e);
        }
    }

    @Override
    public SerializedPayload serialize(Collection<Object> payload, SerializationContext context) {
        return serialize((Object) payload, context);
    }

    @Override
    public Object deserialize(SerializedPayload payload, DeserializationContext context) {
        try {
            final TypeToken<?> typeToken = TypeToken.getParameterized(context.getRawType(),
                    context.getParameterizedTypes());
            final Gson gsonInstance = getGsonInstance(this.gson, context);
            return gsonInstance.fromJson(payload.asString(), typeToken.getType());
        } catch (Exception e) {
            throw new UnableToDeserializeException("The GsonDeserializer failed to deserialize the payload to " +
                    getTypesNames(context.getRawType(), context.getParameterizedTypes()) + ".", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Collection<Object>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                        DeserializationContext context) {
        return (C) deserialize(payload, context);
    }

    private static Gson getGsonInstance(Gson gson, SerializationContext context) {
        if (gson != null) return gson;
        return context.hasProvider(Gson.class) ? context.getInstance(Gson.class) : GsonSingletonProvider.getGson();
    }

    private static Gson getGsonInstance(Gson gson, DeserializationContext context) {
        if (gson != null) return gson;
        return context.hasProvider(Gson.class) ? context.getInstance(Gson.class) : GsonSingletonProvider.getGson();
    }

    private static String getTypesNames(Class<?> rawType, Class<?>[] parameterizedTypes) {
        final StringBuilder sb = new StringBuilder(rawType.getSimpleName());
        if (parameterizedTypes.length > 0) sb.append('<');
        for (Class<?> parameterizedType : parameterizedTypes) {
            sb.append(parameterizedType.getSimpleName()).append(", ");
        }
        if (parameterizedTypes.length > 0) sb.replace(sb.length() - 2, sb.length(), ">");
        return sb.toString();
    }
}
