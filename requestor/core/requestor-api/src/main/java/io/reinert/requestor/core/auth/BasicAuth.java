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
package io.reinert.requestor.core.auth;

import io.reinert.requestor.core.Auth;
import io.reinert.requestor.core.PreparedRequest;

/**
 * HTTP Basic Authentication implementation. <br>
 * Beyond username and password, this class accepts a withCredentials boolean, useful for CORS requests.
 *
 * @author Danilo Reinert
 */
public class BasicAuth implements Auth {

    public interface Base64 {
        String encode(String text);
    }

    // Requestor implementations must set this
    public static Base64 BASE64 = null;

    private final String user;
    private final String password;
    private final boolean withCredentials;

    public BasicAuth(String user, String password) {
        this(user, password, false);
    }

    public BasicAuth(String user, String password, boolean withCredentials) {
        this.user = user;
        this.password = password;
        this.withCredentials = withCredentials;
    }

    @Override
    public void auth(PreparedRequest request) {
        if (BASE64 == null) {
            throw new UnsupportedOperationException("BasicAuth is not supported because package-private BASE64" +
                    " constant is not set. Please, statically assign this variable in io.requestor.core.auth package.");
        }
        request.setHeader("Authorization", "Basic " + BASE64.encode(user + ":" + password));
        request.setWithCredentials(withCredentials);
        request.send();
    }
}
