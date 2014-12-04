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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class performs all necessary processing steps to incoming responses.
 *
 * @author Danilo Reinert
 */
public class ResponseProcessor {

    private static Logger logger = Logger.getLogger(ResponseProcessor.class.getName());

    private final SerializationEngine serializationEngine;
    private final FilterEngine filterEngine;
    private final InterceptorEngine interceptorEngine;

    public ResponseProcessor(SerializationEngine serializationEngine, FilterEngine filterEngine,
                             InterceptorEngine interceptorEngine) {
        this.serializationEngine = serializationEngine;
        this.filterEngine = filterEngine;
        this.interceptorEngine = interceptorEngine;
    }

    @SuppressWarnings("unchecked")
    public <T, R extends SerializedResponse & ResponseInterceptorContext & ResponseFilterContext>
            DeserializedResponse<T> process(Request request, R response, Class<T> deserializationType) {
        // 1: FILTER
        filterEngine.filterResponse(request, response);

        // 2: INTERCEPT
        interceptorEngine.interceptResponse(request, response);

        // 3: DESERIALIZE
        final ResponseType responseType = response.getResponseType();
        DeserializedResponse<T> r;
        if (Payload.class == deserializationType) {
            r = (DeserializedResponse<T>) new DeserializedResponse<Payload>(response.getHeaders(),
                    response.getStatusCode(), response.getStatusText(), responseType, response.getPayload());
        } else if (Response.class == deserializationType || SerializedResponse.class == deserializationType) {
            r = (DeserializedResponse<T>) new DeserializedResponse<Response>(response.getHeaders(),
                    response.getStatusCode(), response.getStatusText(), response.getResponseType(), response);
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            r = serializationEngine.deserializeResponse(request, response, deserializationType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            r = new DeserializedResponse<T>(response.getHeaders(), response.getStatusCode(), response.getStatusText(),
                    responseType, null);
        }

        return r;
    }

    public <T, R extends SerializedResponse & ResponseInterceptorContext & ResponseFilterContext>
            DeserializedResponse<Collection<T>> process(Request request, R response,
                                                        Class<T> deserializationType,
                                                        Class<? extends Collection> containerType) {
        // 1: FILTER
        filterEngine.filterResponse(request, response);

        // 2: INTERCEPT
        interceptorEngine.interceptResponse(request, response);

        // 3: DESERIALIZE
        final ResponseType responseType = response.getResponseType();
        DeserializedResponse<Collection<T>> r;
        if (Payload.class == deserializationType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Payload.class.getName()
                    + "'. A null payload will be returned.");
            r = new DeserializedResponse<Collection<T>>(response.getHeaders(), response.getStatusCode(),
                    response.getStatusText(), responseType, null);
        } else if (Response.class == deserializationType || SerializedResponse.class == deserializationType) {
            logger.log(Level.SEVERE, "It's not allowed to ask a collection of '" + Response.class.getName()
                    + "'. A null payload will be returned.");
            r = new DeserializedResponse<Collection<T>>(response.getHeaders(), response.getStatusCode(),
                    response.getStatusText(), responseType, null);
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            r = serializationEngine.deserializeResponse(request, response, deserializationType, containerType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            r = new DeserializedResponse<Collection<T>>(response.getHeaders(), response.getStatusCode(),
                    response.getStatusText(), responseType, null);
        }

        return r;
    }
}
