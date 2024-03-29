/*
 * Copyright 2014-2022 Danilo Reinert
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

/**
 * An enumeration representing the class of status code.
 *
 * @author Danilo Reinert
 */
public enum StatusFamily implements RequestEvent {

    /**
     * {@code 1xx} HTTP status codes.
     */
    INFORMATIONAL("1", RequestEvent.FAIL),
    /**
     * {@code 2xx} HTTP status codes.
     */
    SUCCESSFUL("2", RequestEvent.SUCCESS),
    /**
     * {@code 3xx} HTTP status codes.
     */
    REDIRECTION("3", RequestEvent.FAIL),
    /**
     * {@code 4xx} HTTP status codes.
     */
    CLIENT_ERROR("4", RequestEvent.FAIL),
    /**
     * {@code 5xx} HTTP status codes.
     */
    SERVER_ERROR("5", RequestEvent.FAIL),
    /**
     * Other, unrecognized HTTP status codes.
     */
    OTHER("other", RequestEvent.FAIL);

    private final String eventName;
    private final RequestEvent parent;

    StatusFamily(String eventName, RequestEvent parent) {
        this.eventName = eventName;
        this.parent = parent;
    }

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

    @Override
    public String getName() {
        return eventName;
    }

    @Override
    public RequestEvent getParent() {
        return parent;
    }

    @Override
    public boolean includes(RequestEvent event) {
        return RequestEventImpl.includes(this, event);
    }

    @Override
    public boolean is(RequestEvent event) {
        return RequestEventImpl.is(this, event);
    }

    public boolean is(int statusCode) {
        return of(statusCode) == this;
    }
}
