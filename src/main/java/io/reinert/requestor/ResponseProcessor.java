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

/**
 * This class performs all necessary processing steps to incoming responses.
 *
 * @author Danilo Reinert
 */
public class ResponseProcessor {

    private final SerializationEngine serializationEngine;
    private final FilterEngine filterEngine;

    public ResponseProcessor(SerializationEngine serializationEngine, FilterEngine filterEngine) {
        this.serializationEngine = serializationEngine;
        this.filterEngine = filterEngine;
    }

    @SuppressWarnings("unchecked")
    public <T> DeserializedResponse<T> process(Request request, SerializedResponse response,
                                               Class<T> deserializationType) {
        DeserializedResponse<T> r;
        if (deserializationType == Payload.class) {
            r = (DeserializedResponse<T>) new DeserializedResponse<Payload>(response.getHeaders(),
                    response.getStatusCode(), response.getStatusText(), response.getPayload());
        } else {
            r = serializationEngine.deserializeResponse(request, response, deserializationType);
        }
        filterEngine.filterResponse(request, r);
        return  r;
    }

    public <T> DeserializedResponse<Collection<T>> process(Request request, SerializedResponse response,
                                                           Class<T> deserializationType,
                                                           Class<? extends Collection> containerType) {
        DeserializedResponse<Collection<T>> r = serializationEngine.deserializeResponse(request, response,
                deserializationType, containerType);
        filterEngine.filterResponse(request, r);
        return  r;
    }
}
