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

import com.google.gwt.http.client.Header;

import io.reinert.requestor.header.AcceptHeader;

/**
 * This type provides fluent style request building.
 *
 * @author Danilo Reinert
 */
public interface Request {

    /**
     * Set the content type of this request.
     *
     * @param contentType The content type of this request
     *
     * @return the updated Request
     */
    Request contentType(String contentType);

    /**
     * Set the content type accepted for the response.
     *
     * @param contentType The content type accepted for the response
     *
     * @return the updated Request
     */
    Request accept(String contentType);

    /**
     * Set the Accept header of the request.
     *
     * @param acceptHeader The accept header of the request.
     *
     * @return the updated Request
     */
    Request accept(AcceptHeader acceptHeader);

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
    Request header(String header, String value);

    /**
     * Sets a request header. If a header with the specified name has already been set
     * then the new value overwrites the current value.
     *
     * @param header the header instance
     */
    Request header(Header header);

    /**
     * Sets the user name that will be used in the request URL.
     *
     * @param user user name to use
     *
     * @throws IllegalArgumentException if the user is empty
     * @throws NullPointerException if the user is null
     */
    Request user(String user);

    /**
     * Sets the password to use in the request URL. This is ignored if there is no
     * user specified.
     *
     * @param password password to use in the request URL
     *
     * @throws IllegalArgumentException if the password is empty
     * @throws NullPointerException if the password is null
     */
    Request password(String password);

    /**
     * Sets the number of milliseconds to wait for a request to complete. Should
     * the request timeout, the
     * {@link com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, Throwable)}
     * method will be called on the callback instance given to the
     * {@link com.google.gwt.http.client.RequestBuilder#sendRequest(String, com.google.gwt.http.client.RequestCallback)}
     * method. The callback method will receive an instance of the
     * {@link com.google.gwt.http.client.RequestTimeoutException} class as its
     * {@link Throwable} argument.
     *
     * @param timeoutMillis number of milliseconds to wait before canceling the
     *          request, a value of zero disables timeouts
     *
     * @throws IllegalArgumentException if the timeout value is negative
     */
    Request timeout(int timeoutMillis);

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
    Request payload(Object object);

}
