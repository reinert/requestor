/*
 * Copyright 2015 Danilo Reinert
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

/**
 * Represents a response with its payload raw.
 *
 * @author Danilo Reinert
 */
public class RawResponseImpl extends ResponseImpl<Payload> implements RawResponse {

    public RawResponseImpl(StatusType status, Headers headers, ResponseType type, Payload payload) {
        super(status, headers, type, payload);
    }

    @Override
    public void addHeader(Header header) {
        super.addHeader(header);
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
}
