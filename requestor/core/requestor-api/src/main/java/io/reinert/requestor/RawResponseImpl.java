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

import java.util.Collection;

import io.reinert.requestor.header.Header;

/**
 * Represents a response with its payload raw.
 *
 * @author Danilo Reinert
 */
public class RawResponseImpl extends ResponseImpl<Payload> implements RawResponse {

    private final SerializationEngine serializationEngine;

    public RawResponseImpl(Request request, StatusType status, Headers headers, ResponseType type, Payload payload,
                           SerializationEngine serializationEngine) {
        super(request, status, headers, type, payload);
        this.serializationEngine = serializationEngine;
    }

    @Override
    public void putHeader(Header headerName) {
        super.putHeader(headerName);
    }

    @Override
    public void setContentType(String contentType) {
        super.setContentType(contentType);
    }

    @Override
    public void setPayload(Payload payload) {
        super.setPayload(payload);
    }

    @Override
    public void setResponseType(ResponseType responseType) {
        super.setResponseType(responseType);
    }

    @Override
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
    }

    @Override
    public Header popHeader(String headerName) {
        return super.popHeader(headerName);
    }

    @Override
    public <T> T getPayloadAs(Class<T> type) {
        return serializationEngine.deserializePayload(getRequest(), this, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, C extends Collection> Collection<T> getPayloadAs(Class<T> type, Class<C> containerType) {
        return serializationEngine.deserializePayload(getRequest(), this, type, containerType);
    }
}
