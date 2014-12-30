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

import io.reinert.requestor.form.FormData;
import io.reinert.requestor.form.FormDataSerializer;
import io.reinert.requestor.header.ContentTypeHeader;

/**
 * This class performs all necessary processing steps to ongoing requests.
 *
 * @author Danilo Reinert
 */
class RequestProcessor {

    private final FormDataSerializer formDataSerializer;
    private final SerializationEngine serializationEngine;
    private final FilterEngine filterEngine;
    private final InterceptorEngine interceptorEngine;

    public RequestProcessor(SerializationEngine serializationEngine, FilterEngine filterEngine,
                            InterceptorEngine interceptorEngine, FormDataSerializer formDataSerializer) {
        this.serializationEngine = serializationEngine;
        this.filterEngine = filterEngine;
        this.interceptorEngine = interceptorEngine;
        this.formDataSerializer = formDataSerializer;
    }

    public <R extends RequestBuilder & RequestFilterContext> SerializedRequest process(R request) {
        // 1: FILTER
        filterEngine.filterRequest(request);

        // 2: SERIALIZE
        SerializedRequestDelegate serializedRequest;
        Object payload = request.getPayload();
        if (payload instanceof Payload) {
            // Skip serialization (File, Blob, ArrayBuffer should be wrapped in a Payload to skip serialization)
            serializedRequest = new SerializedRequestDelegate(request, (Payload) payload);
        } else if (payload instanceof FormData) {
            // FormData serialization
            final Payload serializedPayload = formDataSerializer.serialize((FormData) payload);
            request.putHeader(new ContentTypeHeader(formDataSerializer.mediaType()));
            serializedRequest = new SerializedRequestDelegate(request, serializedPayload);
        } else {
            serializedRequest = serializationEngine.serializeRequest(request);
        }

        // 3: INTERCEPT
        interceptorEngine.interceptRequest(serializedRequest);
        return serializedRequest;
    }
}
