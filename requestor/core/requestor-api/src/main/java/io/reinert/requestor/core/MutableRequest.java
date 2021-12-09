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

import io.reinert.requestor.core.uri.Uri;

/**
 * A request with setters.
 *
 * @author Danilo Reinert
 */
public interface MutableRequest extends RequestOptions, HasHeaders, HasPollingOptions {

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

    /**
     * Sets the necessary information for authenticating the request against the server.
     *
     * @param authProvider The authentication provider
     */
    void setAuth(Auth.Provider authProvider);

    /**
     * Sets the number of milliseconds to wait for a request to complete.
     * <p></p>
     *
     * Should the request timeout, registered RejectedCallbacks will be called in the returning Request.
     * The callback method will receive an instance of the {@link RequestTimeoutException} class as its
     * {@link Throwable} argument.
     * <p></p>
     *
     * @param timeoutMillis Number of milliseconds to wait before canceling the
     *                      request, a value of zero disables timeouts
     */
    void setTimeout(int timeoutMillis);

    /**
     * Set a retry policy for this request.
     * <p></p>
     *
     * @param delaysMillis  The times in milliseconds to wait before each consecutive retry
     * @param events        The events that will trigger a retry
     */
    void setRetry(int[] delaysMillis, Event... events);

    /**
     * The poll counter is incremented by 1.
     *
     * @return the updated polling count.
     */
    int incrementPollingCount();

    /**
     * Input a payload to be serialized and then sent in the HTTP request body.
     *
     * @param payload The payload of the request
     * @param fields  The fields to be serialized
     */
    void setPayload(Object payload, String... fields);

    /**
     * Sets the HTTP method of the request.
     *
     * @param httpMethod The HTTP method of the request
     */
    void setMethod(HttpMethod httpMethod);

}
