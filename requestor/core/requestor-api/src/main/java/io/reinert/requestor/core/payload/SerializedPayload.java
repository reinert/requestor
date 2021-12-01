/*
 * Copyright 2014-2021 Danilo Reinert
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
package io.reinert.requestor.core.payload;

/**
 * Represents an HTTP payload.
 * It envelopes a String.
 *
 * @author Danilo Reinert
 */
public class SerializedPayload {

    public static final SerializedPayload EMPTY_PAYLOAD = new SerializedPayload(null);

    private final String string;

    public SerializedPayload(String string) {
        this.string = string;
    }

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return string == null || string.length() == 0;
    }

    /**
     * Returns the string value if this payload is of String type.
     *
     * @return The payload as String
     */
    public String asText() {
        return string == null ? "" : string;
    }
}
