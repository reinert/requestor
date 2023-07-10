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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.reinert.requestor.core.AuthException;


/**
 * Certificate authentication..
 *
 * @author Danilo Reinert
 */
public class CertAuth extends SslAuth {

    public CertAuth(String certPath, String password) {
        super(getSslContext(certPath, password));
    }

    public CertAuth(InputStream certInputStream, String password) {
        super(getSslContext(certInputStream, password));
    }

    private static SSLContext getSslContext(String certPath, String password) {
        try {
            return getSslContext(Files.newInputStream(Paths.get(certPath)), password);
        } catch (IOException e) {
            throw new AuthException("Could not load certificate.", e);
        }
    }

    private static SSLContext getSslContext(InputStream certInputStream, String password) {
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(certInputStream, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, password.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            return context;
        } catch (Exception e) {
            throw new AuthException("Could not load certificate.", e);
        }
    }
}
