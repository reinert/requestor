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
import io.reinert.requestor.types.SpecialType;

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
        filter(request);

        // 2: SERIALIZE
        SerializedRequestDelegate serializedRequest = serialize(request);

        // 3: INTERCEPT
        intercept(serializedRequest);

        return serializedRequest;
    }

    private void filter(RequestFilterContext request) {
        filterEngine.filterRequest(request);
    }

    private void intercept(SerializedRequestDelegate serializedRequest) {
        interceptorEngine.interceptRequest(serializedRequest);
    }

    private SerializedRequestDelegate serialize(RequestBuilder request) {
        SerializedRequestDelegate serializedRequest;
        Object payload = request.getPayload();
        if (payload instanceof FormData) {
            // TODO: extract to a FormDataSerializationEngine and support multiple serializers by content-type (or no?)
            // maybe extract contentType matching from SerializationEngine

            // FormData serialization
            final Payload serializedPayload = formDataSerializer.serialize((FormData) payload);
            // If mediaType is null then content-type header is removed and the browser handles it
            request.header("Content-Type", formDataSerializer.mediaType());
            serializedRequest = new SerializedRequestDelegate(request, serializedPayload);
        } else if (payload instanceof SpecialType) {
            serializedRequest = new SerializedRequestDelegate(request, new Payload(((SpecialType) payload).as()));
        } else if (payload instanceof Payload) {
            serializedRequest = new SerializedRequestDelegate(request, (Payload) payload);
        } else {
            serializedRequest = serializationEngine.serializeRequest(request);
        }
        return serializedRequest;
    }
}
