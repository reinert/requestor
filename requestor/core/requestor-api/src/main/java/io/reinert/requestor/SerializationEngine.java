/*
 * Copyright 2021 Danilo Reinert
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
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.requestor.form.FormData;
import io.reinert.requestor.form.FormDataSerializer;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.SerializationException;
import io.reinert.requestor.serialization.Serializer;

/**
 * Responsible for performing managed de/serialization.
 *
 * @author Danilo Reinert
 */
class SerializationEngine {

    private static Logger logger = Logger.getLogger(SerializationEngine.class.getName());

    private final SerializerManagerImpl serializerManager;
    private final ProviderManagerImpl providerManager;
    private final FormDataSerializer formDataSerializer;

    public SerializationEngine(SerializerManagerImpl serializerManager, ProviderManagerImpl providerManager,
                               FormDataSerializer formDataSerializer) {
        this.serializerManager = serializerManager;
        this.providerManager = providerManager;
        this.formDataSerializer = formDataSerializer;
    }

    public <T, C extends Collection<T>> void deserializeResponse(Request request, RawResponse response,
                                                                 Class<T> entityType, Class<C> collectionType) {
        C result = deserializePayload(request, response, entityType, collectionType);
        response.setDeserializedPayload(result);
    }

    public <T> void deserializeResponse(Request request, RawResponse response, Class<T> entityType) {
        T result = deserializePayload(request, response, entityType);
        response.setDeserializedPayload(result);
    }

    public <T, C extends Collection<T>> C deserializePayload(Request request, Response response,
                                                             Class<T> entityType, Class<C> collectionType) {
        final String mediaType = getResponseMediaType(request, response);
        final Deserializer<T> deserializer = serializerManager.getDeserializer(entityType, mediaType);
        checkDeserializerNotNull(response, entityType, deserializer);
        final DeserializationContext context = new HttpDeserializationContext(request, response, providerManager,
                entityType);
        return deserializer.deserialize(collectionType, response.getSerializedPayload().isString(), context);
    }

    public <T> T deserializePayload(Request request, Response response, Class<T> entityType) {
        final String mediaType = getResponseMediaType(request, response);
        final Deserializer<T> deserializer = serializerManager.getDeserializer(entityType, mediaType);
        checkDeserializerNotNull(response, entityType, deserializer);
        final DeserializationContext context = new HttpDeserializationContext(request, response, providerManager,
                entityType);
        return deserializer.deserialize(response.getSerializedPayload().isString(), context);
    }

    public void serializeRequest(SerializableRequest request) {
        Object payload = request.getPayload();

        if (payload instanceof FormData) {
            // TODO: refactor FormDataSerializer to extend Serializer and register as a regular serializer in the module
            // maybe extract contentType matching from SerializationEngine

            // FormData serialization
            final Payload serializedPayload = formDataSerializer.serialize((FormData) payload);

            // If mediaType is null then content-type header is removed and the browser handles it
            request.setContentType(formDataSerializer.mediaType());

            request.serializePayload(serializedPayload);

            return;
        }

        if (payload instanceof Payload) {
            request.serializePayload((Payload) payload);

            return;
        }

        String body = null;
        if (payload != null) { // Checks whether there's a payload to serialized
            final String mediaType = getRequestMediaType(request);
            if (payload instanceof Collection) {
                // Proceed to obtain the class instance of the first non-null element in this collection,
                // in order to retrieve the corresponding serializer
                Collection c = (Collection) payload;
                final Iterator iterator = c.iterator();
                Object item = null;
                // Get the first non-null element to obtain its class instance
                while (iterator.hasNext() && item == null) {
                    item = iterator.next();
                }
                if (item == null) {
                    // There were no non-null elements in the collection.
                    // An empty array is then assumed.
                    // TODO: provide some way of configuring this behavior
                    body = isJsonMediaType(mediaType) ? "[]" : "";
                } else {
                    final Class<?> type = item.getClass();
                    final Class<? extends Collection> collectionType = c.getClass();

                    Serializer<?> serializer = serializerManager.getSerializer(type, mediaType);
                    checkSerializerNotNull(request, type, serializer);
                    body = serializer.serialize(c, new HttpSerializationContext(request, collectionType, type));
                }
            } else {
                final Class<?> type = payload.getClass();
                @SuppressWarnings("unchecked")
                Serializer<Object> serializer = (Serializer<Object>) serializerManager.getSerializer(type, mediaType);
                checkSerializerNotNull(request, payload.getClass(), serializer);
                body = serializer.serialize(payload, new HttpSerializationContext(request, type));
            }
        }

        request.serializePayload(Payload.fromText(body));
    }

    private String getRequestMediaType(SerializableRequest request) {
        String mediaType = request.getContentType();
        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "*/*";
            logger.log(Level.INFO, "Request with no 'Content-Type' header being dispatched to '" + request.getUri()
                    + "'. The content-type value has been automatically set to '*/*' to match serializers.");
        } else {
            mediaType = extractMediaTypeFromContentType(mediaType);
        }
        return mediaType;
    }

    private String getResponseMediaType(Request request, Response response) {
        String medaType = response.getContentType();
        if (medaType == null || medaType.isEmpty()) {
            medaType = "*/*";
            logger.log(Level.INFO, "Response with no 'Content-Type' header received from '" + request.getUri()
                    + "'. The content-type value has been automatically set to '*/*' to match deserializers.");
        } else {
            medaType = extractMediaTypeFromContentType(medaType);
        }
        return medaType;
    }

    private String extractMediaTypeFromContentType(String contentType) {
        return contentType.split(";")[0];
    }

    private void checkDeserializerNotNull(Response response, Class<?> type, Deserializer<?> deserializer) {
        if (deserializer == null) {
            throw new SerializationException("Could not find Deserializer for class '" + type.getName() + "' and " +
                    "media-type '" + response.getContentType() + "'.");
        }
    }

    private void checkSerializerNotNull(Request request, Class<?> type, Serializer<?> serializer) {
        if (serializer == null) {
            throw new SerializationException("Could not find Serializer for class '" + type.getName() + "' and " +
                    "media-type '" + request.getContentType() + "'.");
        }
    }

    private boolean isJsonMediaType(String mediaType) {
        return mediaType.contains("json") || mediaType.contains("javascript");
    }
}
