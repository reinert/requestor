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

    SUCCESS("success"),
    FAIL("fail"),
    LOAD("load"),
    TIMEOUT("timeout"),
    CANCEL("cancel"),
    ABORT("abort"),
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
