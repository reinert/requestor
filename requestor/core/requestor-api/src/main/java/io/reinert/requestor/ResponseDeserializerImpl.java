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

public class ResponseDeserializerImpl implements ResponseDeserializer {
    @Override
    public void deserialize(DeserializableResponseInProcess response, SerializationEngine serializationEngine) {
        if (isSuccessful(response)) {
            serializationEngine.deserializeResponse(response);
        } else {
            // TODO: deserialize by statusCode
            response.deserializePayload(response.getSerializedPayload());
        }

        response.proceed();
    }

    protected boolean isSuccessful(SerializedResponse response) {
        return response.getStatusCode() / 100 == 2;
    }
}
