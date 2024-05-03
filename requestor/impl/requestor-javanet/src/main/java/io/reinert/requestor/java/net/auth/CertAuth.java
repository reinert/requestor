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
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.reinert.requestor.core.AuthException;
import io.reinert.requestor.java.net.ssl.TrustPolicy;

/**
 * Provides certificate-based authentication by configuring an SSLContext
 * with the specified certificate and password. This class supports both
 * certificate files and certificate data provided via InputStreams.
 * Optionally, a custom TrustPolicy can be applied to manage how trust
 * is handled within SSL negotiations.
 *
 * @author Danilo Reinert
 */
public class CertAuth extends SslAuth {

    public static String PROTOCOL = "TLSv1.2";

    /**
     * Constructs a CertAuth instance using a certificate file path and password.
     *
     * @param certPath the file system path to the certificate file
     * @param password the password for the certificate's key store
     */
    public CertAuth(String certPath, String password) {
        super(getSslContext(certPath, password, null));
    }

    /**
     * Constructs a CertAuth instance using a certificate InputStream and password.
     *
     * @param certInputStream the InputStream containing the certificate
     * @param password the password for the certificate's key store
     */
    public CertAuth(InputStream certInputStream, String password) {
        super(getSslContext(certInputStream, password, null));
    }

    /**
     * Constructs a CertAuth instance with an additional TrustPolicy using a certificate file path and password.
     *
     * @param certPath the file system path to the certificate file
     * @param password the password for the certificate's key store
     * @param trustPolicy the custom policy to manage trust during SSL negotiations
     */
    public CertAuth(String certPath, String password, TrustPolicy trustPolicy) {
        super(getSslContext(certPath, password, trustPolicy));
    }

    /**
     * Constructs a CertAuth instance with an additional TrustPolicy using a certificate InputStream and password.
     *
     * @param certInputStream the InputStream containing the certificate
     * @param password the password for the certificate's key store
     * @param trustPolicy the custom policy to manage trust during SSL negotiations
     */
    public CertAuth(InputStream certInputStream, String password, TrustPolicy trustPolicy) {
        super(getSslContext(certInputStream, password, trustPolicy));
    }

    /**
     * Creates an SSLContext from a certificate path, password, and an optional TrustPolicy.
     * Throws AuthException if the certificate could not be loaded or the SSLContext could not be initialized.
     *
     * @param certPath the file system path to the certificate file
     * @param password the password for the certificate's key store
     * @param trustPolicy the custom policy to manage trust during SSL negotiations, or null for default behavior
     * @return SSLContext configured with the certificate and trust managers
     */
    private static SSLContext getSslContext(String certPath, String password, TrustPolicy trustPolicy) {
        try {
            return getSslContext(Files.newInputStream(Paths.get(certPath)), password, trustPolicy);
        } catch (IOException e) {
            throw new AuthException("Could not load certificate", e);
        }
    }

    /**
     * Creates an SSLContext from a certificate InputStream, password, and an optional TrustPolicy.
     * Throws AuthException if the certificate could not be loaded or the SSLContext could not be initialized.
     *
     * @param certInputStream the InputStream containing the certificate
     * @param password the password for the certificate's key store
     * @param trustPolicy the custom policy to manage trust during SSL negotiations, or null for default behavior
     * @return SSLContext configured with the certificate and trust managers
     */
    private static SSLContext getSslContext(InputStream certInputStream, String password, TrustPolicy trustPolicy) {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(certInputStream, password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            if (trustPolicy != null) {
                trustManagers = Arrays.stream(trustManagers)
                    .map(tm -> new PolicyTrustManager(tm, trustPolicy))
                    .toArray(TrustManager[]::new);
            }

            SSLContext context = SSLContext.getInstance(PROTOCOL);
            context.init(kmf.getKeyManagers(), trustManagers, new SecureRandom());

            return context;
        } catch (Exception e) {
            throw new AuthException("Could not load certificate", e);
        }
    }
}
