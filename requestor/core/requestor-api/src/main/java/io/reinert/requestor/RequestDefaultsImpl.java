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

class RequestDefaultsImpl implements RequestDefaults {

    private String mediaType;
    private Auth.Provider authProvider;
    private int timeout;
    private int delay;
    private PollingOptions pollingOptions = new PollingOptions();
    private final Headers headers = new Headers();
    private RequestSerializer requestSerializer = new RequestSerializerImpl();
    private ResponseDeserializer responseDeserializer = new ResponseDeserializerImpl();

    static RequestDefaultsImpl copy(RequestDefaultsImpl defaults) {
        RequestDefaultsImpl copy = new RequestDefaultsImpl();
        copy.setMediaType(defaults.mediaType);
        copy.setAuth(defaults.authProvider);
        copy.setTimeout(defaults.timeout);
        copy.setDelay(defaults.delay);
        copy.pollingOptions = PollingOptions.copy(defaults.pollingOptions);
        for (Header h : defaults.headers) copy.putHeader(h);
        copy.setRequestSerializer(defaults.requestSerializer);
        copy.setResponseDeserializer(defaults.responseDeserializer);
        return copy;
    }

    static void validateMediaType(String mediaType) {
        int i = mediaType.indexOf('/');
        if (i == -1 || i != mediaType.lastIndexOf('/')) {
            throw new IllegalArgumentException("Media-type must follow the pattern {type}/{subtype}");
        }
    }

    @Override
    public void reset() {
        mediaType = null;
        authProvider = null;
        timeout = 0;
        delay = 0;
        pollingOptions.reset();
        headers.clear();
    }

    @Override
    public void setMediaType(String mediaType) {
        if (mediaType != null) {
            validateMediaType(mediaType);

            if (headers.containsKey("Content-Type") || headers.containsKey("Accept")) {
                throw new IllegalStateException(
                        "You cannot set mediaType while having a Content-Type or Accept header set." +
                                " Please remove Content-Type and Accept headers before setting a mediaType.");
            }
        }
        this.mediaType = mediaType;
    }

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public void setAuth(final Auth auth) {
        Auth.Provider provider = (auth == null) ? null : new Auth.Provider() {
            @Override
            public Auth getInstance() {
                return auth;
            }
        };
        setAuth(provider);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public Auth getAuth() {
        if (authProvider == null) return null;
        return authProvider.getInstance();
    }

    @Override
    public Auth.Provider getAuthProvider() {
        return authProvider;
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setDelay(int delayMillis) {
        delay = delayMillis;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setPolling(PollingStrategy strategy, int intervalMillis, int limit) {
        pollingOptions.startPolling(strategy, intervalMillis, limit);
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
    public PollingStrategy getPollingStrategy() {
        return pollingOptions.getPollingStrategy();
    }

    @Override
    public void putHeader(Header header) {
        if (header != null && mediaType != null) {
            if ("content-type".equalsIgnoreCase(header.getName()) || "accept".equalsIgnoreCase(header.getName())) {
                throw new IllegalStateException(
                        "You cannot set a Content-Type or Accept header while having a default mediaType set." +
                                " Please make sure to set mediaType to null before adding such headers." +
                                " Currently, mediaType is set to \"" + mediaType + "\".");
            }
        }
        headers.add(header);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        headers.set(headerName, headerValue);
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String headerName) {
        return headers.getValue(headerName);
    }

    @Override
    public Header popHeader(String headerName) {
        return headers.pop(headerName);
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        this.requestSerializer = requestSerializer;
    }

    public RequestSerializer getRequestSerializer() {
        return requestSerializer;
    }

    public void setResponseDeserializer(ResponseDeserializer responseDeserializer) {
        this.responseDeserializer = responseDeserializer;
    }

    public ResponseDeserializer getResponseDeserializer() {
        return responseDeserializer;
    }

    public void apply(RequestBuilder request) {
        if (mediaType != null) {
            request.contentType(mediaType);
            request.accept(mediaType);
        }

        if (authProvider != null) {
            request.auth(authProvider);
        }

        if (timeout > 0) {
            request.timeout(timeout);
        }

        if (delay > 0) {
            request.delay(delay);
        }

        if (pollingOptions.isPolling()) {
            request.poll(pollingOptions.getPollingStrategy(), pollingOptions.getPollingInterval(),
                    pollingOptions.getPollingLimit());
        }

        for (Header h : headers) {
            request.header(h);
        }
    }
}
