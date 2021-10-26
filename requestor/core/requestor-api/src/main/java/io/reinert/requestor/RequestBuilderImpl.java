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

    private static final Logger logger = Logger.getLogger(RequestBuilderImpl.class.getName());

    private Uri uri;
    private VolatileStore store;
    private Headers headers;
    private Auth.Provider authProvider;
    private HttpMethod httpMethod;
    private int timeout;
    private int delay;
    private PollingOptions pollingOptions;
    private Object payload;
    private SerializedPayload serializedPayload;
    private boolean serialized;

    public RequestBuilderImpl(Uri uri, VolatileStore store) {
        this(uri, store, null, null, null, 0, 0, null, null, null, false);
    }

    public RequestBuilderImpl(Uri uri, VolatileStore store, Headers headers, Auth.Provider authProvider,
                              HttpMethod httpMethod, int timeout, int delay, PollingOptions pollingOptions,
                              Object payload, SerializedPayload serializedPayload, boolean serialized) {
        if (uri == null) throw new IllegalArgumentException("Uri cannot be null");
        this.uri = uri;
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.store = store;
        this.headers = headers != null ? headers : new Headers();
        this.authProvider = authProvider;
        this.httpMethod = httpMethod;
        this.timeout = timeout;
        this.delay = delay;
        this.pollingOptions = pollingOptions != null ? pollingOptions : new PollingOptions();
        this.payload = payload;
        this.serializedPayload = serializedPayload;
        this.serialized = serialized;
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
    public boolean isPolling() {
        return pollingOptions.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return pollingOptions.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return pollingOptions.getPollingLimit();
    }

    @Override
    public int getPollingCounter() {
        return pollingOptions.getPollingCounter();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return pollingOptions.getPollingStrategy();
    }

    @Override
    public void stopPolling() {
        pollingOptions.stopPolling();
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public Auth getAuth() {
        if (authProvider == null) return null;
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
    public RequestBuilder poll(PollingStrategy strategy) {
        pollingOptions.startPolling(strategy, 0, 0);
        return this;
    }

    @Override
    public RequestBuilder poll(PollingStrategy strategy, int intervalMillis) {
        pollingOptions.startPolling(strategy, intervalMillis, 0);
        return this;
    }

    @Override
    public RequestBuilder poll(PollingStrategy strategy, int intervalMillis, int limit) {
        pollingOptions.startPolling(strategy, intervalMillis, limit);
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
    public void setPollingActive(boolean active) {
        pollingOptions.setPollingActive(active);
    }

    @Override
    public void setPollingStrategy(PollingStrategy strategy) {
        pollingOptions.setPollingStrategy(strategy);
    }

    @Override
    public void setPollingInterval(int intervalMillis) {
        pollingOptions.setPollingInterval(intervalMillis);
    }

    @Override
    public void setPollingLimit(int pollLimit) {
        pollingOptions.setPollingLimit(pollLimit);
    }

    @Override
    public int incrementPollingCounter() {
        return pollingOptions.incrementPollingCounter();
    }

    @Override
    public void setPayload(Object payload) {
        if (serialized) {
            logger.warning("Setting a deserialized payload in an already serialized request.");
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
        return new RequestBuilderImpl(
                Uri.copy(uri),
                store, // keep store reference
                Headers.copy(headers),
                authProvider,
                httpMethod,
                timeout,
                delay,
                pollingOptions, // keep pollingOptions reference
                payload,
                serializedPayload,
                serialized
        );
    }

    //===================================================================
    // Own methods
    //===================================================================

    protected RequestBuilderImpl build() {
        return new RequestBuilderImpl(
                Uri.copy(uri),
                VolatileStore.copy(store),
                Headers.copy(headers),
                authProvider,
                httpMethod,
                timeout,
                delay,
                PollingOptions.copy(pollingOptions),
                payload,
                null,
                false
        );
    }
}
