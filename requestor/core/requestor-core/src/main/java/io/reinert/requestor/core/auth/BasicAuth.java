/*
 * Copyright 2014-2022 Danilo Reinert
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
import io.reinert.requestor.core.AuthException;
import io.reinert.requestor.core.Base64Codec;
import io.reinert.requestor.core.PreparedRequest;

/**
 * HTTP Basic Authentication implementation. <br>
 * Beyond username and password, this class accepts a withCredentials boolean, useful for CORS requests.
 *
 * @author Danilo Reinert
 */
public class BasicAuth implements Auth, Base64Codec.Holder {

    private final String user;
    private final String password;
    private final boolean withCredentials;
    private Base64Codec base64Codec;

    public BasicAuth(String user, String password) {
        this(user, password, false);
    }

    public BasicAuth(String user, String password, boolean withCredentials) {
        if (user == null) throw new IllegalArgumentException("BasicAuth user cannot be null.");
        if (password == null) throw new IllegalArgumentException("BasicAuth password cannot be null.");
        this.user = user;
        this.password = password;
        this.withCredentials = withCredentials;
    }

    @Override
    public void auth(PreparedRequest request) {
        if (base64Codec == null) {
            throw new AuthException("Could not perform Basic authentication. The Base64Codec is not set.");
        }
        request.setHeader("Authorization", "Basic " +
                base64Codec.encode(user + ":" + password, request.getCharset()));
        request.setWithCredentials(withCredentials);
        request.send();
    }

    @Override
    public void setBase64Codec(Base64Codec codec) {
        this.base64Codec = codec;
    }
}
