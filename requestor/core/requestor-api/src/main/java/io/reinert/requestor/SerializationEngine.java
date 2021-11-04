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

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.form.FormData;
import io.reinert.requestor.form.FormDataOverlay;
import io.reinert.requestor.form.FormDataSerializerUrlEncoded;
import io.reinert.requestor.payload.SerializedPayload;
import io.reinert.requestor.payload.type.CollectionPayloadType;
import io.reinert.requestor.payload.type.CompositePayloadType;
import io.reinert.requestor.payload.type.DictionaryPayloadType;
import io.reinert.requestor.payload.type.PayloadType;
import io.reinert.requestor.payload.type.SinglePayloadType;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.SerializationException;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.type.ArrayBuffer;
import io.reinert.requestor.type.Blob;
import io.reinert.requestor.type.Document;
import io.reinert.requestor.type.Json;

/**
 * Responsible for performing managed de/serialization.
 *
 * @author Danilo Reinert
 */
class SerializationEngine {

    private static Logger logger = Logger.getLogger(SerializationEngine.class.getName());

    private final SerializerManagerImpl serializerManager;
    private final ProviderManagerImpl providerManager;

    public SerializationEngine(SerializerManagerImpl serializerManager, ProviderManagerImpl providerManager) {
        this.serializerManager = serializerManager;
        this.providerManager = providerManager;
    }

    public void deserializeResponse(DeserializableResponse response) {
        final Request request = response.getRequest();
        final PayloadType payloadType = response.getPayloadType();
        final Class<?> type = payloadType.getType();

        Object result = null;

        // Special types that skip regular deserialization
        if (SerializedPayload.class == type) {
            result = response.getSerializedPayload();
        } else if (Blob.class == type) {
            result = new Blob(response.getSerializedPayload().getObject());
        } else if (ArrayBuffer.class == type) {
            result = new ArrayBuffer(response.getSerializedPayload().getObject());
        } else if (Document.class == type) {
            result = new Document(response.getSerializedPayload().getObject());
        } else if (Json.class == type) {
            result = new Json(response.getSerializedPayload().getObject());
        } else if (Response.class == type || SerializedResponse.class == type || RawResponse.class == type) {
            result = response.getRawResponse();
        } else if (Headers.class == type) {
            result = response.getHeaders();
        }

        if (result != null) {
            response.deserializePayload(result);
            return;
        }

        // Regular deserialization process
        final ResponseType responseType = response.getResponseType();
        if (responseType != ResponseType.DEFAULT && responseType != ResponseType.TEXT) {
            throw new SerializationException("Deserialization of '" + responseType +
                    "' response type is not supported yet.");
        }

        if (payloadType instanceof DictionaryPayloadType || payloadType instanceof CompositePayloadType) {
            throw new SerializationException("Deserialization of " + payloadType.getClass().getName() +
                    " is not supported yet.");
        }

        if (payloadType instanceof CollectionPayloadType) {
            CollectionPayloadType<?> collectionPayloadType = (CollectionPayloadType<?>) payloadType;
            Class<? extends Collection> collectionType = collectionPayloadType.getType();
            Class<?> parametrizedType = collectionPayloadType.getParametrizedPayloadType().getType();
            result = deserializePayload(request, response, parametrizedType, collectionType);
        } else if (payloadType instanceof SinglePayloadType) {
            result = deserializePayload(request, response, type);
        }

        response.deserializePayload(result);
    }

    public <T, C extends Collection<T>> C deserializePayload(Request request, SerializedResponse response,
                                                             Class<T> entityType, Class<C> collectionType) {
        final String mediaType = getResponseMediaType(request, response);
        final Deserializer<T> deserializer = serializerManager.getDeserializer(entityType, mediaType);
        checkDeserializerNotNull(response, entityType, deserializer);
        final DeserializationContext context = new HttpDeserializationContext(request, response, providerManager,
                entityType);
        return deserializer.deserialize(collectionType, response.getSerializedPayload().getString(), context);
    }

    public <T> T deserializePayload(Request request, SerializedResponse response, Class<T> type) {
        final String mediaType = getResponseMediaType(request, response);
        final Deserializer<T> deserializer = serializerManager.getDeserializer(type, mediaType);
        checkDeserializerNotNull(response, type, deserializer);
        final DeserializationContext context = new HttpDeserializationContext(request, response, providerManager,
                type);
        return deserializer.deserialize(response.getSerializedPayload().getString(), context);
    }

    public void serializeRequest(SerializableRequest request) {
        Object payload = request.getPayload().getObject();

        if (payload instanceof FormData &&
                !FormDataSerializerUrlEncoded.MEDIA_TYPE.equalsIgnoreCase(request.getContentType())) {
            request.serializePayload(getFormDataSerializedPayload((FormData) payload));

            return;
        }

        if (payload instanceof SerializedPayload) {
            request.serializePayload((SerializedPayload) payload);

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
                    body = serializer.serialize(c, new HttpSerializationContext(request, collectionType, type,
                            request.getPayload().getFields()));
                }
            } else {
                final Class<?> type = payload.getClass();
                @SuppressWarnings("unchecked")
                Serializer<Object> serializer = (Serializer<Object>) serializerManager.getSerializer(type, mediaType);
                checkSerializerNotNull(request, payload.getClass(), serializer);
                body = serializer.serialize(payload, new HttpSerializationContext(request, type,
                        request.getPayload().getFields()));
            }
        }

        request.serializePayload(SerializedPayload.fromText(body));
    }

    private SerializedPayload getFormDataSerializedPayload(FormData formData) {
        if (formData.getFormElement() != null)
            return SerializedPayload.fromFormData(FormDataOverlay.create(formData.getFormElement()));

        FormDataOverlay overlay = FormDataOverlay.create();
        for (FormData.Param param : formData) {
            final Object value = param.getValue();
            if (value instanceof String) {
                overlay.append(param.getName(), (String) value);
            } else {
                overlay.append(param.getName(), (JavaScriptObject) value, param.getFileName());
            }
        }
        return SerializedPayload.fromFormData(overlay);
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

    private String getResponseMediaType(Request request, SerializedResponse response) {
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

    private void checkDeserializerNotNull(SerializedResponse response, Class<?> type, Deserializer<?> deserializer) {
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
