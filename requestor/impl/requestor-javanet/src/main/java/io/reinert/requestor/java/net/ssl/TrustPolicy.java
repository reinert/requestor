package io.reinert.requestor.java.net.ssl;

import java.security.cert.X509Certificate;

/**
 * Defines a policy to trust server certificates in SSL connections.
 *
 * @author Danilo Reinert
 */
public interface TrustPolicy {

    TrustPolicy TRUST_ALL = (chain, authType) -> true;

    TrustPolicy TRUST_SELF_SIGNED = (chain, authType) -> chain.length == 1;

    boolean isTrusted(X509Certificate[] chain, String authType);
}
