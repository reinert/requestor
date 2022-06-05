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
package io.reinert.requestor.core;

/**
 * Base64 decoder and encoder.
 *
 * @author Danilo Reinert
 */
public abstract class Base64Codec {

    private static Base64Codec INSTANCE = null;

    public static Base64Codec getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Requestor was not initialized. Please call Requestor.init.");
        }
        return INSTANCE;
    }

    public static synchronized void setInstance(Base64Codec base64Codec) {
        INSTANCE = base64Codec;
    }

    public interface Holder {
        void setBase64Codec(Base64Codec codec);
    }

    public abstract String decode(String encoded, String toCharset);

    public abstract String decode(byte[] encoded, String toCharset);

    public abstract String encode(String text, String fromCharset);

}
