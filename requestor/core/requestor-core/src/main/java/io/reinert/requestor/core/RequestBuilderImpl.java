/*
 * Copyright 2014-2022 Danilo Reinert
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
import java.util.HashSet;
import java.util.Set;
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

    private final Session session;
    private Uri uri;
    private LeafStore store;
    private Headers headers;
    private Auth.Provider authProvider;
    private HttpMethod httpMethod;
    private int timeout;
    private int delay;
    private String charset;
    private RetryPolicy.Provider retryPolicyProvider;
    private PollingOptions pollingOptions;
    private Payload payload;
    private SerializedPayload serializedPayload;
    private boolean serialized;
    private Set<Process> skippedProcesses;

    public RequestBuilderImpl(Session session, Uri uri, LeafStore store) {
        this(session, uri, store, null, null, null, 0, 0, null, null, null, null, null, false, null);
    }

    public RequestBuilderImpl(Session session, Uri uri, LeafStore store, Headers headers, Auth.Provider authProvider,
                              HttpMethod httpMethod, int timeout, int delay, String charset,
                              RetryPolicy.Provider retryPolicyProvider, PollingOptions pollingOptions, Payload payload,
                              SerializedPayload serializedPayload, boolean serialized, Set<Process> skippedProcesses) {
        this.session = session;
        if (uri == null) throw new IllegalArgumentException("Uri cannot be null");
        this.uri = uri;
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.store = store;
        this.headers = headers != null ? headers : new Headers();
        this.authProvider = authProvider;
        this.httpMethod = httpMethod;
        this.timeout = timeout;
        this.delay = delay;
        this.charset = charset == null ? Uri.CHARSET : charset;
        this.retryPolicyProvider = retryPolicyProvider;
        this.pollingOptions = pollingOptions != null ? pollingOptions : new PollingOptions();
        this.payload = payload != null ? payload : Payload.EMPTY_PAYLOAD;
        this.serializedPayload = serializedPayload;
        this.serialized = serialized;
        this.skippedProcesses = skippedProcesses != null ? skippedProcesses : Collections.<Process>emptySet();
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
    public String getCharset() {
        return charset;
    }

    @Override
    public Session getSession() {
        return session;
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
    public RetryPolicy getRetryPolicy() {
        if (retryPolicyProvider == null) return null;
        return retryPolicyProvider.getInstance();
    }

    @Override
    public boolean isRetryEnabled() {
        return retryPolicyProvider != null;
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
    public RequestBuilder charset(String charset) {
        this.charset = charset;
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
            return retry(new RetryPolicyImpl(delaysMillis, events));
        }
        return this;
    }

    @Override
    public RequestBuilderImpl skip(Process... processes) {
        if (processes.length != 0) {
            Set<Process> processesSet = new HashSet<Process>(skippedProcesses);
            Collections.addAll(processesSet, processes);
            skippedProcesses = Collections.unmodifiableSet(processesSet);
        }
        return this;
    }

    @Override
    public Set<Process> getSkippedProcesses() {
        return skippedProcesses;
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
            this.authProvider = null;
            return this;
        }
        return auth(new Auth.Provider() {
            public Auth getInstance() {
                return auth;
            }
        });
    }

    @Override
    public RequestBuilderImpl auth(Auth.Provider authProvider) {
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
    public void setContentType(ContentTypeHeader header) {
        headers.add(header);
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
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public void setRetry(int[] delaysMillis, Event... events) {
        retry(delaysMillis, events);
    }

    @Override
    public void setSkippedProcesses(Process... processes) {
        if (processes.length == 0) {
            skippedProcesses = Collections.emptySet();
        } else {
            Set<Process> processesSet = new HashSet<Process>(processes.length);
            Collections.addAll(processesSet, processes);
            skippedProcesses = Collections.unmodifiableSet(processesSet);
        }
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
    // Store methods
    //===================================================================

    @Override
    public <T> T retrieve(String key) {
        return store.retrieve(key);
    }

    @Override
    public RequestBuilderImpl save(String key, Object value, Level level) {
        store.save(key, value, level);
        return this;
    }

    @Override
    public RequestBuilderImpl save(String key, Object value) {
        store.save(key, value);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return store.exists(key);
    }

    @Override
    public boolean isEquals(String key, Object value) {
        return store.isEquals(key, value);
    }

    @Override
    public boolean remove(String key) {
        return store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

//===================================================================
    // MutableSerializedRequest
    //===================================================================

    @Override
    public RequestBuilderImpl copy() {
        return new RequestBuilderImpl(
                session,
                Uri.copy(uri),
                LeafStore.copy(store),
                Headers.copy(headers),
                authProvider,
                httpMethod,
                timeout,
                delay,
                charset,
                retryPolicyProvider,
                PollingOptions.copy(pollingOptions),
                payload,
                serializedPayload,
                serialized,
                skippedProcesses
        );
    }

    @Override
    public RequestBuilderImpl replicate() {
        return new RequestBuilderImpl(
                session,
                Uri.copy(uri),
                store, // keep store reference
                Headers.copy(headers),
                authProvider,
                httpMethod,
                timeout,
                delay,
                charset,
                retryPolicyProvider,
                pollingOptions, // keep pollingOptions reference
                payload,
                serializedPayload,
                serialized,
                skippedProcesses
        );
    }

    //===================================================================
    // Own methods
    //===================================================================

    protected RequestBuilderImpl build() {
        return copy();
    }
}
