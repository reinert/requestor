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

import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;

abstract class AbstractProcessableResponse implements ProcessableResponse {

    protected final ProcessableResponse response;

    public AbstractProcessableResponse(ProcessableResponse response) {
        this.response = response;
    }

    @Override
    public final void setContentType(String mediaType) {
        response.setContentType(mediaType);
    }

    @Override
    public boolean hasHeader(String headerName) {
        return response.hasHeader(headerName);
    }

    @Override
    public final void setHeader(Header header) {
        response.setHeader(header);
    }

    @Override
    public final void setHeader(String header, String value) {
        response.setHeader(header, value);
    }

    @Override
    public final Header delHeader(String name) {
        return response.delHeader(name);
    }

    @Override
    public void setPayload(Object payload) {
        response.setPayload(payload);
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        response.setSerializedPayload(serializedPayload);
    }

    @Override
    public void deserializePayload(Payload payload) {
        response.deserializePayload(payload);
    }

    @Override
    public final String getContentType() {
        return response.getContentType();
    }

    @Override
    public final Headers getHeaders() {
        return response.getHeaders();
    }

    @Override
    public final String getHeader(String headerName) {
        return response.getHeader(headerName);
    }

    @Override
    public Payload getPayload() {
        return response.getPayload();
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        return response.getSerializedPayload();
    }

    @Override
    public final Store getStore() {
        return response.getStore();
    }

    @Override
    public Iterable<Link> getLinks() {
        return response.getLinks();
    }

    @Override
    public boolean hasLink(String relation) {
        return response.hasLink(relation);
    }

    @Override
    public Link getLink(String relation) {
        return response.getLink(relation);
    }

    @Override
    public int getStatusCode() {
        return response.getStatusCode();
    }

    @Override
    public HttpStatus getStatus() {
        return response.getStatus();
    }

    @Override
    public PayloadType getPayloadType() {
        return response.getPayloadType();
    }

    @Override
    public RequestOptions getRequestOptions() {
        return response.getRequestOptions();
    }

    @Override
    public final void proceed() {
        response.process();
    }

    @Override
    public Response getRawResponse() {
        return response.getRawResponse();
    }
}