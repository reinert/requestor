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

import io.reinert.requestor.header.Header;

/**
 * Allows one to modify HTTP Response Headers.
 *
 * @author Danilo Reinert
 */
public interface ResponseFilterContext {

    /**
     * Adds (or replaces) a headerName to this response.
     *
     * @param headerName the headerName to add
     */
    void putHeader(Header headerName);

    /**
     * Set a header to this response.
     *
     * @param name the header name
     * @param value the header value
     */
    void setHeader(String name, String value);

    /**
     * Returns the value of the requested headerName or null if the headerName was not specified.
     *
     * @param headerName the headerName to query for
     * @return the value of response headerName
     */
    String getHeader(String headerName);

    Header popHeader(String headerName);

    /**
     * Returns the value of the content-type header.
     *
     * @return the value of response header
     */
    String getContentType();

    /**
     * Returns the links attached to the response as headers.
     *
     * @return links as an {@link Iterable}; does not return {@code null}
     */
    Iterable<Link> getLinks();

    /**
     * Check if link for relation exists.
     *
     * @param relation link relation
     *
     * @return {@code true} if the link for the relation is present in the {@link #getHeaders() response headers},
     *         {@code false} otherwise.
     */
    boolean hasLink(String relation);

    /**
     * Returns the link for the relation.
     *
     * @param relation link relation
     *
     * @return the link for the relation, otherwise {@code null} if not present
     */
    Link getLink(String relation);

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
     * Returns the HTTP status as a {@link HttpStatus} object.
     *
     * @return the HTTP status
     */
    HttpStatus getStatus();

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

    /**
     * Returns the store of this request/response cycle.
     *
     * @return the store
     */
    Store getStore();

}
