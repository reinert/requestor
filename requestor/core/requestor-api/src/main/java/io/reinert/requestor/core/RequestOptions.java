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

import java.util.List;

import io.reinert.requestor.core.payload.Payload;
import io.reinert.requestor.core.uri.Uri;

/**
 * Provides the request configuration.
 *
 * @author Danilo Reinert
 */
public interface RequestOptions {

    String getAccept();

    String getContentType();

    Headers getHeaders();

    String getHeader(String name);

    HttpMethod getMethod();

    Payload getPayload();

    int getTimeout();

    int getDelay();

    List<Integer> getRetryDelays();

    List<Event> getRetryEvents();

    boolean isRetryEnabled();

    Uri getUri();

    Auth getAuth();

    String getCharset();

    Session getSession();

}
