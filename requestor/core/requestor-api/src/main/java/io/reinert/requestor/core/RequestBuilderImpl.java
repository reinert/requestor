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

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import io.reinert.requestor.core.header.AcceptHeader;
import io.reinert.requestor.core.header.ContentTypeHeader;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.header.SimpleHeader;
import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.uri.Uri;

/**
 * Default implementation for {@link RequestBuilder}.
 *
 * @author Danilo Reinert
 */
class RequestBuilderImpl implements PollingRequestBuilder, MutableSerializedRequest, SerializableRequest {

    private static final Logger logger = Logger.getLogger(RequestBuilderImpl.class.getName());

    private Uri uri;
    private TransientStore store;
    private Headers headers;
    private Auth.Provider authProvider;
    private HttpMethod httpMethod;
    private int timeout;
    private int delay;
    private RetryOptions retryOptions;
    private PollingOptions pollingOptions;
    private Payload payload;
    private SerializedPayload serializedPayload;
    private boolean serialized;

    public RequestBuilderImpl(Uri uri, TransientStore store) {
        this(uri, store, null, null, null, 0, 0, null, null, null, null, false);
    }

    public RequestBuilderImpl(Uri uri, TransientStore store, Headers headers, Auth.Provider authProvider,
                              HttpMethod httpMethod, int timeout, int delay, RetryOptions retryOptions,
                              PollingOptions pollingOptions, Payload payload, SerializedPayload serializedPayload,
                              boolean serialized) {
        if (uri == null) throw new IllegalArgumentException("Uri cannot be null");
        this.uri = uri;
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.store = store;
        this.headers = headers != null ? headers : new Headers();
        this.authProvider = authProvider;
        this.httpMethod = httpMethod;
        this.timeout = timeout;
        this.delay = delay;
        this.retryOptions = retryOptions;
        this.pollingOptions = pollingOptions != null ? pollingOptions : new PollingOptions();
        this.payload = payload != null ? payload : Payload.EMPTY_PAYLOAD;
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
    public Payload getPayload() {
        return payload;
    }

    @Override
    public void serializePayload(SerializedPayload serializedPayload) {
        if (serialized) {
            throw new IllegalStateException("The request is already serialized." +
                    " Cannot serialize twice.");
        }

        this.serializedPayload = serializedPayload == null ? SerializedPayload.EMPTY_PAYLOAD : serializedPayload;
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
    public int getPollingCount() {
        return pollingOptions.getPollingCount();
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
    public TransientStore getStore() {
        return store;
    }

    @Override
    public List<Integer> getRetryDelays() {
        return retryOptions != null ? retryOptions.getDelays() : Collections.<Integer>emptyList();
    }

    @Override
    public List<Event> getRetryEvents() {
        return retryOptions != null ? retryOptions.getEvents() : Collections.<Event>emptyList();
    }

    @Override
    public boolean isRetryEnabled() {
        return retryOptions != null && retryOptions.isEnabled();
    }

    //===================================================================
    // RequestBuilder methods
    //===================================================================

    @Override
    public RequestBuilderImpl accept(String mediaType) {
        headers.add(new AcceptHeader(mediaType));
        return this;
    }

    @Override
    public RequestBuilderImpl contentType(String mediaType) {
        headers.add(new ContentTypeHeader(mediaType));
        return this;
    }

    @Override
    public RequestBuilderImpl header(String header, String value) {
        headers.set(header, value);
        return this;
    }

    @Override
    public RequestBuilderImpl header(Header header) {
        headers.add(header);
        return this;
    }

    @Override
    public RequestBuilderImpl payload(Object payload, String... fields) {
        this.payload = payload == null ? Payload.EMPTY_PAYLOAD : payload instanceof Payload ?
                (Payload) payload : new Payload(payload, fields);
        return this;
    }

    @Override
    public RequestBuilderImpl delay(int delayMillis) {
        if (delayMillis > 0) delay = delayMillis;
        return this;
    }

    @Override
    public RequestBuilderImpl retry(int[] delaysMillis, Event... events) {
        if (delaysMillis != null && delaysMillis.length > 0 && events.length > 0) {
            retryOptions = new RetryOptions(delaysMillis, events);
        }
        return this;
    }

    @Override
    public RequestBuilderImpl poll(PollingStrategy strategy) {
        pollingOptions.startPolling(strategy, 0, 0);
        return this;
    }

    @Override
    public RequestBuilderImpl poll(PollingStrategy strategy, int intervalMillis) {
        pollingOptions.startPolling(strategy, intervalMillis, 0);
        return this;
    }

    @Override
    public RequestBuilderImpl poll(PollingStrategy strategy, int intervalMillis, int limit) {
        pollingOptions.startPolling(strategy, intervalMillis, limit);
        return this;
    }

    @Override
    public RequestBuilderImpl timeout(int timeoutMillis) {
        if (timeoutMillis > 0) timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestBuilderImpl auth(final Auth auth) {
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
    public RequestBuilderImpl auth(Auth.Provider authProvider) {
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
    public boolean hasHeader(String headerName) {
        return headers.containsKey(headerName);
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
    public void setHeader(Header header) {
        headers.add(header);
    }

    @Override
    public Header delHeader(String name) {
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
    public void setRetry(int[] delaysMillis, Event... events) {
        retry(delaysMillis, events);
    }

    @Override
    public int incrementPollingCount() {
        return pollingOptions.incrementPollingCount();
    }

    @Override
    public void setPayload(Object payload, String... fields) {
        if (serialized) {
            throw new IllegalStateException("Request payload was already deserialized." +
                    "Cannot change the payload after it was serialized.");
        }

        this.payload(payload, fields);
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        if (!serialized) {
            throw new IllegalStateException("Request payload was not serialized yet." +
                    "Cannot change the serialized payload before serializing the request.");
        }

        this.serializedPayload = serializedPayload == null ? SerializedPayload.EMPTY_PAYLOAD : serializedPayload;
    }

    //===================================================================
    // MutableSerializedRequest
    //===================================================================

    @Override
    public RequestBuilderImpl copy() {
        return new RequestBuilderImpl(
                Uri.copy(uri),
                TransientStore.copy(store),
                Headers.copy(headers),
                authProvider,
                httpMethod,
                timeout,
                delay,
                retryOptions != null ? RetryOptions.copy(retryOptions) : null,
                PollingOptions.copy(pollingOptions),
                payload,
                serializedPayload,
                serialized
        );
    }

    @Override
    public RequestBuilderImpl replicate() {
        return new RequestBuilderImpl(
                Uri.copy(uri),
                store, // keep store reference
                Headers.copy(headers),
                authProvider,
                httpMethod,
                timeout,
                delay,
                retryOptions != null ? RetryOptions.copy(retryOptions) : null,
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
        return copy();
    }
}
