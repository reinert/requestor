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

import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.core.uri.UriBuilder;

public class AbstractService implements Service {

    private final Session session;
    private final RequestOptions options;
    private final Store store;
    private final UriBuilder uriBuilder;

    public AbstractService(Session session, String resourceUri) {
        this.session = session;
        this.options = RequestOptions.copy(session.getRequestOptions());
        this.store = new TransientStore(session.getStore());
        this.uriBuilder = UriBuilder.fromUri(resourceUri);
    }

    @Override
    public void reset() {
        options.reset();
    }

    @Override
    public void setMediaType(String mediaType) {
        options.setMediaType(mediaType);
    }

    @Override
    public String getMediaType() {
        return options.getMediaType();
    }

    @Override
    public void setAuth(Auth auth) {
        options.setAuth(auth);
    }

    @Override
    public void setAuth(Auth.Provider authProvider) {
        options.setAuth(authProvider);
    }

    @Override
    public Auth getAuth() {
        return options.getAuth();
    }

    @Override
    public Auth.Provider getAuthProvider() {
        return options.getAuthProvider();
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        options.setTimeout(timeoutMillis);
    }

    @Override
    public int getTimeout() {
        return options.getTimeout();
    }

    @Override
    public void setDelay(int delayMillis) {
        options.setDelay(delayMillis);
    }

    @Override
    public int getDelay() {
        return options.getDelay();
    }

    @Override
    public void setPolling(PollingStrategy strategy, int intervalMillis, int limit) {
        options.setPolling(strategy, intervalMillis, limit);
    }

    @Override
    public boolean isPolling() {
        return options.isPolling();
    }

    @Override
    public int getPollingInterval() {
        return options.getPollingInterval();
    }

    @Override
    public int getPollingLimit() {
        return options.getPollingLimit();
    }

    @Override
    public PollingStrategy getPollingStrategy() {
        return options.getPollingStrategy();
    }

    @Override
    public void setHeader(Header header) {
        options.setHeader(header);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        options.setHeader(headerName, headerValue);
    }

    @Override
    public Headers getHeaders() {
        return options.getHeaders();
    }

    @Override
    public String getHeader(String headerName) {
        return options.getHeader(headerName);
    }

    @Override
    public Header delHeader(String headerName) {
        return options.delHeader(headerName);
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Store getStore() {
        return store;
    }

    protected UriBuilder getUriBuilder() {
        return uriBuilder.clone();
    }

    protected void appendMatrixParamsToUri(UriBuilder reqUriBuilder, Object... params) {
        // Check if params were given to the URI
        if (params != null && params.length > 0) {
            if (params.length % 2 > 0) {
                throw new IllegalArgumentException("It should have an even number of arguments, consisting of " +
                        "key and value pairs which will be appended to the request URI");
            }

            for (int i = 0; i < params.length; i = i + 2) {
                reqUriBuilder.matrixParam(params[i].toString(), params[i + 1]);
            }
        }
    }

    protected void appendQueryParamsToUri(UriBuilder reqUriBuilder, Object... params) {
        // Check if params were given to the URI
        if (params != null && params.length > 0) {
            if (params.length % 2 > 0) {
                throw new IllegalArgumentException("It should have an even number of arguments, consisting of " +
                        "key and value pairs which will be appended to the request URI");
            }

            for (int i = 0; i < params.length; i = i + 2) {
                reqUriBuilder.queryParam(params[i].toString(), params[i + 1]);
            }
        }
    }

    protected RequestInvoker request(Uri uri) {
        final RequestInvoker request = session.req(uri);
        options.apply(request);
        return request;
    }
}
