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

import java.util.logging.Logger;

import io.reinert.requestor.header.AcceptHeader;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.header.SimpleHeader;
import io.reinert.requestor.payload.SerializedPayload;
import io.reinert.requestor.uri.Uri;

/**
 * Default implementation for {@link RequestBuilder}.
 *
 * @author Danilo Reinert
 */
class RequestBuilderImpl implements RequestBuilder, MutableSerializedRequest, SerializableRequest {

    private static Logger LOGGER = Logger.getLogger(RequestBuilderImpl.class.getName());

    private Uri uri;
    private final VolatileStore store;
    private final Headers headers;
    private HttpMethod httpMethod;
    private int timeout;
    private int delay;
    private PollOptions pollOptions = new PollOptions();
    private Object payload;
    private SerializedPayload serializedPayload;
    private boolean serialized = false;
    private Auth.Provider authProvider = PassThroughAuth.getProvider();

    public RequestBuilderImpl(Uri uri, VolatileStore store) {
        this(uri, store, new Headers());
    }

    public RequestBuilderImpl(Uri uri, VolatileStore store, Headers headers) {
        this.uri = uri;
        this.store = store;
        this.headers = headers;
    }

    private static RequestBuilderImpl copy(RequestBuilderImpl request) {
        RequestBuilderImpl copy = new RequestBuilderImpl(
                Uri.copy(request.uri),
                VolatileStore.copy(request.store),
                Headers.copy(request.headers)
        );
        copy.httpMethod = request.httpMethod;
        copy.timeout = request.timeout;
        copy.delay = request.delay;
        copy.pollOptions = request.pollOptions;
        copy.payload = request.payload;
        copy.serializedPayload = request.serializedPayload;
        copy.serialized = request.serialized;
        copy.authProvider = request.authProvider;
        return copy;
    }

    //===================================================================
    // Request methods
    //===================================================================

    @Override
    public String getAccept() {
        return headers.getValue("Accept");
    }

    @Override
    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public HttpMethod getMethod() {
        return httpMethod;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public void serializePayload(SerializedPayload serializedPayload) {
        if (serialized) {
            throw new IllegalStateException("The request is already serialized." +
                    " Cannot serialize twice.");
        }

        this.serializedPayload = serializedPayload;
        serialized = true;
    }

    @Override
    public SerializedPayload getSerializedPayload() {
        if (!serialized) {
            throw new IllegalStateException("Payload was not serialized yet.");
        }

        return serializedPayload;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public int getPollInterval() {
        return pollOptions.getPollInterval();
    }

    @Override
    public int getPollLimit() {
        return pollOptions.getPollLimit();
    }

    @Override
    public int getPollCounter() {
        return pollOptions.getPollCounter();
    }

    @Override
    public void stopPoll() {
        pollOptions.setPollInterval(0);
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public Auth getAuth() {
        return authProvider.getInstance();
    }

    @Override
    public VolatileStore getStore() {
        return store;
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public RequestBuilder accept(String mediaType) {
        headers.add(new AcceptHeader(mediaType));
        return this;
    }

    @Override
    public RequestBuilder contentType(String mediaType) {
        headers.add(new ContentTypeHeader(mediaType));
        return this;
    }

    @Override
    public RequestBuilder header(String header, String value) {
        headers.set(header, value);
        return this;
    }

    @Override
    public RequestBuilder header(Header header) {
        headers.add(header);
        return this;
    }

    @Override
    public RequestBuilder payload(Object payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public RequestBuilder delay(int delayMillis) {
        if (delayMillis > 0) delay = delayMillis;
        return this;
    }

    @Override
    public RequestBuilder poll(int intervalMillis) {
        return poll(intervalMillis, 0);
    }

    @Override
    public RequestBuilder poll(int intervalMillis, int limit) {
        if (intervalMillis > 0) pollOptions.setPollInterval(intervalMillis);
        pollOptions.setPollLimit(limit);
        return this;
    }

    @Override
    public RequestBuilder timeout(int timeoutMillis) {
        if (timeoutMillis > 0) timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestBuilder auth(final Auth auth) {
        if (auth == null) {
            throw new IllegalArgumentException("Auth cannot be null.");
        }
        return auth(new Auth.Provider() {
            @Override
            public Auth getInstance() {
                return auth;
            }
        });
    }

    @Override
    public RequestBuilder auth(Auth.Provider authProvider) {
        if (authProvider == null) {
            throw new IllegalArgumentException("Auth provider cannot be null.");
        }
        this.authProvider = authProvider;
        return this;
    }

    //===================================================================
    // MutableRequest methods
    //===================================================================

    @Override
    public String getHeader(String headerName) {
        return headers.getValue(headerName);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.add(new SimpleHeader(name, value));
    }

    @Override
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void setContentType(String mediaType) {
        headers.add(new ContentTypeHeader(mediaType));
    }

    @Override
    public void setAccept(String mediaType) {
        headers.add(new AcceptHeader(mediaType));
    }

    @Override
    public void putHeader(Header header) {
        headers.add(header);
    }

    @Override
    public Header popHeader(String name) {
        return headers.pop(name);
    }

    @Override
    public void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public void setAuth(Auth auth) {
        this.auth(auth);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        this.auth(authProvider);
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    @Override
    public void setDelay(int delayMillis) {
        this.delay = delayMillis;
    }

    @Override
    public void setPollInterval(int intervalMillis) {
        pollOptions.setPollInterval(intervalMillis);
    }

    @Override
    public void setPollLimit(int pollLimit) {
        pollOptions.setPollLimit(pollLimit);
    }

    @Override
    public int incrementPollCounter() {
        pollOptions.setPollCounter(pollOptions.getPollCounter() + 1);
        return pollOptions.getPollCounter();
    }

    @Override
    public void setPayload(Object payload) {
        if (serialized) {
            LOGGER.warning("Setting a deserialized payload in an already serialized request.");
        }

        this.payload = payload;
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        if (!serialized) {
            throw new IllegalStateException("Request payload was not serialized yet." +
                    "Cannot change the serialized payload before serializing the request.");
        }

        this.serializedPayload = serializedPayload;
    }

    @Override
    public RequestBuilderImpl copy() {
        return build();
    }

    //===================================================================
    // Own methods
    //===================================================================

    protected RequestBuilderImpl build() {
        return RequestBuilderImpl.copy(this);
    }
}
