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
 * Abstraction for HTTP Authentication/Authorization methods.
 *
 * @author Danilo Reinert
 */
public interface Auth {

    interface Provider extends io.reinert.requestor.core.Provider<Auth> { }

    /**
     * <p>Performs the logic for making the request authenticated, then dispatch the request.</p>
     *
     *
     * <p>IMPORTANT: You must call request#send() after the auth has finished in order to dispatch the
     * request, otherwise the request will never be sent.</p>
     *
     * <p>The request can be sent only once.</p>
     *
     * @param request  The request about to be sent
     */
    void auth(PreparedRequest request);
}
