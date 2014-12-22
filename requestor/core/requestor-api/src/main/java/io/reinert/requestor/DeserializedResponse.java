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

/**
 * Represents a response with payload already deserialized.
 *
 * @param <T>   Type of the payload.
 *
 * @author Danilo Reinert
 */
public class DeserializedResponse<T> implements Response<T> {

    private final Headers headers;
    private final int statusCode;
    private final String statusText;
    private final T payload;
    private final ResponseType type;

    public DeserializedResponse(Headers headers, int statusCode, String statusText, ResponseType type, T payload) {
        if (headers == null)
            throw new NullPointerException("Headers cannot be null");
        this.headers = headers;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String getHeader(String header) {
        return headers.getValue(header);
    }

    @Override
    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public ResponseType getResponseType() {
        return type;
    }
}
