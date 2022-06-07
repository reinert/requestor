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
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.core.uri.UriBuilder;

/**
 * Base class for {@link Service}.
 *
 * User should extend it and implement its own service calls.
 *
 * @author Danilo Reinert
 */
public class BaseService implements Service {

    private final Session session;
    private final RequestOptionsHolder options;
    private final Store store;
    private final UriBuilder uriBuilder;

    public BaseService(Session session, String resourceUri) {
        this.session = session;
        this.options = RequestOptionsHolder.copy(session.getRequestOptions());
        this.store = new LeafStore(session, true);
        this.uriBuilder = UriBuilder.fromUri(resourceUri);
    }

    //===================================================================
    // RequestOptions methods
    //===================================================================

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
    public void setCharset(String charset) {
        options.setCharset(charset);
    }

    @Override
    public String getCharset() {
        return options.getCharset();
    }

    @Override
    public void setRetry(int[] delaysMillis, RequestEvent... events) {
        options.setRetry(delaysMillis, events);
    }

    @Override
    public void setRetry(RetryPolicy retryPolicy) {
        options.setRetry(retryPolicy);
    }

    @Override
    public void setRetry(RetryPolicy.Provider retryPolicyProvider) {
        options.setRetry(retryPolicyProvider);
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return options.getRetryPolicy();
    }

    @Override
    public RetryPolicy.Provider getRetryPolicyProvider() {
        return options.getRetryPolicyProvider();
    }

    @Override
    public boolean isRetryEnabled() {
        return options.isRetryEnabled();
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
    public boolean hasHeader(String headerName) {
        return options.hasHeader(headerName);
    }

    @Override
    public Header delHeader(String headerName) {
        return options.delHeader(headerName);
    }

    @Override
    public Session getSession() {
        return session;
    }

    //===================================================================
    // Store methods
    //===================================================================

    @Override
    public <T> T retrieve(String key) {
        return store.retrieve(key);
    }

    @Override
    public Service save(String key, Object value) {
        store.save(key, value);
        return this;
    }

    @Override
    public Service save(String key, Object value, Level level) {
        store.save(key, value, level);
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
    // Internal methods
    //===================================================================

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

    protected RequestInvoker req(Uri uri) {
        final RequestInvoker request = session.req(uri);
        options.apply(request);
        return request;
    }
}
