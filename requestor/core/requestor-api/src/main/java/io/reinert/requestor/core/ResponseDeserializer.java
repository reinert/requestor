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

/**
 * Response deserializers are intended to deserialize the response payload after it's received from the server.
 *
 * @author Danilo Reinert
 */
public interface ResponseDeserializer {

    /**
     * Deserialize method called after interceptors and before filters.
     * It should deserialize the response using the SerializationEngine provided.
     *
     * @param response  The received response.
     */
    void deserialize(DeserializableResponseInProcess response, SerializationEngine serializationEngine);
}
