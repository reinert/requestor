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

import io.reinert.requestor.core.header.Header;

public interface HasHeaders {
    Headers getHeaders();

    String getHeader(String headerName);

    boolean hasHeader(String headerName);

    /**
     * Adds a header overwriting existing headers with the same name.
     *
     * @param header    The header to be inserted
     */
    void setHeader(Header header);

    void setHeader(String headerName, String headerValue);

    /**
     * Removes a header and returns the removed header.
     *
     * @param headerName    The name of the header
     * @return  The removed header instance, if it exists
     */
    Header delHeader(String headerName);
}
