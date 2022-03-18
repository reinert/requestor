/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.net.payload;

import java.util.Base64;

import io.reinert.requestor.core.payload.SerializedPayload;

/**
 * Represents an HTTP payload.
 * It envelops a byte array.
 *
 * @author Danilo Reinert
 */
public class BinarySerializedPayload implements SerializedPayload {

    protected String string;
    protected byte[] bytes;

    public BinarySerializedPayload(byte[] bytes) {
        this.bytes = bytes == null ? new byte[]{} : bytes;
    }

    @Override
    public boolean isEmpty() {
        return bytes.length == 0;
    }

    @Override
    public boolean isStringAvailable() {
        return string != null;
    }

    @Override
    public boolean isBytesAvailable() {
        return bytes != null;
    }

    @Override
    public long getLength() {
        return bytes.length;
    }

    @Override
    public byte[] asBytes() {
        return bytes;
    }

    @Override
    public String asString() {
        return string == null ? string = Base64.getEncoder().encodeToString(bytes) : string;
    }
}
