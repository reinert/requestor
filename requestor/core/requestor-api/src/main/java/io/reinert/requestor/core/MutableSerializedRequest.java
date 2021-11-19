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

import io.reinert.requestor.core.payload.SerializedPayload;

public interface MutableSerializedRequest extends MutableRequest, SerializedRequest {

    /**
     * Sets the payload serialized to be sent in the HTTP request body.
     *
     * @param serializedPayload The payload of the request
     */
    void setSerializedPayload(SerializedPayload serializedPayload);

    /**
     * Copy the request.
     *
     * @return a copy of this request
     */
    MutableSerializedRequest copy();

    /**
     * Copy the request keeping references to the store and retry and polling options.
     *
     * @return a replica of this request
     */
    MutableSerializedRequest replicate();

}
