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

import java.io.Serializable;

/**
 * <p>Represents an event that may occur during a {@link Request}.</p>
 *
 * <p>It can be either a {@link Response} or a {@link RequestException}.</p>
 *
 * @author Danilo Reinert
 */
public interface RequestEvent extends Serializable {

    /**
     * Represents a response received.
     */
    RequestEvent LOAD = RequestEventImpl.LOAD;

    /**
     * Represents a 2xx response.
     */
    RequestEvent SUCCESS = RequestEventImpl.SUCCESS;

    /**
     * Represents a non 2xx response.
     */
    RequestEvent FAIL = RequestEventImpl.FAIL;

    /**
     * Represents any request error, combining 'timeout', 'cancel' and 'abort' events.
     */
    RequestEvent ERROR = RequestEventImpl.ERROR;

    /**
     * Represents a request timeout with no response.
     */
    RequestEvent TIMEOUT = RequestEventImpl.TIMEOUT;

    /**
     * Represents a request cancel before receiving a response.
     */
    RequestEvent CANCEL = RequestEventImpl.CANCEL;

    /**
     * Represents a request abort before sending during the processing cycle.
     */
    RequestEvent ABORT = RequestEventImpl.ABORT;

    /**
     * Get event name.
     *
     * @return the event name.
     */
    String getName();

    /**
     * Get the parent event of this event.
     *
     * @return the parent event.
     */
    RequestEvent getParent();

    boolean includes(RequestEvent event);

    boolean is(RequestEvent event);

}
