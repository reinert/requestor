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
 * Represents a Request/Response processing step.
 *
 * @author Danilo Reinert
 */
public enum Process {

    FILTER_REQUEST,
    SERIALIZE_REQUEST,
    INTERCEPT_REQUEST,
    AUTH_REQUEST,
    FILTER_RESPONSE,
    DESERIALIZE_RESPONSE,
    INTERCEPT_RESPONSE;

    public static Process[] all() {
        return Process.values();
    }

    public static Process[] request() {
        return new Process[]{ FILTER_REQUEST, SERIALIZE_REQUEST, INTERCEPT_REQUEST, AUTH_REQUEST};
    }

    public static Process[] response() {
        return new Process[]{ FILTER_RESPONSE, DESERIALIZE_RESPONSE, INTERCEPT_RESPONSE };
    }
}
