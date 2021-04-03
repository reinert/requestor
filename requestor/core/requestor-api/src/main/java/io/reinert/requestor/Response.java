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

import java.util.HashMap;

/**
 * Represents a HTTP response.
 *
 * @param <T> Type of the payload
 *
 * @author Danilo Reinert
 */
public interface Response<T> {

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
     * Returns the payload deserialized.
     *
     * @return the response payload
     */
    T getPayload();

    /**
     * Returns the response type.
     *
     * @return the response type
     */
    ResponseType getResponseType();

    /**
     * Returns the request that originated this response.
     *
     * @return  the original request
     */
    Request getRequest();

    /**
     * Returns the storage of this request/response cycle.
     *
     * @return the storage
     */
    Storage getStorage();

    /**
     * Base interface for statuses used in responses.
     */
    public interface HttpStatus {

        /**
         * Get the associated status code.
         *
         * @return the status code.
         */
        int getStatusCode();

        /**
         * Get the class of status code.
         *
         * @return the class of status code.
         */
        Status.Family getFamily();

        /**
         * Get the reason phrase.
         *
         * @return the reason phrase.
         */
        String getReasonPhrase();

        /**
         * An enumeration representing the class of status code.
         */
        enum Family {

            /**
             * {@code 1xx} HTTP status codes.
             */
            INFORMATIONAL,
            /**
             * {@code 2xx} HTTP status codes.
             */
            SUCCESSFUL,
            /**
             * {@code 3xx} HTTP status codes.
             */
            REDIRECTION,
            /**
             * {@code 4xx} HTTP status codes.
             */
            CLIENT_ERROR,
            /**
             * {@code 5xx} HTTP status codes.
             */
            SERVER_ERROR,
            /**
             * Other, unrecognized HTTP status codes.
             */
            OTHER;

            /**
             * Get the response status family for the status code.
             *
             * @param statusCode response status code to get the family for.
             * @return family of the response status code.
             */
            public static Family of(final int statusCode) {
                switch (statusCode / 100) {
                    case 1:
                        return Family.INFORMATIONAL;
                    case 2:
                        return Family.SUCCESSFUL;
                    case 3:
                        return Family.REDIRECTION;
                    case 4:
                        return Family.CLIENT_ERROR;
                    case 5:
                        return Family.SERVER_ERROR;
                    default:
                        return Family.OTHER;
                }
            }
        }
    }

    /**
     * Commonly used status codes defined by HTTP, see
     * {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10">HTTP/1.1 documentation</a>}
     * for the complete list. Additional status codes can be added by applications
     * by creating an implementation of {@link HttpStatus}.
     */
    public enum Status implements HttpStatus {

        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
        NO_CONTENT(204, "No Content"),
        RESET_CONTENT(205, "Reset Content"),
        PARTIAL_CONTENT(206, "Partial Content"),
        MULTIPLE_CHOICES(300, "Multiple Choices"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        FOUND(302, "Found"),
        SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        USE_PROXY(305, "Use Proxy"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),
        PERMANENT_REDIRECT(308, "Permanent Redirect"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        PAYMENT_REQUIRED(402, "Payment Required"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
        EXPECTATION_FAILED(417, "Expectation Failed"),
        IM_A_TEAPOT(418, "I'm a teapot"),
        UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
        TOO_EARLY(425, "Too Early"),
        UPGRADE_REQUIRED(426, "Upgrade Required"),
        PRECONDITION_REQUIRED(428, "Precondition Required"),
        TOO_MANY_REQUESTS(429, "Too Many Requests"),
        REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
        UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        BAD_GATEWAY(502, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
        VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
        INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
        LOOP_DETECTED(508, "Loop Detected"),
        NOT_EXTENDED(510, "Not Extended"),
        NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

        private static HashMap<String, HttpStatus> statuses = null;

        /**
         * Convert a numerical status code into the corresponding Status.
         *
         * @param statusCode the numerical status code.
         * @return the matching Status or null is no matching Status is defined.
         */
        public static HttpStatus of(final int statusCode) {
            if (statuses == null) {
                statuses = new HashMap<String, HttpStatus>();
                for (Status s : Status.values()) {
                    statuses.put("" + s.code, s);
                }
            }

            HttpStatus status = statuses.get("" + statusCode);
            if (status != null) {
                return status;
            }

            final Family family = Family.of(statusCode);

            return new HttpStatus() {
                public int getStatusCode() {
                    return statusCode;
                }

                public Family getFamily() {
                    return family;
                }

                public String getReasonPhrase() {
                    return "";
                }
            };
        }

        private final int code;
        private final String reason;
        private final Family family;

        private Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            this.family = Family.of(statusCode);
        }

        /**
         * Get the class of status code.
         *
         * @return the class of status code.
         */
        @Override
        public Family getFamily() {
            return family;
        }

        /**
         * Get the associated status code.
         *
         * @return the status code.
         */
        @Override
        public int getStatusCode() {
            return code;
        }

        /**
         * Get the reason phrase.
         *
         * @return the reason phrase.
         */
        @Override
        public String getReasonPhrase() {
            return toString();
        }

        @Override
        public String toString() {
            return "Status{" +
                    "code=" + code +
                    ", reason='" + reason + '\'' +
                    ", family=" + family +
                    '}';
        }
    }
}
