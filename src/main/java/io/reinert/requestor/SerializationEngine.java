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
package io.reinert.requestor;

import java.util.Collection;
import java.util.Iterator;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.SerdesManager;
import io.reinert.requestor.serialization.Serializer;

/**
 * Responsible for performing managed de/serialization.
 *
 * @author Danilo Reinert
 */
public class SerializationEngine {

    private final SerdesManager serdesManager;
    private final ProviderManager providerManager;

    public SerializationEngine(SerdesManager serdesManager, ProviderManager providerManager) {
        this.serdesManager = serdesManager;
        this.providerManager = providerManager;
    }

    public <T, C extends Collection> Collection<T> deserialize(String payload, Class<T> type, Class<C> containerType,
                                                               String mediaType, String url, Headers headers) {
        final Deserializer<T> deserializer = serdesManager.getDeserializer(type, mediaType);
        final DeserializationContext context = new HttpDeserializationContext(url, headers, type, providerManager);
        @SuppressWarnings("unchecked")
        Collection<T> result = deserializer.deserialize(containerType, payload, context);
        return result;
    }

    public <T> T deserialize(String payload, Class<T> type, String mediaType, String url, Headers headers) {
        final Deserializer<T> deserializer = serdesManager.getDeserializer(type, mediaType);
        final DeserializationContext context = new HttpDeserializationContext(url, headers, type, providerManager);
        return deserializer.deserialize(payload, context);
    }

    @SuppressWarnings("unchecked")
    public String serialize(Object payload, String mediaType, String url, Headers headers) {
        String body = null;
        if (payload != null) {
            if (payload instanceof Collection) {
                Collection c = (Collection) payload;
                final Iterator iterator = c.iterator();
                Object item = null;
                while (iterator.hasNext() && item == null) {
                    item = iterator.next();
                }
                if (item == null) {
                    /* FIXME: This is forcing empty collections responses to be a empty json array.
                        It will cause error for serializers expecting other content-type (e.g. XML).
                       TODO: Create some EmptyCollectionSerializerManager for serialization of empty collections
                        by content-type. */
                    body = "[]";
                } else {
                    Serializer<?> serializer = serdesManager.getSerializer(item.getClass(), mediaType);
                    body = serializer.serialize(c, new HttpSerializationContext(url, headers));
                }
            } else {
                Serializer<Object> serializer = (Serializer<Object>) serdesManager.getSerializer(payload.getClass(),
                        mediaType);
                body = serializer.serialize(payload, new HttpSerializationContext(url, headers));
            }
        }
        return body;
    }
}
