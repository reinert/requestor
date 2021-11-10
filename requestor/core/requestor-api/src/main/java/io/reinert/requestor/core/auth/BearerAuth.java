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
package io.reinert.requestor.core.auth;

import io.reinert.requestor.core.Auth;
import io.reinert.requestor.core.PreparedRequest;

/**
 * Bearer Token Authentication implementation. <br>
 * It adds the following header "Authorization: Bearer &lt;token&gt;" to the request. <br>
 * Beyond token, this class accepts a withCredentials boolean, useful for CORS requests.
 *
 * @author Danilo Reinert
 */
public class BearerAuth implements Auth {

    private final String token;
    private final boolean withCredentials;

    public BearerAuth(String token) {
        this(token, false);
    }

    public BearerAuth(String token, boolean withCredentials) {
        this.token = token;
        this.withCredentials = withCredentials;
    }

    @Override
    public void auth(PreparedRequest request) {
        request.setHeader("Authorization", "Bearer " + token);
        request.setWithCredentials(withCredentials);
        request.send();
    }
}
