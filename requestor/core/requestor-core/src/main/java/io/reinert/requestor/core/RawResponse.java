/*
 * Copyright 2015-2022 Danilo Reinert
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

import io.reinert.requestor.core.header.ContentTypeHeader;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.header.LinkHeader;
import io.reinert.requestor.core.header.SimpleHeader;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * Represents a response just received by the request and prepared to be processed.
 *
 * @author Danilo Reinert
 */
public class RawResponse implements MutableResponse, DeserializableResponse, ProcessableResponse {

    private final Headers headers;
    private final LinkHeader linkHeader;
    private final HttpStatus status;
    private Payload payload;
    private SerializedPayload serializedPayload;
    private boolean deserialized = false;
    private boolean loaded = false;
    private final PayloadType payloadType;
    private final Deferred<?> deferred;
    private final Request<?> request;
    private IncomingResponse incomingResponse;

    public RawResponse(Deferred<?> deferred, HttpStatus status, Headers headers, PayloadType payloadType) {
        this(deferred, status, headers, payloadType, null);
    }

    public RawResponse(Deferred<?> deferred, HttpStatus status, Headers headers, PayloadType payloadType,
                       SerializedPayload serializedPayload) {
        if (headers == null) throw new IllegalArgumentException("Headers cannot be null");
        this.headers = headers;
        this.linkHeader = (LinkHeader) headers.get("Link");
        this.status = status;
        this.payloadType = payloadType;
        this.serializedPayload = serializedPayload;
        this.deferred = deferred;
        this.request = deferred.getRequest();
    }

    @Override
    public String getHeader(String headerName) {
        return headers.getValue(headerName);
    }

    @Override
    public boolean hasHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    @Override
    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    @Override
    public Iterable<Link> getLinks() {
        return linkHeader != null ? linkHeader.getLinks() : Collections.<Link>emptyList();
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
    public <T> T getPayload() {
        if (!deserialized) {
            throw new IllegalStateException("Payload was not deserialized yet.");
        }

        return payload.asObject();
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
    public void deserializePayload(Payload payload) {
        if (deserialized) {
            throw new IllegalStateException("Deserialized payload was already set. Cannot deserialize twice.");
        }

        this.payload = payload == null ? Payload.EMPTY_PAYLOAD : payload;
        deserialized = true;
    }

    @Override
    public RequestOptions getRequestOptions() {
        return request;
    }

    @Override
    public <T> T retrieve(String key) {
        return request.retrieve(key);
    }

    @Override
    public RawResponse save(String key, Object value) {
        request.save(key, value);
        return this;
    }

    @Override
    public RawResponse save(String key, Object value, Level level) {
        request.save(key, value, level);
        return this;
    }

    @Override
    public RawResponse save(String key, Object value, long ttl, Level level) {
        request.save(key, value, ttl, level);
        return this;
    }

    @Override
    public RawResponse save(String key, Object value, long ttl) {
        request.save(key, value, ttl);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return request.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return request.exists(key, value);
    }

    @Override
    public Data remove(String key) {
        return request.remove(key);
    }

    @Override
    public void clear() {
        request.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        request.clear(fireRemovedEvent);
    }

    @Override
    public RawResponse onSaved(String key, Handler handler) {
        request.onSaved(key, handler);
        return this;
    }

    @Override
    public RawResponse onRemoved(String key, Handler handler) {
        request.onRemoved(key, handler);
        return this;
    }

    @Override
    public RawResponse onExpired(String key, Handler handler) {
        request.onExpired(key, handler);
        return this;
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        if (deserialized) {
            throw new IllegalStateException("Payload was already deserialized. Cannot set a serialized payload after" +
                    " deserialization.");
        }

        this.serializedPayload = serializedPayload == null ? SerializedPayload.EMPTY_PAYLOAD : serializedPayload;
        loaded = true;
        deferred.getResponseBodyLock().signalAll();
    }

    @Override
    public void setPayload(Object payload) {
        if (!deserialized) {
            throw new IllegalStateException("Response payload was not deserialized yet." +
                    "Cannot change the deserialized payload before deserializing the response.");
        }

        this.payload = payload == null ? Payload.EMPTY_PAYLOAD : payload instanceof Payload ?
                (Payload) payload : new Payload(payload);
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
    public Session getSession() {
        return request.getSession();
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

    public Deferred<?> getDeferred() {
        return deferred;
    }

    public boolean isDeserialized() {
        return deserialized;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public synchronized IncomingResponse getIncomingResponse() {
        if (incomingResponse == null) {
            incomingResponse = new IncomingResponseImpl(this);
        }

        return incomingResponse;
    }
}
