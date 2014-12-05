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
 * This type provides fluent request building.
 *
 * @author Danilo Reinert
 */
public interface RequestBuilder extends Request {

    /**
     * Set the content type of this request.
     *
     * @param mediaType The content type of this request
     *
     * @return the updated Request
     */
    RequestBuilder contentType(String mediaType);

    /**
     * Set the content type accepted for the response.
     *
     * @param mediaType The content type accepted for the response
     *
     * @return the updated Request
     */
    RequestBuilder accept(String mediaType);

    /**
     * Sets a request header with the given name and value. If a header with the
     * specified name has already been set then the new value overwrites the
     * current value.
     *
     * @param header the name of the header
     * @param value the value of the header
     *
     * @throws NullPointerException if header or value are null
     * @throws IllegalArgumentException if header or value are the empty string
     */
    RequestBuilder header(String header, String value);

    /**
     * Sets a request header. If a header with the specified name has already been set
     * then the new value overwrites the current value.
     *
     * @param header the header instance
     */
    RequestBuilder header(Header header);

    /**
     * Sets the user name that will be used in the request URL.
     *
     * @param user user name to use
     *
     * @throws IllegalArgumentException if the user is empty
     * @throws NullPointerException if the user is null
     */
    RequestBuilder user(String user);

    /**
     * Sets the password to use in the request URL. This is ignored if there is no
     * user specified.
     *
     * @param password password to use in the request URL
     *
     * @throws IllegalArgumentException if the password is empty
     * @throws NullPointerException if the password is null
     */
    RequestBuilder password(String password);

    /**
     * Sets the number of milliseconds to wait for a request to complete.
     *
     * Should the request timeout, registered RejectedCallbacks will be called in the returning Promise.
     * The callback method will receive an instance of the {@link RequestTimeoutException} class as its
     * {@link Throwable} argument.
     * <p/>
     *
     * Negative aren't allowed according to XMLHttpRequest specification.
     * So if a value less than zero is passed, it is ignored.
     *
     * @param timeoutMillis number of milliseconds to wait before canceling the
     *          request, a value of zero disables timeouts
     */
    RequestBuilder timeout(int timeoutMillis);

    /**
     * Input a object to be sent in the HTTP Request payload.
     * <p/>
     *
     * This object will be serialized considering its class and the current content-type.<br/>
     * If no serializer was found matching these two factors, the a exception is thrown.
     *
     * @param object the payload of the request
     *
     * @return the updated Request
     */
    /* TODO: return some exception if no serializer is registered for this object and content-type,
       or let the SerdesManager claim? */
    RequestBuilder payload(Object object);

    /**
     * Sets the expected xhr response type of this Request.
     *
     * @param responseType the type of response
     *
     * @return the updated request
     */
    RequestBuilder responseType(ResponseType responseType);

}
