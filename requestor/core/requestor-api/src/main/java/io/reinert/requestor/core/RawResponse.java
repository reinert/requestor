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
package io.reinert.requestor.core;

import java.util.Collections;
import java.util.logging.Logger;

import io.reinert.requestor.core.header.ContentTypeHeader;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.header.LinkHeader;
import io.reinert.requestor.core.header.SimpleHeader;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * Represents a response just received by the request and prepared to be processed.
 *
 * @author Danilo Reinert
 */
public class RawResponse implements MutableResponse, DeserializableResponse, ProcessableResponse {

    private static final Logger logger = Logger.getLogger(RawResponse.class.getName());

    private final Headers headers;
    private final LinkHeader linkHeader;
    private final HttpStatus status;
    private final RequestOptions requestOptions;
    private Object payload;
    private SerializedPayload serializedPayload;
    private boolean deserialized = false;
    private final PayloadType payloadType;
    private final Deferred<?> deferred;

    public RawResponse(RequestOptions requestOptions, HttpStatus status, Headers headers, PayloadType payloadType,
                       SerializedPayload serializedPayload, Deferred<?> deferred) {
        this.requestOptions = requestOptions;
        if (headers == null) throw new IllegalArgumentException("Headers cannot be null");
        this.headers = headers;
        this.linkHeader = (LinkHeader) headers.get("Link");
        this.status = status;
        this.payloadType = payloadType;
        this.serializedPayload = serializedPayload;
        this.deferred = deferred;
    }

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
        if (!deserialized) {
            throw new IllegalStateException("Payload was not deserialized yet.");
        }

        return payload;
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        return serializedPayload;
    }

    @Override
    public PayloadType getPayloadType() {
        return payloadType;
    }

    @Override
    public void deserializePayload(Object payload) {
        if (deserialized) {
            throw new IllegalStateException("Deserialized payload was already set. Cannot deserialize twice.");
        }

        this.payload = payload;
        deserialized = true;
    }

    @Override
    public RequestOptions getRequestOptions() {
        return requestOptions;
    }

    @Override
    public Store getStore() {
        return deferred.getRequest().getStore();
    }

    @Override
    public String toString() {
        return "ResponseImpl{" +
                "headers=" + headers +
                ", linkHeader=" + (linkHeader != null ? linkHeader : "null") +
                ", status=" + status +
                ", payload=" + (payload != null ? payload : "null") +
                ", serializedPayload=" + (serializedPayload != null ? serializedPayload : "null") +
                '}';
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        if (deserialized) {
            logger.warning("Setting a serialized payload in an already serialized response.");
        }

        this.serializedPayload = serializedPayload;
    }

    @Override
    public void setPayload(Object payload) {
        if (!deserialized) {
            throw new IllegalStateException("Response payload was not deserialized yet." +
                    "Cannot change the deserialized payload before deserializing the response.");
        }

        this.payload = payload;
    }

    @Override
    public void setContentType(String contentType) {
        headers.add(new ContentTypeHeader(contentType));
    }

    @Override
    public void setHeader(Header header) {
        headers.add(header);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.add(new SimpleHeader(name, value));
    }

    @Override
    public Header delHeader(String headerName) {
        return headers.pop(headerName);
    }

    @Override
    public void proceed() {
        this.process();
    }

    @Override
    public void process() {
        deferred.resolve(this);
    }

    @Override
    public Response getRawResponse() {
        return this;
    }
}
