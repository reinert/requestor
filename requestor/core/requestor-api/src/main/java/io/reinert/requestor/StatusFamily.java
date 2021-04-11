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

/**
 * An enumeration representing the class of status code.
 */
public enum StatusFamily {

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
    public static StatusFamily of(final int statusCode) {
        switch (statusCode / 100) {
            case 1:
                return StatusFamily.INFORMATIONAL;
            case 2:
                return StatusFamily.SUCCESSFUL;
            case 3:
                return StatusFamily.REDIRECTION;
            case 4:
                return StatusFamily.CLIENT_ERROR;
            case 5:
                return StatusFamily.SERVER_ERROR;
            default:
                return StatusFamily.OTHER;
        }
    }
}
