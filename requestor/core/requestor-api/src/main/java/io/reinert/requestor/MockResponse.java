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
package io.reinert.requestor;

import io.reinert.requestor.header.Header;
import io.reinert.requestor.payload.SerializedPayload;

/**
 * A response manually created by the user to abort a Request.
 *
 * @author Danilo Reinert
 */
public class MockResponse {

    private final HttpStatus status;
    private final Headers headers;
    private final ResponseType responseType;
    private final SerializedPayload serializedPayload;

    public MockResponse(HttpStatus status) {
        this(status, new Headers());
    }

    public MockResponse(HttpStatus status, Header... headers) {
        this(status, new Headers(headers));
    }

    public MockResponse(HttpStatus status, Iterable<Header> headers) {
        this(status, new Headers(headers));
    }

    public MockResponse(HttpStatus status, Headers headers) {
        this(status, headers, ResponseType.DEFAULT, null);
    }

    public MockResponse(HttpStatus status, Headers headers, ResponseType responseType,
                        SerializedPayload serializedPayload) {
        this.status = status;
        this.headers = headers;
        this.responseType = responseType;
        this.serializedPayload = serializedPayload;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public SerializedPayload getSerializedPayload() {
        return serializedPayload;
    }
}
