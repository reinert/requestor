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

import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;

/**
 * Represents a response with its payload raw.
 *
 * @author Danilo Reinert
 */
public class SerializedResponseImpl implements SerializedResponse, ResponseFilterContext, ResponseInterceptorContext {

    private final String statusText;
    private final int statusCode;
    private final Headers headers;
    private ResponseType responseType;
    private Payload payload;

    public SerializedResponseImpl(String statusText, int statusCode, Headers headers, ResponseType responseType,
                                  Payload payload) {
        this.statusText = statusText;
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseType = responseType;
        this.payload = payload;
    }

    @Override
    public void addHeader(Header header) {
        headers.add(header);
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
        return new Headers(headers);
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
    public Payload getPayload() {
        return payload;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public void setContentType(String mediaType) {
        headers.add(new ContentTypeHeader(mediaType));
    }

    @Override
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }
}
