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
package io.reinert.requestor.core;

import java.util.ListIterator;

/**
 * This class performs all necessary processing steps to ongoing responses.
 *
 * @author Danilo Reinert
 */
public class ResponseProcessor {

    private final SerializationEngine serializationEngine;
    private ResponseDeserializer responseDeserializer;
    private final FilterManagerImpl filterManager;
    private final InterceptorManagerImpl interceptorManager;

    public ResponseProcessor(SerializationEngine serializationEngine, ResponseDeserializer responseDeserializer,
                             FilterManagerImpl filterManager, InterceptorManagerImpl interceptorManager) {
        this.serializationEngine = serializationEngine;
        this.responseDeserializer = responseDeserializer;
        this.filterManager = filterManager;
        this.interceptorManager = interceptorManager;
    }

    public void process(ProcessableResponse response) {
        // TODO: create a bypassResponse[Filter|Intercept] option
        // To bypass deserialization, just ask for Payload.class

        // 3: FILTER
        response = applyFilters(response);

        // 2: DESERIALIZE
        response = applyDeserializer(response);

        // 1: INTERCEPT
        response = applyInterceptors(response);

        response.process();
    }

    public ResponseDeserializer getResponseDeserializer() {
        return responseDeserializer;
    }

    public void setResponseDeserializer(ResponseDeserializer responseDeserializer) {
        this.responseDeserializer = responseDeserializer;
    }

    private ProcessableResponse applyFilters(ProcessableResponse response) {
        // Apply filters in reverse order, so they are executed in the order they were registered
        final ListIterator<ResponseFilter.Provider> it = filterManager.reverseResponseFiltersIterator();
        while (it.hasPrevious()) {
            response = new ResponseInFilterProcess(response, it.previous().getInstance());
        }

        return response;
    }

    private ProcessableResponse applyInterceptors(ProcessableResponse response) {
        // Apply interceptors in reverse order, so they are executed in the order they were registered
        final ListIterator<ResponseInterceptor.Provider> it = interceptorManager.reverseResponseInterceptorsIterator();
        while (it.hasPrevious()) {
            response = new ResponseInInterceptProcess(response, it.previous().getInstance());
        }

        return response;
    }

    private ProcessableResponse applyDeserializer(ProcessableResponse response) {
        return new ResponseInDeserializeProcess(response, serializationEngine, responseDeserializer);
    }
}
