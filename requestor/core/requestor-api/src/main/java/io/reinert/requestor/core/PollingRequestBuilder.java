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

/**
 * A polling request builder.
 *
 * @author Danilo Reinert
 */
public interface PollingRequestBuilder extends RequestBuilder, HasPollingOptions {

    /**
     * Set the content type of this request.
     *
     * @param mediaType The content type of this request
     *
     * @return This building request
     */
    PollingRequestBuilder contentType(String mediaType);

    /**
     * Set the content type accepted for the response.
     *
     * @param mediaType The content type accepted for the response
     *
     * @return This building request
     */
    PollingRequestBuilder accept(String mediaType);

    /**
     * Sets a request header with the given name and value.
     *
     * If a header with the specified name has already been set
     * then the new value overwrites the current value.
     *
     * If a null value is given, then any existing header with
     * the given name is removed.
     *
     * @param header The name of the header
     * @param value  The value of the header
     *
     * @return This building request
     */
    PollingRequestBuilder header(String header, String value);

    /**
     * Sets a request header.
     *
     * If a header with the specified name has already been set
     * then the new value overwrites the current value.
     *
     * @param header The header instance
     *
     * @return This building request
     */
    PollingRequestBuilder header(Header header);

    /**
     * Sets the necessary information for authenticating the request against the server.
     *
     * @param auth The authentication procedure
     *
     * @return This building request
     */
    PollingRequestBuilder auth(Auth auth);

    /**
     * Sets the necessary information for authenticating the request against the server.
     *
     * @param authProvider The authentication procedure provider
     *
     * @return This building request
     */
    PollingRequestBuilder auth(Auth.Provider authProvider);

    /**
     * Input a object to be sent in the HTTP Request payload.
     * <p></p>
     *
     * This object will be serialized considering its class and the current content-type.<br>
     * If no serializer was found by matching these two factors, then a exception is thrown.
     *
     * @param payload The payload of the request
     * @param fields  The fields to be serialized
     *
     * @return This building request
     */
    PollingRequestBuilder payload(Object payload, String... fields);

    /**
     * Sets the number of milliseconds to wait for a request to complete.
     *
     * Should the request timeout, registered RejectedCallbacks will be called in the returning Request.
     * The callback method will receive an instance of the {@link RequestTimeoutException} class as its
     * {@link Throwable} argument.
     * <p></p>
     *
     * Negative aren't allowed according to XMLHttpRequest specification.
     * So if a value less than zero is passed, it is ignored.
     * <p></p>
     *
     * @param timeoutMillis Number of milliseconds to wait before canceling the
     *                      request, a value of zero disables timeouts
     *
     * @return This building request
     */
    PollingRequestBuilder timeout(int timeoutMillis);

    /**
     * Delay the request dispatching in milliseconds.
     * <p></p>
     *
     * Negative aren't allowed.
     * So if a value less than zero is passed, it is ignored.
     * <p></p>
     *
     * @param delayMillis The time in milliseconds to delay the request
     *
     * @return This building request
     */
    PollingRequestBuilder delay(int delayMillis);

    /**
     * Set a retry policy for this request.
     * <p></p>
     *
     * @param delaysMillis  The times in milliseconds to wait before each consecutive retry
     * @param events        The events that will trigger a retry
     *
     * @return This building request
     */
    PollingRequestBuilder retry(int[] delaysMillis, RequestEvent... events);

}
