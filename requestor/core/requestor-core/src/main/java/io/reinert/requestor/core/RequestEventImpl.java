/*
 * Copyright 2021-2022 Danilo Reinert
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
enum RequestEventImpl implements RequestEvent {

    /**
     * Represents a response received.
     */
    LOAD("load", null),
    /**
     * Represents a 2xx response.
     */
    SUCCESS("success", LOAD),
    /**
     * Represents a non 2xx response.
     */
    FAIL("fail", LOAD),

    /**
     * Represents any request error, combining 'timeout', 'cancel' and 'abort' events.
     */
    ERROR("error", null),
    /**
     * Represents a request timeout with no response.
     */
    TIMEOUT("timeout", ERROR),
    /**
     * Represents a request cancel before receiving a response.
     */
    CANCEL("cancel", ERROR),
    /**
     * Represents a request abort before sending during the processing cycle.
     */
    ABORT("abort", ERROR);

    private final String eventName;
    private final RequestEvent parent;

    RequestEventImpl(String eventName, RequestEvent parent) {
        this.eventName = eventName;
        this.parent = parent;
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
        return includes(this, event);
    }

    @Override
    public boolean is(RequestEvent event) {
        return is(this, event);
    }

    static boolean is(RequestEvent a, RequestEvent b) {
        return a.getName().equals(b.getName());
    }

    static boolean includes(RequestEvent including, RequestEvent included) {
        do {
            if (including.is(included)) return true;
            included = included.getParent();
        } while (included != null);

        return false;
    }
}
