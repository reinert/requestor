/*
 * Copyright 2014-2021 Danilo Reinert
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
 * Request interceptors are intended to manipulate the serialized request payload before it is sent to the server.
 *
 * @author Danilo Reinert
 */
public interface RequestInterceptor {

    interface Provider extends io.reinert.requestor.core.Provider<RequestInterceptor> { }

    /**
     * Intercept method called immediately before a request is sent to a client transport layer.
     *
     * @param request   The request to be sent.
     */
    void intercept(SerializedRequestInProcess request);
}
