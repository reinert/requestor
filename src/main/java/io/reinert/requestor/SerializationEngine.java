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
import io.reinert.requestor.serialization.Serializer;

/**
 * Responsible for performing managed de/serialization.
 *
 * @author Danilo Reinert
 */
public class SerializationEngine {

    private static Logger logger = Logger.getLogger(SerializationEngine.class.getName());

    private final SerdesManager serdesManager;
    private final ProviderManager providerManager;

    public SerializationEngine(SerdesManager serdesManager, ProviderManager providerManager) {
        this.serdesManager = serdesManager;
        this.providerManager = providerManager;
    }

    public <T, C extends Collection> DeserializedResponse<Collection<T>> deserializeResponse(Request request,
                                                                                             SerializedResponse resp,
                                                                                             Class<T> type,
                                                                                             Class<C> containerType) {
        String responseContentType = getResponseContentType(request, resp);
        final Deserializer<T> deserializer = serdesManager.getDeserializer(type, responseContentType);
        final DeserializationContext context = new HttpDeserializationContext(request, resp, type, providerManager);
        @SuppressWarnings("unchecked")
        Collection<T> result = deserializer.deserialize(containerType, resp.getPayload(), context);
        return getDeserializedResponse(resp, result);
    }

    public <T> DeserializedResponse<T> deserializeResponse(Request request, SerializedResponse response,
                                                           Class<T> type) {
        String responseContentType = getResponseContentType(request, response);
        final Deserializer<T> deserializer = serdesManager.getDeserializer(type, responseContentType);
        final DeserializationContext context = new HttpDeserializationContext(request, response, type, providerManager);
        T result = deserializer.deserialize(response.getPayload(), context);
        return getDeserializedResponse(response, result);
    }

    @SuppressWarnings("unchecked")
    public SerializedRequest serializeRequest(Request request) {
        Object payload = request.getPayload();
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
                    Serializer<?> serializer = serdesManager.getSerializer(item.getClass(), request.getContentType());
                    body = serializer.serialize(c, new HttpSerializationContext(request));
                }
            } else {
                Serializer<Object> serializer = (Serializer<Object>) serdesManager.getSerializer(payload.getClass(),
                        request.getContentType());
                body = serializer.serialize(payload, new HttpSerializationContext(request));
            }
        }
        return new SerializedRequest(request, body);
    }

    private String getResponseContentType(Request request, SerializedResponse response) {
        String responseContentType = response.getHeader("Content-Type");
        if (responseContentType == null) {
            responseContentType = "*/*";
            logger.log(Level.INFO, "Response with no 'Content-Type' header received from '" + request.getUrl()
                    + "'. The content-type value has been automatically set to '*/*' to match deserializers.");
        }
        return responseContentType;
    }

    private <T> DeserializedResponse<T> getDeserializedResponse(SerializedResponse response, T result) {
        return new DeserializedResponse<T>(response.getHeaders(), response.getStatusCode(), response.getStatusText(),
                result);
    }
}
