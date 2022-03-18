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

import java.io.UnsupportedEncodingException;

/**
 * Represents an HTTP payload.
 * It envelops a String.
 *
 * @author Danilo Reinert
 */
public class TextSerializedPayload implements SerializedPayload {

    public static final String DEFAULT_CHARSET = "UTF-8";

    protected final String charset;
    protected String string;
    protected byte[] bytes;

    public TextSerializedPayload(String string) {
        this(string, DEFAULT_CHARSET);
    }

    public TextSerializedPayload(String string, String charset) {
        this.string = string == null ? "" : string;
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    public TextSerializedPayload(byte[] bytes) {
        this(bytes, DEFAULT_CHARSET);
    }

    public TextSerializedPayload(byte[] bytes, String charset) {
        this.bytes = bytes == null ? new byte[]{} : bytes;
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    @Override
    public boolean isEmpty() {
        return string != null ? string.length() == 0 : bytes == null || bytes.length == 0;
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
    public byte[] asBytes() {
        if (!isBytesAvailable()) {
            try {
                bytes = string.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException("Cannot convert the string to bytes because the given " +
                        "charset (" + charset + ") is not supported.", e);
            }
        }
        return bytes;
    }

    @Override
    public String asString() {
        if (!isStringAvailable()) {
            try {
                string = new String(bytes, charset);
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException("Cannot convert the byte array to string because the given " +
                        "charset (" + charset + ") is not supported.", e);
            }
        }
        return string;
    }

    @Override
    public long getLength() {
        return asBytes().length;
    }
}
