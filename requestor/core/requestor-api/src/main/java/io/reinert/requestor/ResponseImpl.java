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

import java.util.Collections;

import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.LinkHeader;
import io.reinert.requestor.header.SimpleHeader;

/**
 * Represents a response with payload already deserialized.
 *
 * @param <T>   Type of the payload.
 *
 * @author Danilo Reinert
 */
public class ResponseImpl<T> implements Response<T> {

    private final Headers headers;
    private final LinkHeader linkHeader;
    private final StatusType status;
    private T payload;
    private ResponseType responseType;

    public ResponseImpl(StatusType status, Headers headers, ResponseType responseType, T payload) {
        if (headers == null)
            throw new NullPointerException("Headers cannot be null");
        this.headers = headers;
        this.linkHeader = (LinkHeader) headers.get("Link");
        this.status = status;
        this.responseType = responseType;
        this.payload = payload;
    }

    public static ResponseImpl<RawResponse> fromRawResponse(RawResponse rawResponse) {
        return new ResponseImpl<RawResponse>(rawResponse.getStatus(), rawResponse.getHeaders(),
                rawResponse.getResponseType(), rawResponse);
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
    public StatusType getStatus() {
        return status;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public ResponseType getResponseType() {
        return responseType;
    }

    protected void addHeader(Header header) {
        headers.add(header);
    }

    protected void setHeader(String name, String value) {
        headers.add(new SimpleHeader(name, value));
    }

    protected void setContentType(String contentType) {
        headers.add(new ContentTypeHeader(contentType));
    }

    protected void setPayload(T payload) {
        this.payload = payload;
    }

    protected void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }
}
