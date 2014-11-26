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

import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * This class performs all necessary processing steps to incoming responses.
 *
 * @author Danilo Reinert
 */
public class ResponseProcessor {

    private static Logger logger = Logger.getLogger(ResponseProcessor.class.getName());

    private final SerializationEngine serializationEngine;
    private final FilterEngine filterEngine;

    public ResponseProcessor(SerializationEngine serializationEngine, FilterEngine filterEngine) {
        this.serializationEngine = serializationEngine;
        this.filterEngine = filterEngine;
    }

    @SuppressWarnings("unchecked")
    public <T> DeserializedResponse<T> process(Request request, SerializedResponse response,
                                               Class<T> deserializationType) {
        final ResponseType responseType = request.getResponseType();
        DeserializedResponse<T> r;
        if (deserializationType == Payload.class) {
            r = (DeserializedResponse<T>) new DeserializedResponse<Payload>(response.getHeaders(),
                    response.getStatusCode(), response.getStatusText(), response.getPayload());
        } else if (deserializationType == Response.class) {
            r = (DeserializedResponse<T>) new DeserializedResponse<Response>(response.getHeaders(),
                    response.getStatusCode(), response.getStatusText(), response);
        } else if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            r = serializationEngine.deserializeResponse(request, response, deserializationType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            r = new DeserializedResponse<T>(response.getHeaders(), response.getStatusCode(), response.getStatusText(),
                    null);
        }

        filterEngine.filterResponse(request, r);
        return r;
    }

    public <T> DeserializedResponse<Collection<T>> process(Request request, SerializedResponse response,
                                                           Class<T> deserializationType,
                                                           Class<? extends Collection> containerType) {
        if (deserializationType == Payload.class) {
            throw new UnableToDeserializeException("It's not allowed to ask a collection of "
                    + Payload.class.getName());
        }
        if (deserializationType == Response.class) {
            throw new UnableToDeserializeException("It's not allowed to ask a collection of "
                    + Payload.class.getName());
        }

        final ResponseType responseType = request.getResponseType();
        DeserializedResponse<Collection<T>> r;
        if (responseType == ResponseType.DEFAULT || responseType == ResponseType.TEXT) {
            r = serializationEngine.deserializeResponse(request, response, deserializationType, containerType);
        } else {
            logger.log(Level.SEVERE, "Could not process response of type '" + responseType + "' to class '"
                    + deserializationType.getName() + "'. A null payload will be returned.");
            r = new DeserializedResponse<Collection<T>>(response.getHeaders(), response.getStatusCode(),
                    response.getStatusText(), null);
        }

        filterEngine.filterResponse(request, r);
        return r;
    }
}
