/*
 * Copyright 2021-2022 Danilo Reinert
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

/**
 * Base class for ProcessableResponse.
 *
 * It delegates every method to the underlying response, except for the process method.
 *
 * @author Danilo Reinert
 */
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
    public <T> T getPayload() {
        return response.getPayload();
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        return response.getSerializedPayload();
    }

    @Override
    public <T> T getValue(String key) {
        return response.getValue(key);
    }

    @Override
    public Data getData(String key) {
        return response.getData(key);
    }

    @Override
    public ProcessableResponse save(String key, Object value) {
        response.save(key, value);
        return this;
    }

    @Override
    public ProcessableResponse save(String key, Object value, Level level) {
        response.save(key, value, level);
        return this;
    }

    @Override
    public ProcessableResponse save(String key, Object value, long ttl, Level level) {
        response.save(key, value, ttl, level);
        return this;
    }

    @Override
    public ProcessableResponse save(String key, Object value, long ttl) {
        response.save(key, value, ttl);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return response.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return response.exists(key, value);
    }

    @Override
    public Data remove(String key) {
        return response.remove(key);
    }

    @Override
    public void clear() {
        response.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        response.clear(fireRemovedEvent);
    }

    @Override
    public ProcessableResponse onSaved(String key, Handler handler) {
        response.onSaved(key, handler);
        return this;
    }

    @Override
    public ProcessableResponse onRemoved(String key, Handler handler) {
        response.onRemoved(key, handler);
        return this;
    }

    @Override
    public ProcessableResponse onExpired(String key, Handler handler) {
        response.onExpired(key, handler);
        return this;
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

    @Override
    public Session getSession() {
        return response.getSession();
    }
}
