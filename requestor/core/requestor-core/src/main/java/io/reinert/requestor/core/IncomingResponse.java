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

import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * Represents an HTTP response with no payload yet.
 *
 * @author Danilo Reinert
 */
public interface IncomingResponse extends Store {

    /**
     * Returns the value of the requested header or null if the header was not
     * specified.
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
     * @return the headers
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
     * Return the requested {@link PayloadType} specified in the request invoking.
     *
     * @return the expected successful response Payload Type
     */
    PayloadType getPayloadType();

    /**
     * Returns the request that originated this response.
     *
     * @return  the original request
     */
    RequestOptions getRequestOptions();

    /**
     * Returns the session that this response is bound to.
     *
     * @return  the originating session
     */
    Session getSession();

}
