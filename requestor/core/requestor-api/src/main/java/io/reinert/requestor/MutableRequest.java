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

import io.reinert.requestor.uri.Uri;

/**
 * A fluent request builder.
 *
 * @author Danilo Reinert
 */
public interface MutableRequest extends Request, HasHeaders {

    /**
     * Set the URI of this request.
     *
     * @param uri The endpoint of this request
     */
    void setUri(Uri uri);

    /**
     * Set the content type header of this request.
     *
     * @param mediaType The content type of this request
     */
    void setContentType(String mediaType);

    /**
     * Set the accept header for the response.
     *
     * @param mediaType The media type accepted as response
     */
    void setAccept(String mediaType);

    /**
     * Sets the necessary information for authenticating the request against the server.
     *
     * @param auth The authentication procedure
     */
    void setAuth(Auth auth);

    void setAuth(Auth.Provider authProvider);

    /**
     * Sets the number of milliseconds to wait for a request to complete.
     *
     * Should the request timeout, registered RejectedCallbacks will be called in the returning Promise.
     * The callback method will receive an instance of the {@link RequestTimeoutException} class as its
     * {@link Throwable} argument.
     * <p></p>
     *
     * @param timeoutMillis Number of milliseconds to wait before canceling the
     *                      request, a value of zero disables timeouts
     */
    void setTimeout(int timeoutMillis);

    /**
     * Sets the number of milliseconds to wait for a request to be sent.
     *
     * @param delayMillis Number of milliseconds to wait before sending the request
     */
    void setDelay(int delayMillis);

    /**
     * Sets the number of milliseconds to poll the request in a regular time window.
     *
     * @param intervalMillis Number of milliseconds to poll the request
     */
    void setPollInterval(int intervalMillis);

    /**
     * The request will automatically stop polling after being called for the limit number of times.
     *
     * @param pollLimit Maximum number of times the request should be polled
     */
    void setPollLimit(int pollLimit);

    /**
     * The poll counter is incremented by 1.
     *
     * Returns the updated poll counter.
     */
    int incrementPollCounter();

    /**
     * Input a payload to be serialized and then sent in the HTTP request body.
     *
     * @param payload The payload of the request
     */
    void setPayload(Object payload);

    /**
     * Sets the HTTP method of the request.
     *
     * @param httpMethod The HTTP method of the request
     */
    void setMethod(HttpMethod httpMethod);

}
