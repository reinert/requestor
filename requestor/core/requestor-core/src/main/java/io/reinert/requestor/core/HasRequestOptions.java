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

/**
 * Holds request options.
 *
 * @author Danilo Reinert
 */
public interface HasRequestOptions extends HasHeaders {

    /**
     * Resets all defaults (mediaType, auth, timeout and headers).
     */
    void reset();

    /**
     * Sets a default media-type to be applied in all requests as Content-Type and Accept headers.
     *
     * @throws IllegalArgumentException If media-type string is malformed
     *
     * @param mediaType The media-type to be set by default in all requests
     */
    void setMediaType(String mediaType);

    /**
     * Get the default media-type that is being applied in all requests as Content-Type and Accept headers.
     *
     * @return  The default HTTP media-type
     */
    String getMediaType();

    void setAuth(Auth auth);

    void setAuth(Auth.Provider authProvider);

    Auth getAuth();

    Auth.Provider getAuthProvider();

    void setTimeout(int timeoutMillis);

    int getTimeout();

    void setDelay(int delayMillis);

    int getDelay();

    void setCharset(String charset);

    String getCharset();

    void setRetry(int[] delaysMillis, RequestEvent... events);

    void setRetry(RetryPolicy retryPolicy);

    void setRetry(RetryPolicy.Provider retryPolicyProvider);

    RetryPolicy getRetryPolicy();

    RetryPolicy.Provider getRetryPolicyProvider();

    boolean isRetryEnabled();

}
