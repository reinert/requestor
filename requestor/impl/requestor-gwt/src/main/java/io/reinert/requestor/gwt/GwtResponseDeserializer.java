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
package io.reinert.requestor.gwt;

import io.reinert.requestor.DeserializableResponseInProcess;
import io.reinert.requestor.Headers;
import io.reinert.requestor.RawResponse;
import io.reinert.requestor.Response;
import io.reinert.requestor.ResponseDeserializer;
import io.reinert.requestor.SerializationEngine;
import io.reinert.requestor.SerializedResponse;
import io.reinert.requestor.gwt.payload.SerializedJsPayload;
import io.reinert.requestor.gwt.type.ArrayBuffer;
import io.reinert.requestor.gwt.type.Blob;
import io.reinert.requestor.gwt.type.Document;
import io.reinert.requestor.gwt.type.Json;
import io.reinert.requestor.payload.SerializedPayload;
import io.reinert.requestor.payload.type.PayloadType;

public class GwtResponseDeserializer implements ResponseDeserializer {
    @Override
    public void deserialize(DeserializableResponseInProcess response, SerializationEngine serializationEngine) {
        if (isSuccessful(response)) {
            final PayloadType payloadType = response.getPayloadType();
            final Class<?> type = payloadType.getType();

            Object result = null;

            if (SerializedPayload.class == type) {
                result = response.getSerializedPayload();
            } else if (Blob.class == type) {
                result = new Blob(((SerializedJsPayload) response.getSerializedPayload()).getObject());
            } else if (ArrayBuffer.class == type) {
                result = new ArrayBuffer(((SerializedJsPayload) response.getSerializedPayload()).getObject());
            } else if (Document.class == type) {
                result = new Document(((SerializedJsPayload) response.getSerializedPayload()).getObject());
            } else if (Json.class == type) {
                result = new Json(((SerializedJsPayload) response.getSerializedPayload()).getObject());
            } else if (Response.class == type || SerializedResponse.class == type || RawResponse.class == type) {
                result = response.getRawResponse();
            } else if (Headers.class == type) {
                result = response.getHeaders();
            }

            if (result != null) {
                response.deserializePayload(result);
            } else {
                serializationEngine.deserializeResponse(response);
            }
        } else {
            // TODO: deserialize by statusCode
            response.deserializePayload(response.getSerializedPayload());
        }

        response.proceed();
    }

    private boolean isSuccessful(SerializedResponse response) {
        return response.getStatusCode() / 100 == 2;
    }
}
