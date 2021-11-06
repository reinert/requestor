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

import io.reinert.requestor.type.ArrayBuffer;
import io.reinert.requestor.type.Blob;
import io.reinert.requestor.type.Document;
import io.reinert.requestor.type.Json;

/**
 * The type of response expected from the XMLHttpRequest.
 */
public enum ResponseType {

    DEFAULT(""),
    ARRAY_BUFFER("arraybuffer"),
    BLOB("blob"),
    DOCUMENT("document"),
    JSON("json"),
    TEXT("text");

    private final String value;

    private ResponseType(String value) {
        this.value = value;
    }

    public static ResponseType of(Class<?> type) {
        if (type == ArrayBuffer.class) {
            return ARRAY_BUFFER;
        }

        if (type == Blob.class) {
            return BLOB;
        }

        if (type == Document.class) {
            return DOCUMENT;
        }

        if (type == Json.class) {
            return JSON;
        }

        return DEFAULT;
    }

    public static ResponseType of(String responseTypeString) {
        if ("arraybuffer".equalsIgnoreCase(responseTypeString)) {
            return ARRAY_BUFFER;
        }

        if ("blob".equalsIgnoreCase(responseTypeString)) {
            return BLOB;
        }

        if ("document".equalsIgnoreCase(responseTypeString)) {
            return DOCUMENT;
        }

        if ("json".equalsIgnoreCase(responseTypeString)) {
            return JSON;
        }

        if ("text".equalsIgnoreCase(responseTypeString)) {
            return TEXT;
        }

        return DEFAULT;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
