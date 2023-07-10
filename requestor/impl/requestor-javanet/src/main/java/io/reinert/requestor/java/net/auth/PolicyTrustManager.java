package io.reinert.requestor.java.net.auth;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reinert.requestor.java.net.ssl.TrustPolicy;

/**
 * Policy based {@link TrustManager}.
 *
 * @author Danilo Reinert
 */
class PolicyTrustManager implements X509TrustManager {

    private final X509TrustManager trustManager;
    private final TrustPolicy trustPolicy;

    PolicyTrustManager(TrustManager trustManager, TrustPolicy trustPolicy) {
        this.trustManager = (X509TrustManager) trustManager;
        this.trustPolicy = trustPolicy;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        this.trustManager.checkClientTrusted(chain, authType);
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (!this.trustPolicy.isTrusted(chain, authType)) {
            this.trustManager.checkServerTrusted(chain, authType);
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return this.trustManager.getAcceptedIssuers();
    }
}
