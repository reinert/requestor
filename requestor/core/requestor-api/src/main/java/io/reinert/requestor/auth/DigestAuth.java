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
        final StringBuilder authBuilder = new StringBuilder("Digest username=\"").append(user);
        final String authHeader = result.getValue("WWW-Authenticate");

        // Read Realm
        int realmStartIdx = authHeader.indexOf("realm=\"") + 7;
        int realmEndIdx = authHeader.indexOf("\"", realmStartIdx);
        final String realm = authHeader.substring(realmStartIdx, realmEndIdx);

        // Append Realm
        authBuilder.append("\", realm=\"").append(realm);

        // Read Nonce
        int nonceStartIdx = authHeader.indexOf("nonce=\"") + 7;
        int nonceEndIdx = authHeader.indexOf("\"", nonceStartIdx);
        final String nonce = authHeader.substring(nonceStartIdx, nonceEndIdx);

        // Append Nonce
        authBuilder.append("\", nonce=\"").append(nonce);

        // Append URI
        authBuilder.append("\", uri=\"").append(request.getUrl());

        // Read QoP
        int qopStartIdx = authHeader.indexOf("qop=\"") + 5;
        final String qop = qopStartIdx != 6 ?
                authHeader.substring(qopStartIdx, authHeader.indexOf("\"", qopStartIdx)) : null;

        // Calculate Nonce Count
        final String nc = NC_FORMAT.format(++NONCE_COUNT);

        // Append Nonce Count
        authBuilder.append("\", nc=\"").append(nc);

        // Generate Client Nonce
        final String cNonce = MD5.hash(nc + nonce + Duration.currentTimeMillis() + getRandom());

        // Append Client Nonce
        authBuilder.append("\", cnonce=\"").append(cNonce);

        // Calculate ha1
        String ha1 = MD5.hash(user + ":" + realm + ":" + password);

        String ha2str = null;
        String response = null;
        if (qop != null) {
            if (qop.contains("auth-int") && !qop.contains("auth")) {
                // "Auth-Int" method
                final String body = request.getPayload() == null ? "" : request.getPayload().isString();
                if (body == null) {
                    // TODO: Try to convert the JavaScriptObject to String before throwing the exception
                    throw new AuthenticationException("Cannot convert a JavaScriptObjectPayload to a string");
                }
                ha2str = request.getMethod().getValue() + ":" + request.getUrl() + ":" + MD5.hash(body);

                // Append QoP
                authBuilder.append("\", qop=\"").append("auth-int");
            } else {
                // "Auth" method
                ha2str = request.getMethod().getValue() + ":" + request.getUrl();

                // Append QoP
                authBuilder.append("\", qop=\"").append("auth");
            }
            final String ha2 = MD5.hash(ha2str);
            // MD5(ha1:nonce:nonceCount:clientNonce:qop:ha2)
            final String respStr = ha1 + ":" + nonce + ":" + nc + ":" + cNonce + ":" + qop + ":" + ha2;
            response = MD5.hash(respStr);
        } else {
            // unspecified method
            ha2str = request.getMethod().getValue() + ":" + request.getUrl();
            final String ha2 = MD5.hash(ha2str);

            // MD5(ha1:nonce:ha2)
            final String respStr = ha1 + ":" + nonce + ":" + ha2;
            response = MD5.hash(respStr);
        }

        // Append Response
        authBuilder.append("\", response=\"").append(response);

        int opaqueStartIdx = authHeader.indexOf("opaque=\"") + 8;
        int opaqueEndIdx = authHeader.indexOf("\"", opaqueStartIdx);
        final String opaque = authHeader.substring(opaqueStartIdx, opaqueEndIdx);

        // Append Opaque
        authBuilder.append("\", opaque=\"").append(opaque).append("\"");

        request.setHeader("Authorization", authBuilder.toString());
        request.setWithCredentials(withCredentials);
        request.send();
    }

    private static int getRandom() {
        return 10 + (int) (Math.random() * ((Integer.MAX_VALUE - 10) + 1));
    }
}
