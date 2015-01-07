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
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private final SerdesManager serdesManager;
    private final ProviderManager providerManager;

    public SerializationEngine(SerdesManager serdesManager, ProviderManager providerManager) {
        this.serdesManager = serdesManager;
        this.providerManager = providerManager;
    }

    public <T, C extends Collection> Response<Collection<T>> deserializeResponse(Request request,
                                                                                 SerializedResponse response,
                                                                                 Class<T> type,
                                                                                 Class<C> containerType) {
        final String mediaType = getResponseMediaType(request, response);
        final Deserializer<T> deserializer = serdesManager.getDeserializer(type, mediaType);
        checkDeserializerNotNull(response, type, deserializer);
        final DeserializationContext context = new HttpDeserializationContext(request, response, type, providerManager);
        @SuppressWarnings("unchecked")
        Collection<T> result = deserializer.deserialize(containerType, response.getPayload().isString(), context);
        return getDeserializedResponse(response, result);
    }

    public <T> Response<T> deserializeResponse(Request request, SerializedResponse response, Class<T> type) {
        final String mediaType = getResponseMediaType(request, response);
        final Deserializer<T> deserializer = serdesManager.getDeserializer(type, mediaType);
        checkDeserializerNotNull(response, type, deserializer);
        final DeserializationContext context = new HttpDeserializationContext(request, response, type, providerManager);
        T result = deserializer.deserialize(response.getPayload().isString(), context);
        return getDeserializedResponse(response, result);
    }

    @SuppressWarnings("unchecked")
    public SerializedRequestDelegate serializeRequest(Request request) {
        Object payload = request.getPayload();
        String body = null;
        if (payload != null) {
            final String mediaType = getRequestMediaType(request);
            if (payload instanceof Collection) {
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
                    Serializer<?> serializer = serdesManager.getSerializer(item.getClass(), mediaType);
                    checkSerializerNotNull(request, item.getClass(), serializer);
                    body = serializer.serialize(c, new HttpSerializationContext(request));
                }
            } else {
                Serializer<Object> serializer = (Serializer<Object>) serdesManager.getSerializer(payload.getClass(),
                        mediaType);
                checkSerializerNotNull(request, payload.getClass(), serializer);
                body = serializer.serialize(payload, new HttpSerializationContext(request));
            }
        }
        return new SerializedRequestDelegate(request, new Payload(body));
    }

    private String getRequestMediaType(Request request) {
        String mediaType = request.getContentType();
        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "*/*";
            logger.log(Level.INFO, "Request with no 'Content-Type' header being dispatched to '" + request.getUrl()
                    + "'. The content-type value has been automatically set to '*/*' to match serializers.");
        } else {
            mediaType = extractMediaTypeFromContentType(mediaType);
        }
        return mediaType;
    }

    private String getResponseMediaType(Request request, SerializedResponse response) {
        String medaType = response.getContentType();
        if (medaType == null || medaType.isEmpty()) {
            medaType = "*/*";
            logger.log(Level.INFO, "Response with no 'Content-Type' header received from '" + request.getUrl()
                    + "'. The content-type value has been automatically set to '*/*' to match deserializers.");
        } else {
            medaType = extractMediaTypeFromContentType(medaType);
        }
        return medaType;
    }

    private String extractMediaTypeFromContentType(String contentType) {
        return contentType.split(";")[0];
    }

    private <T> Response<T> getDeserializedResponse(SerializedResponse response, T result) {
        return new ResponseImpl<T>(response.getHeaders(), response.getStatusCode(), response.getStatusText(),
                response.getResponseType(), result);
    }

    private void checkDeserializerNotNull(SerializedResponse response, Class<?> type, Deserializer<?> deserializer) {
        if (deserializer == null)
            throw new SerializationException("Could not find Deserializer for class '" + type.getName() + "' and " +
                    "media-type '" + response.getContentType() + "'.");
    }

    private void checkSerializerNotNull(Request request, Class<?> type, Serializer<?> serializer) {
        if (serializer == null)
            throw new SerializationException("Could not find Serializer for class '" + type.getName() + "' and " +
                    "media-type '" + request.getContentType() + "'.");
    }

    private boolean isJsonMediaType(String mediaType) {
        return mediaType.contains("json") || mediaType.contains("javascript");
    }
}
