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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reinert.requestor.core.payload.SerializedPayload;

/**
 * Represents an HTTP payload.
 * It envelops a list of payloads.
 *
 * @author Danilo Reinert
 */
public class CompositeSerializedPayload implements SerializedPayload, Iterable<SerializedPayload> {

    protected final List<SerializedPayload> parts = new ArrayList<>();
    private long totalLength = -1;

    @Override
    public boolean isEmpty() {
        return parts.isEmpty();
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
        if (totalLength == -1) {
            totalLength = 0;
            for (SerializedPayload part : parts) {
                if (part.getLength() == 0) {
                    totalLength = 0;
                    break;
                }
                totalLength += part.getLength();
            }
        }
        return totalLength;
    }

    @Override
    public byte[] asBytes() {
        return null;
    }

    @Override
    public String asString() {
        return null;
    }

    public CompositeSerializedPayload add(SerializedPayload part) {
        parts.add(part);
        return this;
    }

    @Override
    public Iterator<SerializedPayload> iterator() {
        return parts.iterator();
    }
}
