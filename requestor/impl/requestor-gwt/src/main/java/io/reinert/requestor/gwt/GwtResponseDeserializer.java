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

import io.reinert.requestor.core.BaseResponseDeserializer;
import io.reinert.requestor.core.DeserializableResponseInProcess;
import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.SerializationEngine;
import io.reinert.requestor.core.SerializedResponse;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.gwt.payload.SerializedJsPayload;
import io.reinert.requestor.gwt.type.ArrayBuffer;
import io.reinert.requestor.gwt.type.Blob;
import io.reinert.requestor.gwt.type.Document;
import io.reinert.requestor.gwt.type.Json;

public class GwtResponseDeserializer extends BaseResponseDeserializer {

    private static GwtResponseDeserializer responseDeserializer;

    public static GwtResponseDeserializer getInstance() {
        if (responseDeserializer == null) responseDeserializer = new GwtResponseDeserializer();
        return responseDeserializer;
    }

    @Override
    public void deserialize(DeserializableResponseInProcess response, SerializationEngine serializationEngine) {
        if (isSuccessful(response)) {
            final Class<?> type = response.getPayloadType().getType();
            final SerializedPayload serializedPayload = response.getSerializedPayload();

            Object result = null;
            boolean handled = false;

            if (SerializedPayload.class == type) {
                handled = true;
                result = response.getSerializedPayload();
            } else if (Blob.class == type) {
                handled = true;
                if (!serializedPayload.isEmpty())
                    result = new Blob(((SerializedJsPayload) serializedPayload).asJso());
            } else if (ArrayBuffer.class == type) {
                handled = true;
                if (!serializedPayload.isEmpty())
                    result = new ArrayBuffer(((SerializedJsPayload) serializedPayload).asJso());
            } else if (Document.class == type) {
                handled = true;
                if (!serializedPayload.isEmpty())
                    result = new Document(((SerializedJsPayload) serializedPayload).asJso());
            } else if (Json.class == type) {
                handled = true;
                if (!serializedPayload.isEmpty())
                    result = new Json(((SerializedJsPayload) serializedPayload).asJso());
            } else if (Response.class == type || SerializedResponse.class == type || RawResponse.class == type) {
                handled = true;
                result = response.getRawResponse();
            } else if (Headers.class == type) {
                handled = true;
                result = response.getHeaders();
            }

            if (handled) {
                response.deserializePayload(result == null ? Payload.EMPTY_PAYLOAD : new Payload(result));
                response.proceed();
                return;
            }
        }

        super.deserialize(response, serializationEngine);
    }
}
