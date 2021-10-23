/*
 * Copyright 2014 Danilo Reinert
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

/**
 * A fluent request builder.
 *
 * @author Danilo Reinert
 */
public interface RequestBuilder extends Request {

    /**
     * Set the content type of this request.
     *
     * @param mediaType The content type of this request
     *
     * @return This building request
     */
    RequestBuilder contentType(String mediaType);

    /**
     * Set the content type accepted for the response.
     *
     * @param mediaType The content type accepted for the response
     *
     * @return This building request
     */
    RequestBuilder accept(String mediaType);

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
    RequestBuilder header(String header, String value);

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
    RequestBuilder header(Header header);

    /**
     * Sets the necessary information for authenticating the request against the server.
     *
     * @param auth The authentication procedure
     *
     * @return This building request
     */
    RequestBuilder auth(Auth auth);

    /**
     * Sets the necessary information for authenticating the request against the server.
     *
     * @param authProvider The authentication procedure provider
     *
     * @return This building request
     */
    RequestBuilder auth(Auth.Provider authProvider);

    /**
     * Input a object to be sent in the HTTP Request payload.
     * <p></p>
     *
     * This object will be serialized considering its class and the current content-type.<br>
     * If no serializer was found by matching these two factors, then a exception is thrown.
     *
     * @param payload The payload of the request
     *
     * @return This building request
     */
    RequestBuilder payload(Object payload);

    /**
     * Sets the number of milliseconds to wait for a request to complete.
     *
     * Should the request timeout, registered RejectedCallbacks will be called in the returning Promise.
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
    RequestBuilder timeout(int timeoutMillis);

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
    RequestBuilder delay(int delayMillis);

    /**
     * Poll the request according to the specified interval in milliseconds.
     * <p></p>
     *
     * The poll strategy determines whether the next request should be triggered after sending the
     * previous one (SHORT) or after receiving its response (LONG).
     * <p></p>
     *
     * If delay is also set, then the time window between each request will be poll + delay.
     * <p></p>
     *
     * Negative aren't allowed.
     * So if a value less than zero is passed, it is ignored.
     * <p></p>
     *
     * @param strategy          The polling strategy
     * @param intervalMillis    The time in milliseconds to poll the request
     *
     * @return This building request
     */
    RequestBuilder poll(PollingStrategy strategy, int intervalMillis);

    /**
     * Poll the request dispatching in a regular period of time in milliseconds for a limited number of times.
     * <p></p>
     *
     * The poll strategy determines whether the next request should be triggered after sending the
     * previous one (SHORT) or after receiving its response (LONG).
     * <p></p>
     *
     * The request will automatically stop polling after being called for the limit number of times.
     * <p></p>
     *
     * If delay is also set, then the time window between each request will be pollInterval + delay.
     * <p></p>
     *
     * Negative aren't allowed.
     * So if a value less than zero is passed, it is ignored.
     * <p></p>
     *
     * @param strategy          The polling strategy
     * @param intervalMillis    The time in milliseconds to poll the request
     * @param limit             Maximum number of times the request will be polled
     *
     * @return This building request
     */
    RequestBuilder poll(PollingStrategy strategy, int intervalMillis, int limit);

}
