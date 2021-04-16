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
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.requestor.payload.CollectionPayloadType;
import io.reinert.requestor.payload.PayloadType;

/**
 * This class performs all necessary processing steps to incoming responses.
 *
 * @author Danilo Reinert
 */
public class ResponseProcessor {

    private static Logger logger = Logger.getLogger(ResponseProcessor.class.getName());

    private final FilterEngine filterEngine;
    private final InterceptorEngine interceptorEngine;
    private final SerializationEngine serializationEngine;

    public ResponseProcessor(SerializationEngine serializationEngine, FilterEngine filterEngine,
                             InterceptorEngine interceptorEngine) {
        this.serializationEngine = serializationEngine;
        this.filterEngine = filterEngine;
        this.interceptorEngine = interceptorEngine;
    }

    public <T> void process(Request request, RawResponse response, Deferred<T> deferred) {
        // TODO: create a bypassResponse[Filter|Intercept] option
        // To bypass deserialization, just ask for Payload.class

        // 1: FILTER
        filter(request, response);

        // 2: INTERCEPT
        intercept(request, response);

        if (isSuccessful(response)) {
            // 3: DESERIALIZE
            deserializeResponse(request, response);
        } else {
            // TODO: deserialize by statusCode
            response.setDeserializedPayload(null);
        }

        // 4: RESOLVE
        resolve(deferred, response);
    }

    SerializationEngine getSerializationEngine() {
        return serializationEngine;
    }

    private void deserializeResponse(Request request, RawResponse response) {
        PayloadType payloadType = response.getPayloadType();

        if (payloadType instanceof CollectionPayloadType) {
            CollectionPayloadType collectionPayloadType = (CollectionPayloadType) payloadType;
            deserializeCollectionResponse(request, response,
                    collectionPayloadType.getParametrizedType().getType(),
                    collectionPayloadType.getCollectionType());
            return;
        }

        deserializeSingleResponse(request, response, payloadType.getType());
    }

    @SuppressWarnings("unchecked")
    private <T, C extends Collection> void deserializeCollectionResponse(Request request,
                                                                         RawResponse response,
                                                                         Class<T> entityType,
                                                                         Class<C> collectionType) {
        // TODO: return a list of one element instead of null, logging the occurrence
        final ResponseType responseType = response.getResponseType();
        if (Payload.class == entityType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Payload.class.getName()
                    + "'. A null payload will be returned.");
            response.setDeserializedPayload(null);
        } else if (Response.class == entityType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Response.class.getName()
                    + "'. A null payload will be returned.");
            response.setDeserializedPayload(null);
        } else if (Headers.class == entityType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Headers.class.getName()
                    + "'. A null payload will be returned.");
            response.setDeserializedPayload(null);
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            serializationEngine.deserializeResponse(request, response, entityType, collectionType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + entityType.getName() + "'. A null payload will be returned.");
            response.setDeserializedPayload(null);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void deserializeSingleResponse(Request request, RawResponse response,
                                               Class<T> deserializationType) {
        final ResponseType responseType = response.getResponseType();
        if (Payload.class == deserializationType) {
            response.setDeserializedPayload((T) response.getSerializedPayload());
        } else if (Response.class == deserializationType) {
            response.setDeserializedPayload(response);
        } else if (Headers.class == deserializationType) {
            response.setDeserializedPayload((T) response.getHeaders());
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            serializationEngine.deserializeResponse(request, response, deserializationType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            response.setDeserializedPayload(null);
        }
    }

    private void filter(Request request, RawResponse response) {
        filterEngine.filterResponse(request, response);
    }

    private void intercept(Request request, RawResponse response) {
        interceptorEngine.interceptResponse(request, response);
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }

    private <D> void resolve(Deferred<D> deferred, Response r) {
        deferred.resolve(r);
    }
}
