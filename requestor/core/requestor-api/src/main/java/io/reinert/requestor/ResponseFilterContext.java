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
 * Allows one to modify HTTP Response Headers.
 *
 * @author Danilo Reinert
 */
public interface ResponseFilterContext {

    /**
     * Adds (or replaces) a header to this response.
     *
     * @param header the header to add
     */
    void addHeader(Header header);

    /**
     * Set a header to this response.
     *
     * @param name the header name
     * @param value the header value
     */
    void setHeader(String name, String value);

    /**
     * Returns the value of the requested header or null if the header was not specified.
     *
     * @param header the header to query for
     * @return the value of response header
     */
    String getHeader(String header);

    /**
     * Returns the value of the content-type header.
     *
     * @return the value of response header
     */
    String getContentType();

    /**
     * Returns the HTTP headers associated with this response.
     *
     * @return the Headers
     */
    Headers getHeaders();

    /**
     * Returns the HTTP status code that is part of this response.
     *
     * @return the HTTP status code
     */
    int getStatusCode();

    /**
     * Returns the HTTP status message text.
     *
     * @return the HTTP status message text
     */
    String getStatusText();

    /**
     * Returns the raw payload.
     *
     * @return the response payload
     */
    Payload getPayload();

    /**
     * Returns the response type.
     *
     * @return the response type
     */
    ResponseType getResponseType();

}
