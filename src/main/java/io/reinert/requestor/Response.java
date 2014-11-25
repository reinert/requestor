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

/**
 * Represents a HTTP response.
 *
 * @author Danilo Reinert
 */
public interface Response {

    int ACCEPTED = 202;
    int BAD_GATEWAY = 502;
    int BAD_REQUEST = 400;
    int CONFLICT = 409;
    int CONTINUE = 100;
    int CREATED = 201;
    int EXPECTATION_FAILED = 417;
    int FORBIDDEN = 403;
    int GATEWAY_TIMEOUT = 504;
    int GONE = 410;
    int HTTP_VERSION_NOT_SUPPORTED = 505;
    int INTERNAL_SERVER_ERROR = 500;
    int LENGTH_REQUIRED = 411;
    int METHOD_NOT_ALLOWED = 405;
    int MOVED_PERMANENTLY = 301;
    int MOVED_TEMPORARILY = 302;
    int MULTIPLE_CHOICES = 300;
    int NO_CONTENT = 204;
    int NON_AUTHORITATIVE_INFORMATION = 203;
    int NOT_ACCEPTABLE = 406;
    int NOT_FOUND = 404;
    int NOT_IMPLEMENTED = 501;
    int NOT_MODIFIED = 304;
    int OK = 200;
    int PARTIAL_CONTENT = 206;
    int PAYMENT_REQUIRED = 402;
    int PRECONDITION_FAILED = 412;
    int PROXY_AUTHENTICATION_REQUIRED = 407;
    int REQUEST_ENTITY_TOO_LARGE = 413;
    int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    int RESET_CONTENT = 205;
    int SEE_OTHER = 303;
    int SERVICE_UNAVAILABLE = 503;
    int SWITCHING_PROTOCOLS = 101;
    int TEMPORARY_REDIRECT = 307;
    int UNAUTHORIZED = 401;
    int UNSUPPORTED_MEDIA_TYPE = 415;
    int USE_PROXY = 305;

    /**
     * Returns the value of the requested header or null if the header was not
     * specified.
     *
     * @param header the header to query for
     * @return the value of response header
     *
     * @throws IllegalArgumentException if the header name is empty
     * @throws NullPointerException if the header name is null
     */
    String getHeader(String header);

    /**
     * Returns the value of the content-type header.
     *
     * @return the value of response header
     */
    String getContentType();

    /**
     * Returns an array of HTTP headers associated with this response.
     *
     * @return array of HTTP headers; returns zero length array if there are no
     *         headers
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
     * Returns the payload deserialized.
     *
     * @return the response payload
     */
    Object getPayload();
}
