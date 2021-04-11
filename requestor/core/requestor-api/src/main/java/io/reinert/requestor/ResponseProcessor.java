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
    public <T> void process(Request request, RawResponse response, Class<T> deserializationType, Deferred<T> deferred) {

        // 1: FILTER
        filter(request, response);

        // 2: INTERCEPT
        intercept(request, response);

        Response<T> r = (Response<T>) response;

        if (isSuccessful(response)) {
            // 3: DESERIALIZE
            r = deserializeSingleResponse(request, response, deserializationType);
        }

        // 4: RESOLVE
        resolve(deferred, r);
    }

    public <T, C extends Collection> void process(Request request, RawResponse response, Class<T> deserializationType,
                                                  Class<C> collectionType, Deferred<C> deferred) {
        // 1: FILTER
        filter(request, response);

        // 2: INTERCEPT
        intercept(request, response);

        Response<C> r;

        if (isSuccessful(response)) {
            // 3: DESERIALIZE
            r = deserializeCollectionResponse(request, response, deserializationType, collectionType);
        } else {
            r = new ResponseImpl<C>(request, response.getStatus(), response.getHeaders(), response.getResponseType(),
                    null);
        }

        // 4: RESOLVE
        resolve(deferred, r);
    }

    SerializationEngine getSerializationEngine() {
        return serializationEngine;
    }

    @SuppressWarnings("unchecked")
    private <T, C extends Collection> Response<C> deserializeCollectionResponse(Request request, RawResponse response,
                                                                                Class<T> deserializationType,
                                                                                Class<C> collectionType) {
        // TODO: return a list of one element instead of null, logging the occurrence
        final ResponseType responseType = response.getResponseType();
        Response<C> r;
        if (Payload.class == deserializationType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Payload.class.getName()
                    + "'. A null payload will be returned.");
            r = new ResponseImpl<C>(request, response.getStatus(), response.getHeaders(), responseType, null);
        } else if (Response.class == deserializationType || RawResponse.class == deserializationType
                || SerializedResponse.class == deserializationType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Response.class.getName()
                    + "'. A null payload will be returned.");
            r = new ResponseImpl<C>(request, response.getStatus(), response.getHeaders(), responseType, null);
        } else if (Headers.class == deserializationType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Headers.class.getName()
                    + "'. A null payload will be returned.");
            r = new ResponseImpl<C>(request, response.getStatus(), response.getHeaders(), responseType, null);
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            r = serializationEngine.deserializeResponse(request, response, deserializationType, collectionType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            r = new ResponseImpl<C>(request, response.getStatus(), response.getHeaders(), responseType, null);
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    private <T> Response<T> deserializeSingleResponse(Request request, RawResponse response,
                                                      Class<T> deserializationType) {
        final ResponseType responseType = response.getResponseType();
        Response<T> r;
        if (Payload.class == deserializationType) {
            r = (Response<T>) new ResponseImpl<Payload>(request, response.getStatus(), response.getHeaders(),
                    responseType, response.getPayload());
        } else if (Response.class == deserializationType || RawResponse.class == deserializationType
                || SerializedResponse.class == deserializationType) {
            r = (Response<T>) response;
        } else if (Headers.class == deserializationType) {
            r = (Response<T>) new ResponseImpl<Headers>(request, response.getStatus(), response.getHeaders(),
                    responseType, response.getHeaders());
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            r = serializationEngine.deserializeResponse(request, response, deserializationType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            r = new ResponseImpl<T>(request, response.getStatus(), response.getHeaders(), responseType, null);
        }
        return r;
    }

    private void filter(Request request, RawResponse response) {
        filterEngine.filterResponse(request, response);
    }

    private void intercept(Request request, RawResponse response) {
        interceptorEngine.interceptResponse(request, response);
    }

    private boolean isSuccessful(RawResponse response) {
        return response.getStatusCode() / 100 == 2;
    }

    private <D> void resolve(Deferred<D> deferred, Response<D> r) {
        deferred.resolve(r);
    }
}
