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

import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.uri.Uri;

/**
 * Represents a HTTP Request.
 *
 * @author Danilo Reinert
 */
public interface Request {

    String getAccept();

    String getContentType();

    Headers getHeaders();

    String getHeader(String name);

    HttpMethod getMethod();

    Payload getPayload();

    SerializedPayload getSerializedPayload();

    int getTimeout();

    int getDelay();

    boolean isPolling();

    int getPollingInterval();

    int getPollingLimit();

    int getPollingCounter();

    PollingStrategy getPollingStrategy();

    void stopPolling(); // The request is polled one more time after stopPoll is called

    Uri getUri();

    Auth getAuth();

    Store getStore();

}
