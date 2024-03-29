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

/**
 * Manages request options.
 *
 * @author Danilo Reinert
 */
class RequestOptionsHolder implements HasRequestOptions {

    private volatile String mediaType;
    private volatile Auth.Provider authProvider;
    private volatile int timeout;
    private volatile int delay;
    private volatile String charset;
    private volatile RetryPolicy.Provider retryPolicyProvider;
    private final Headers headers = new Headers(true);

    static RequestOptionsHolder copy(RequestOptionsHolder options) {
        RequestOptionsHolder copy = new RequestOptionsHolder();
        copy.setMediaType(options.mediaType);
        copy.setAuth(options.authProvider);
        copy.setTimeout(options.timeout);
        copy.setDelay(options.delay);
        copy.setCharset(options.charset);
        copy.setRetry(options.retryPolicyProvider);
        for (Header h : options.headers) copy.setHeader(h);
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
        charset = null;
        retryPolicyProvider = null;
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
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public void setRetry(final int[] delaysMillis, final RequestEvent... events) {
        if (delaysMillis != null && delaysMillis.length > 0 && events.length > 0) {
            setRetry(new RetryPolicy.Provider() {
                public RetryPolicy getInstance() {
                    return new RetryPolicyImpl(delaysMillis, events);
                }
            });
        }
    }

    @Override
    public void setRetry(final RetryPolicy retryPolicy) {
        RetryPolicy.Provider provider = (retryPolicy == null) ? null : new RetryPolicy.Provider() {
            public RetryPolicy getInstance() {
                return retryPolicy;
            }
        };
        setRetry(provider);
    }

    @Override
    public void setRetry(RetryPolicy.Provider retryPolicyProvider) {
        this.retryPolicyProvider = retryPolicyProvider;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        if (retryPolicyProvider == null) return null;
        return retryPolicyProvider.getInstance();
    }

    @Override
    public RetryPolicy.Provider getRetryPolicyProvider() {
        return retryPolicyProvider;
    }

    @Override
    public boolean isRetryEnabled() {
        return retryPolicyProvider != null;
    }

    @Override
    public void setHeader(Header header) {
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
    public boolean hasHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    @Override
    public Header delHeader(String headerName) {
        return headers.pop(headerName);
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

        if (charset != null) {
            request.charset(charset);
        }

        if (isRetryEnabled()) {
            request.retry(retryPolicyProvider);
        }

        for (Header h : headers) {
            request.header(h);
        }
    }
}
