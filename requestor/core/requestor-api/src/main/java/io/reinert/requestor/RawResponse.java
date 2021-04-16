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

import java.util.Collections;

import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.LinkHeader;
import io.reinert.requestor.header.SimpleHeader;
import io.reinert.requestor.payload.PayloadType;

/**
 * Represents a response just received by the request and prepared to be processed.
 *
 * @author Danilo Reinert
 */
public class RawResponse implements Response, ResponseInterceptorContext, ResponseFilterContext {

    private final Headers headers;
    private final LinkHeader linkHeader;
    private final HttpStatus status;
    private final Request request;
    private Object payload;
    private Payload serializedPayload;
    private ResponseType responseType;
    private boolean deserialized = false;
    private final PayloadType payloadType;

    public RawResponse(Request request, HttpStatus status, Headers headers, ResponseType responseType,
                       Payload serializedPayload, PayloadType payloadType) {
        this.request = request;
        this.payloadType = payloadType;
        if (headers == null)
            throw new NullPointerException("Headers cannot be null");
        this.headers = headers;
        this.linkHeader = (LinkHeader) headers.get("Link");
        this.status = status;
        this.responseType = responseType;
        this.serializedPayload = serializedPayload;
    }

//    public static ResponseImpl<RawResponse> fromRawResponse(Request request, RawResponse rawResponse) {
//        return new ResponseImpl<RawResponse>(request, rawResponse.getStatus(), rawResponse.getHeaders(),
//                rawResponse.getResponseType(), rawResponse, rawResponse.getSerializedPayload(), entityType,
//                collectionType);
//    }

    @Override
    public String getHeader(String headerName) {
        return headers.getValue(headerName);
    }

    @Override
    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<Link> getLinks() {
        return linkHeader != null ? linkHeader.getLinks() : (Iterable<Link>) Collections.EMPTY_LIST;
    }

    @Override
    public boolean hasLink(String relation) {
        return linkHeader != null && linkHeader.hasLink(relation);
    }

    @Override
    public Link getLink(String relation) {
        return linkHeader != null ? linkHeader.getLink(relation) : null;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public int getStatusCode() {
        return status.getStatusCode();
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public Payload getSerializedPayload() {
        return serializedPayload;
    }

    @Override
    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setDeserializedPayload(Object payload) {
        if (deserialized)
            throw new IllegalStateException("Deserialized payload was already set. Cannot set twice.");

        this.payload = payload;
        deserialized = true;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public Store getStore() {
        return request.getStore();
    }

    @Override
    public String toString() {
        return "ResponseImpl{" +
                "headers=" + headers +
                ", linkHeader=" + (linkHeader != null ? linkHeader : "null") +
                ", status=" + status +
                ", payload=" + (payload != null ? payload : "null") +
                ", serializedPayload=" + (serializedPayload != null ? serializedPayload : "null") +
                ", responseType=" + responseType +
                '}';
    }

    @Override
    public void setSerializedPayload(Payload payload) {
        this.serializedPayload = payload;
    }

    @Override
    public void setContentType(String contentType) {
        headers.add(new ContentTypeHeader(contentType));
    }

    @Override
    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Override
    public void putHeader(Header header) {
        headers.add(header);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.add(new SimpleHeader(name, value));
    }

    @Override
    public Header popHeader(String headerName) {
        return headers.pop(headerName);
    }
}
