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

    @SuppressWarnings("unchecked")
    public <T> void process(Request request, Response response, Class<T> deserializationType, Deferred<T> deferred) {

        // 1: FILTER
        filter(request, response);

        // 2: INTERCEPT
        intercept(request, response);

        if (isSuccessful(response)) {
            // 3: DESERIALIZE
            deserializeSingleResponse(request, response, deserializationType);
        } else {
            // TODO: deserialize by statusCode
            response.deserializePayload(null);
        }

        // 4: RESOLVE
        resolve(deferred, response);
    }

    public <T, C extends Collection> void process(Request request, Response response, Class<T> entityType,
                                                  Class<C> collectionType, Deferred<C> deferred) {
        // 1: FILTER
        filter(request, response);

        // 2: INTERCEPT
        intercept(request, response);

        if (isSuccessful(response)) {
            // 3: DESERIALIZE
            deserializeCollectionResponse(request, response, entityType, collectionType);
        } else {
            // TODO: deserialize by statusCode
            response.deserializePayload(null);
        }

        // 4: RESOLVE
        resolve(deferred, response);
    }

    SerializationEngine getSerializationEngine() {
        return serializationEngine;
    }

    @SuppressWarnings("unchecked")
    private <T, C extends Collection> Response deserializeCollectionResponse(Request request, Response response,
                                                                                Class<T> entityType,
                                                                                Class<C> collectionType) {
        // TODO: return a list of one element instead of null, logging the occurrence
        final ResponseType responseType = response.getResponseType();
        if (Payload.class == entityType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Payload.class.getName()
                    + "'. A null payload will be returned.");
            response.deserializePayload(null);
        } else if (Response.class == entityType || SerializedResponse.class == entityType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Response.class.getName()
                    + "'. A null payload will be returned.");
            response.deserializePayload(null);
        } else if (Headers.class == entityType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Headers.class.getName()
                    + "'. A null payload will be returned.");
            response.deserializePayload(null);
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            response = serializationEngine.deserializeResponse(request, response, entityType, collectionType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + entityType.getName() + "'. A null payload will be returned.");
            response.deserializePayload(null);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private <T> Response deserializeSingleResponse(Request request, Response response,
                                                      Class<T> deserializationType) {
        final ResponseType responseType = response.getResponseType();
        if (Payload.class == deserializationType) {
            response.deserializePayload((T) response.getSerializedPayload());
        } else if (Response.class == deserializationType || SerializedResponse.class == deserializationType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a '" + Response.class.getName()
                    + "'. A null payload will be returned.");
            response.deserializePayload(null);
        } else if (Headers.class == deserializationType) {
            response.deserializePayload((T) response.getHeaders());
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            response = serializationEngine.deserializeResponse(request, response, deserializationType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            response.deserializePayload(null);
        }
        return response;
    }

    private void filter(Request request, Response response) {
        // FIXME
        throw new UnsupportedOperationException();
//        filterEngine.filterResponse(request, response);
    }

    private void intercept(Request request, Response response) {
        // FIXME
        throw new UnsupportedOperationException();
//        interceptorEngine.interceptResponse(request, response);
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }

    private <D> void resolve(Deferred<D> deferred, Response r) {
        deferred.resolve(r);
    }
}
