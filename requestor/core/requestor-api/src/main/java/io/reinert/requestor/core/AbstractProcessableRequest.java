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

import java.util.List;

import io.reinert.requestor.core.header.ContentTypeHeader;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.uri.Uri;

/**
 * Base class for ProcessableRequest.
 *
 * It delegates every method to the underlying request, except for the process method.
 *
 * @author Danilo Reinert
 */
abstract class AbstractProcessableRequest implements ProcessableRequest {

    protected final ProcessableRequest request;

    public AbstractProcessableRequest(ProcessableRequest request) {
        this.request = request;
    }

    @Override
    public void setUri(Uri uri) {
        request.setUri(uri);
    }

    @Override
    public final void setContentType(String mediaType) {
        request.setContentType(mediaType);
    }

    @Override
    public void setContentType(ContentTypeHeader header) {
        request.setContentType(header);
    }

    @Override
    public final void setAccept(String mediaType) {
        request.setAccept(mediaType);
    }

    @Override
    public final void setHeader(Header header) {
        request.setHeader(header);
    }

    @Override
    public final void setHeader(String header, String value) {
        request.setHeader(header, value);
    }

    @Override
    public final Header delHeader(String name) {
        return request.delHeader(name);
    }

    @Override
    public final void setAuth(Auth auth) {
        request.setAuth(auth);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        request.setAuth(authProvider);
    }

    @Override
    public final void setTimeout(int timeoutMillis) {
        request.setTimeout(timeoutMillis);
    }

    @Override
    public void setRetry(int[] delaysMillis, Event... events) {
        request.setRetry(delaysMillis, events);
    }

    @Override
    public int incrementPollingCount() {
        return request.incrementPollingCount();
    }

    @Override
    public void setPayload(Object payload, String... fields) {
        request.setPayload(payload, fields);
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        request.setSerializedPayload(serializedPayload);
    }

    @Override
    public void serializePayload(SerializedPayload serializedPayload) {
        request.serializePayload(serializedPayload);
    }

    @Override
    public final void setMethod(HttpMethod httpMethod) {
        request.setMethod(httpMethod);
    }

    @Override
    public final String getAccept() {
        return request.getAccept();
    }

    @Override
    public final String getContentType() {
        return request.getContentType();
    }

    @Override
    public boolean hasHeader(String headerName) {
        return request.hasHeader(headerName);
    }

    @Override
    public final Headers getHeaders() {
        return request.getHeaders();
    }

    @Override
    public final String getHeader(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public final HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public Payload getPayload() {
        return request.getPayload();
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        return request.getSerializedPayload();
    }

    @Override
    public final int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public int getDelay() {
        return request.getDelay();
    }

    @Override
    public List<Integer> getRetryDelays() {
        return request.getRetryDelays();
    }

    @Override
    public List<Event> getRetryEvents() {
        return request.getRetryEvents();
    }

    @Override
    public boolean isRetryEnabled() {
        return request.isRetryEnabled();
    }

    @Override
    public boolean isPolling() {
        return request.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return request.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return request.getPollingLimit();
    }

    @Override
    public int getPollingCount() {
        return request.getPollingCount();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return request.getPollingStrategy();
    }

    @Override
    public void stopPolling() {
        request.stopPolling();
    }

    @Override
    public final Uri getUri() {
        return request.getUri();
    }

    @Override
    public final Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public MutableSerializedRequest copy() {
        return request.copy();
    }

    @Override
    public MutableSerializedRequest replicate() {
        return request.replicate();
    }

    @Override
    public final void proceed() {
        request.process();
    }

    @Override
    public final void abort(MockResponse response) {
        request.abort(response);
    }

    @Override
    public final void abort(RequestAbortException error) {
        request.abort(error);
    }

    @Override
    public <T> T retrieve(String key) {
        return request.retrieve(key);
    }

    @Override
    public Store save(String key, Object value) {
        return request.save(key, value);
    }

    @Override
    public Store save(String key, Object value, Level level) {
        return request.save(key, value, level);
    }

    @Override
    public boolean exists(String key) {
        return request.exists(key);
    }

    @Override
    public boolean isEquals(String key, Object value) {
        return request.isEquals(key, value);
    }

    @Override
    public boolean remove(String key) {
        return request.remove(key);
    }

    @Override
    public void clear() {
        request.clear();
    }
}
