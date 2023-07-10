/*
 * Copyright 2023 Danilo Reinert
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
package io.reinert.requestor.java.net.auth;

import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import io.reinert.requestor.core.Auth;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.java.net.JavaNetHttpConnection;

/**
 * SSL authentication based on {@link SSLContext}.
 *
 * @author Danilo Reinert
 */
public class SslAuth implements Auth {

    private final SSLContext sslContext;

    public SslAuth(SSLContext sslContext) {
        if (sslContext == null) throw new IllegalArgumentException("sslContext cannot be null.");
        this.sslContext = sslContext;
    }

    @Override
    public void auth(PreparedRequest request) {
        request.setConnectionPreparer(connection -> {
            JavaNetHttpConnection netConn = (JavaNetHttpConnection) connection;
            HttpURLConnection httpConn = netConn.getHttpUrlConnection();
            if (httpConn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) httpConn).setSSLSocketFactory(this.sslContext.getSocketFactory());
            }
        });

        request.send();
    }
}
