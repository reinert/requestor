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
 * It envelops a String.
 *
 * @author Danilo Reinert
 */
public interface SerializedPayload {

    SerializedPayload EMPTY_PAYLOAD = new TextSerializedPayload("");

    /**
     * <p>Returns this serialized payload as a byte array.</p>
     *
     * <p>If the byte array is not available, it automatically converts the string content to bytes.</p>
     *
     * @return The serialized payload as byte[]
     */
    byte[] asBytes();

    /**
     * <p>Returns this serialized payload as a string.</p>
     *
     * <p>If the string is not available, it automatically converts the byte content to string.</p>
     *
     * @return The serialized payload as String
     */
    String asString();

    /**
     * Tells whether this serialized payload is empty or not.
     *
     * @return <code>true</code> if this serialized payload is empty
     */
    boolean isEmpty();

    /**
     * Tells whether there's a string content available in this serialized payload or not.
     *
     * @return <code>true</code> if the string representation is already available
     */
    boolean isStringAvailable();

    /**
     * Tells whether there's a byte content available in this serialized payload or not.
     *
     * @return <code>true</code> if the byte representation is already available
     */
    boolean isBytesAvailable();
}
