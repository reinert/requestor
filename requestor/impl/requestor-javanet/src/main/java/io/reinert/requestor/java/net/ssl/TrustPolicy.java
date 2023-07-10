package io.reinert.requestor.java.net.ssl;

import java.security.cert.X509Certificate;

/**
 * Defines a policy to trust server certificates in SSL connections.
 *
 * @author Danilo Reinert
 */
public interface TrustPolicy {
    boolean isTrusted(X509Certificate[] chain, String authType);
}
