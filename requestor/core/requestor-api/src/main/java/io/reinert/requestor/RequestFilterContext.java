/*
 * Copyright 2014 Danilo Reinert
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

import io.reinert.requestor.auth.Authentication;
import io.reinert.requestor.header.Header;

/**
 *  Allows on to modify some properties of an ongoing request.
 *
 *  @author Danilo Reinert
 */
public interface RequestFilterContext {

    String getHeader(String name);

    void setHeader(String name, String value);

    void putHeader(Header header);

    HttpMethod getMethod();

    void setMethod(HttpMethod httpMethod);

    void setAuth(Authentication auth);

    int getTimeout();

    void setTimeout(int timeoutMillis);

    String getUrl();

    Object getPayload();

    ResponseType getResponseType();

    void setResponseType(ResponseType responseType);

}
