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
package io.reinert.requestor.auth;

import io.reinert.requestor.PreparedRequest;
import io.reinert.requestor.RequestDispatcher;

/**
 * Abstraction for HTTP Authentication/Authorization methods.
 *
 * @author Danilo Reinert
 */
public interface Auth {

    /**
     * Performs the logic for making the request authenticated, then dispatch the request.
     * <p/>
     *
     * IMPORTANT: You must call requestOrder#send() after the auth has finished in order to dispatch the
     * request, otherwise the request will never be sent. <br>
     * The request can be sent only once.
     *
     * @param preparedRequest  The request about to be sent
     */
    void auth(PreparedRequest preparedRequest, RequestDispatcher dispatcher);
}
