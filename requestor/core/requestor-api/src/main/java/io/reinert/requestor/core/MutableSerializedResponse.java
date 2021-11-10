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

/**
 * A mutable serialized response.
 *
 * @author Danilo Reinert
 */
public interface MutableSerializedResponse extends SerializedResponse, HasHeaders {

    /**
     * Set the content type header of this response.
     *
     * @param mediaType The content type of this response
     */
    void setContentType(String mediaType);

    /**
     * Input a serialized payload to be deserialized.
     *
     * @param serializedPayload The payload of the response
     */
    void setSerializedPayload(SerializedPayload serializedPayload);

}
