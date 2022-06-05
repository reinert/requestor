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

/**
 * An enumeration representing the possible {@link Request} events.
 *
 * @author Danilo Reinert
 */
public enum RequestEvent implements Event {

    /**
     * Represents a 2xx response.
     */
    SUCCESS("success"),
    /**
     * Represents a non 2xx response.
     */
    FAIL("fail"),
    /**
     * Represents a response received.
     */
    LOAD("load"),
    /**
     * Represents a request timeout with no response.
     */
    TIMEOUT("timeout"),
    /**
     * Represents a request cancel before receiving a response.
     */
    CANCEL("cancel"),
    /**
     * Represents a request abort before sending during the processing cycle.
     */
    ABORT("abort"),
    /**
     * Represents any request error, combining 'timeout', 'cancel' and 'abort' events.
     */
    ERROR("error");

    private final String eventName;

    RequestEvent(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String getName() {
        return eventName;
    }
}
