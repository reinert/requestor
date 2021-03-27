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

/**
 * Request serializers are intended to serialize the request payload before it is sent to the server.
 *
 * @author Danilo Reinert
 */
public interface RequestSerializer {

    /**
     * Serialize method called after filters and before interceptors.
     * It should serialize the request using the SerializationEngine provided.
     *
     * @param request   The request to be dispatched.
     */
    void serialize(SerializableRequestInProcess request, SerializationEngine serializationEngine);
}
