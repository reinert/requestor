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
import io.reinert.requestor.payload.type.PayloadType;

public abstract class AbstractProcessableResponse implements ProcessableResponse {

    protected final ProcessableResponse response;

    public AbstractProcessableResponse(ProcessableResponse response) {
        this.response = response;
    }

    @Override
    public final void setContentType(String mediaType) {
        response.setContentType(mediaType);
    }

    @Override
    public final void putHeader(Header header) {
        response.putHeader(header);
    }

    @Override
    public final void setHeader(String header, String value) {
        response.setHeader(header, value);
    }

    @Override
    public final Header popHeader(String name) {
        return response.popHeader(name);
    }

    @Override
    public void setResponseType(ResponseType responseType) {
        response.setResponseType(responseType);
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
    public void deserializePayload(Object payload) {
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
    public Object getPayload() {
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
    public ResponseType getResponseType() {
        return response.getResponseType();
    }

    @Override
    public Request getRequest() {
        return response.getRequest();
    }

    @Override
    public final void proceed() {
        response.process();
    }

    @Override
    public Response getRawResponse() {
        return response;
    }
}
