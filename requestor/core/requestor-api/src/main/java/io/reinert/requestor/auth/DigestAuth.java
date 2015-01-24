/*
 * Copyright 2015 Danilo Reinert
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

import com.google.gwt.core.client.Duration;
import com.google.gwt.i18n.client.NumberFormat;

import io.reinert.requestor.Headers;
import io.reinert.requestor.RequestOrder;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.SerializedRequestImpl;
import io.reinert.requestor.UnsuccessfulResponseException;
import io.reinert.requestor.deferred.Callback;
import io.reinert.requestor.deferred.Promise;

/**
 * HTTP Digest Authentication implementation. <br/>
 * This class supports MD5 digest authentication based on <a href="http://tools.ietf.org/html/rfc2617">RFC 2617</a>.
 *
 * @author Danilo Reinert
 */
public class DigestAuth implements Authentication {

    private static int NONCE_COUNT;
    private static NumberFormat NC_FORMAT = NumberFormat.getFormat("#00000000");

    private final Requestor requestor;
    private final String user;
    private final String password;
    private final boolean withCredentials;

    public DigestAuth(Requestor requestor, String user, String password) {
        this(requestor, user, password, false);
    }

    public DigestAuth(Requestor requestor, String user, String password, boolean withCredentials) {
        this.requestor = requestor;
        this.user = user;
        this.password = password;
        this.withCredentials = withCredentials;
    }

    @Override
    public void authenticate(final RequestOrder request) {
        final Promise<Void> promise = requestor.dispatch(new SerializedRequestImpl(request.getMethod(),
                request.getUrl(), request.getPayload()), Void.class);

        promise.then(new Callback<Void, Void>() {
            @Override
            public Void call(Void result) {
                request.send();
                return null;
            }
        }, new Callback<Throwable, Void>() {
            @Override
            public Void call(Throwable error) {
                if (error instanceof UnsuccessfulResponseException) {
                    UnsuccessfulResponseException e = (UnsuccessfulResponseException) error;
                    if (e.getStatusCode() == 401) {
                        digest(e.getResponse().getHeaders(), request);
                    }
                }
                throw new AuthenticationException("Some error occurred while trying to authenticate the request.",
                        error);
            }
        });
    }

    private void digest(Headers result, RequestOrder request) {
        final StringBuilder digestBuilder = new StringBuilder("Digest username=\"").append(user);
        final String authHeader = result.getValue("WWW-Authenticate");

        final String realm = readRealm(authHeader);
        final String opaque = readOpaque(authHeader);
        final String nonce = readNonce(authHeader);
        final String qop = readQop(authHeader);

        final String nc = getNextNonceCount();
        final String cNonce = generateClientNonce(nonce, nc);

        digestBuilder.append("\", realm=\"").append(realm);
        digestBuilder.append("\", nonce=\"").append(nonce);
        digestBuilder.append("\", uri=\"").append(request.getUrl());
        digestBuilder.append("\", nc=\"").append(nc);
        digestBuilder.append("\", cnonce=\"").append(cNonce);

        // Calculate HA1
        String ha1 = generateHa1(realm);

        String response;
        if (qop != null) {
            if (qop.contains("auth-int") && !qop.contains("auth")) {
                // "auth-int" method
                response = generateResponseAuthIntMethod(request, ha1, nonce, nc, cNonce);
                digestBuilder.append("\", qop=\"").append("auth-int");
            } else {
                // "auth" method
                response = generateResponseAuthMethod(request, ha1, nonce, nc, cNonce);
                digestBuilder.append("\", qop=\"").append("auth");
            }
        } else {
            // unspecified method
            response = generateResponseUnspecifiedQop(request, nonce, ha1);
        }

        digestBuilder.append("\", response=\"").append(response);
        digestBuilder.append("\", opaque=\"").append(opaque).append("\"");

        request.setHeader("Authorization", digestBuilder.toString());
        request.setWithCredentials(withCredentials);
        request.send();
    }

    private String generateResponseAuthIntMethod(RequestOrder request, String ha1, String nonce, String nc,
                                                 String cNonce) {
        final String body = request.getPayload() == null ? "" : request.getPayload().isString();
        if (body == null) {
            // TODO: Try to convert the JavaScriptObject to String before throwing the exception
            throw new AuthenticationException("Cannot convert a JavaScriptObject payload to a String");
        }
        final String ha2 = MD5.hash(request.getMethod().getValue() + ":" + request.getUrl() + ":" + MD5.hash(body));
        // MD5(ha1:nonce:nonceCount:clientNonce:qop:ha2)
        return MD5.hash(ha1 + ":" + nonce + ":" + nc + ":" + cNonce + ":auth-int:" + ha2);
    }

    private String generateResponseAuthMethod(RequestOrder request, String ha1, String nonce, String nc,
                                                 String cNonce) {
        final String ha2 = MD5.hash(request.getMethod().getValue() + ":" + request.getUrl());
        // MD5(ha1:nonce:nonceCount:clientNonce:qop:ha2)
        return MD5.hash(ha1 + ":" + nonce + ":" + nc + ":" + cNonce + ":auth:" + ha2);
    }

    private String generateResponseUnspecifiedQop(RequestOrder request, String nonce, String ha1) {
        final String ha2 = MD5.hash(request.getMethod().getValue() + ":" + request.getUrl());
        // MD5(ha1:nonce:ha2)
        return MD5.hash(ha1 + ":" + nonce + ":" + ha2);
    }

    private String generateHa1(String realm) {
        return MD5.hash(user + ":" + realm + ":" + password);
    }

    private String getNextNonceCount() {
        return NC_FORMAT.format(++NONCE_COUNT);
    }

    private String generateClientNonce(String nonce, String nc) {
        return MD5.hash(nc + nonce + Duration.currentTimeMillis() + getRandom());
    }

    private String readQop(String authHeader) {
        int qopStartIdx = authHeader.indexOf("qop=\"") + 5;
        return qopStartIdx != 6 ? authHeader.substring(qopStartIdx, authHeader.indexOf("\"", qopStartIdx)) : null;
    }

    private String readNonce(String authHeader) {
        int nonceStartIdx = authHeader.indexOf("nonce=\"") + 7;
        int nonceEndIdx = authHeader.indexOf("\"", nonceStartIdx);
        return authHeader.substring(nonceStartIdx, nonceEndIdx);
    }

    private String readOpaque(String authHeader) {
        int opaqueStartIdx = authHeader.indexOf("opaque=\"") + 8;
        int opaqueEndIdx = authHeader.indexOf("\"", opaqueStartIdx);
        return authHeader.substring(opaqueStartIdx, opaqueEndIdx);
    }

    private String readRealm(String authHeader) {
        int realmStartIdx = authHeader.indexOf("realm=\"") + 7;
        int realmEndIdx = authHeader.indexOf("\"", realmStartIdx);
        return authHeader.substring(realmStartIdx, realmEndIdx);
    }

    private static int getRandom() {
        return 10 + (int) (Math.random() * ((Integer.MAX_VALUE - 10) + 1));
    }
}
