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

import java.io.InputStream;

import io.reinert.requestor.core.payload.SerializedPayload;

/**
 * Represents an HTTP payload.
 * It envelops an InputStream.
 *
 * @author Danilo Reinert
 */
public class InputStreamSerializedPayload implements SerializedPayload {

    protected final InputStream stream;
    protected final long length;

    public InputStreamSerializedPayload(InputStream stream) {
        this(stream, 0L);
    }

    public InputStreamSerializedPayload(InputStream stream, long length) {
        this.stream = stream;
        this.length = length;
    }

    @Override
    public boolean isEmpty() {
        return stream == null;
    }

    @Override
    public boolean isStringAvailable() {
        return false;
    }

    @Override
    public boolean isBytesAvailable() {
        return false;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public byte[] asBytes() {
        return null;
    }

    @Override
    public String asString() {
        return null;
    }

    public InputStream getInputStream() {
        return stream;
    }
}
