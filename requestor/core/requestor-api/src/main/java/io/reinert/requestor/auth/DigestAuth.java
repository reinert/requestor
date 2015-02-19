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

import javax.annotation.Nullable;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.NumberFormat;

import io.reinert.requestor.HttpConnection;
import io.reinert.requestor.Payload;
import io.reinert.requestor.RawResponse;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestOrder;
import io.reinert.requestor.RequestProgress;
import io.reinert.requestor.Response;
import io.reinert.requestor.UnsuccessfulResponseException;
import io.reinert.requestor.deferred.Promise;
import io.reinert.requestor.uri.UriParser;

/**
 * HTTP Digest Authentication implementation. <br/>
 * This class supports MD5 digest authentication based on <a href="http://tools.ietf.org/html/rfc2617">RFC 2617</a>.
 *
 * @author Danilo Reinert
 */
public class DigestAuth implements Authentication {

    /**
     * An array containing the codes which are considered expected for the first attempt to retrieve the nonce.
     *
     * The default expected code is unauthorized response 401.
     * 404 is put here because some might prevent the browser from prompting the user for the credentials,
     * which will commonly happen if the server returns with a 401 code.
     */
    public static int[] EXPECTED_CODES = new int[]{ 401, 404 };

    /**
     * The default number of max attempts for authenticating using one {@link DigestAuth} instance.
     *
     * It's normally two because the first attempt fails and returns the info for performing the authentication next.
     */
    public static int DEFAULT_MAX_CHALLENGE_CALLS = 2;

    private static NumberFormat NC_FORMAT = NumberFormat.getFormat("#00000000");

    private final String user;
    private final String password;
    private final boolean withCredentials;

    private String uri;
    private String httpMethod;

    private int maxChallengeCalls = DEFAULT_MAX_CHALLENGE_CALLS;
    private int challengeCalls = 1;
    private int nonceCount;
    private String lastNonce;

    public DigestAuth(String user, String password) {
        this(user, password, false);
    }

    public DigestAuth(String user, String password, boolean withCredentials) {
        this.user = user;
        this.password = password;
        this.withCredentials = withCredentials;
    }

    @Override
    public void authenticate(final RequestOrder request) {
        request.setWithCredentials(withCredentials);
        attempt(request, null);
    }

    /**
     * Set the max number of attempts to authenticate.
     * The default is 2.
     *
     * @param maxChallengeCalls  max number of attempt calls
     */
    public void setMaxChallengeCalls(int maxChallengeCalls) {
        this.maxChallengeCalls = maxChallengeCalls;
    }

    private void attempt(RequestOrder originalRequest, @Nullable Response attemptResponse) {
        if (challengeCalls < maxChallengeCalls) {
            final RequestOrder attemptRequest = getAttemptRequest(originalRequest);
            applyDigest(attemptRequest, attemptResponse);
            attemptRequest.send();
        } else {
            applyDigest(originalRequest, attemptResponse);
            originalRequest.send();
        }
        challengeCalls++;
    }

    private RequestOrder getAttemptRequest(final RequestOrder originalRequest) {
        return originalRequest.copy(RawResponse.class, new Deferred<RawResponse>() {
            @Override
            public void resolve(Response<RawResponse> response) {
                // If the attempt succeeded, then abort the original request with the successful response
                originalRequest.abort(response.getPayload());
            }

            @Override
            public void reject(RequestException error) {
                try {
                    if (error instanceof UnsuccessfulResponseException) {
                        UnsuccessfulResponseException e = (UnsuccessfulResponseException) error;
                        if (contains(EXPECTED_CODES, e.getStatusCode())) {
                            // If the error response code is expected, then continue trying to authenticate
                            attempt(originalRequest, e.getResponse());
                            return;
                        }
                    }
                    // Otherwise, throw an AuthenticationException and reject the promise with it
                    throw new AuthenticationException("The server returned a not expected status code.", error);
                } catch (Exception e) {
                    originalRequest.abort(new RequestException("Unable to authenticate request using DigestAuth. "
                            + "See previous log.", e));
                }
            }
        });
    }

    private void applyDigest(RequestOrder request, Response attemptResponse) {
        if (attemptResponse == null)
            return;

        final String authHeader = attemptResponse.getHeader("WWW-Authenticate");
        if (authHeader == null) {
            throw new AuthenticationException("It was not possible to retrieve the 'WWW-Authenticate' header from "
                    + "server response. If you're using CORS, make sure your server allows the client to access this "
                    + "header by adding \"Access-Control-Expose-Headers: WWW-Authenticate\" to the response headers.");
        }

        final StringBuilder digestBuilder = new StringBuilder("Digest username=\"").append(user);

        if (uri == null) {
            uri = UriParser.newInstance().parse(request.getUrl()).getUri().getPath();
            httpMethod = request.getMethod().getValue();
        }

        final String realm = readRealm(authHeader);
        final String opaque = readOpaque(authHeader);
        final String nonce = readNonce(authHeader);
        final String[] qop = readQop(authHeader);

        final String nc = getNonceCount(nonce);
        final String cNonce = generateClientNonce(nonce, nc);

        digestBuilder.append("\", realm=\"").append(realm);
        digestBuilder.append("\", nonce=\"").append(nonce);
        digestBuilder.append("\", uri=\"").append(uri);

        // Calculate HA1
        String ha1 = generateHa1(realm);

        String response;
        if (contains(qop, "auth")) {
            // "auth" method
            response = generateResponseAuthQop(httpMethod, uri, ha1, nonce, nc, cNonce);
            digestBuilder.append("\", qop=\"").append("auth");
            digestBuilder.append("\", nc=\"").append(nc);
            digestBuilder.append("\", cnonce=\"").append(cNonce);
        } else if (contains(qop, "auth-int")) {
            // "auth-int" method
            response = generateResponseAuthIntQop(httpMethod, uri, ha1, nonce, nc, cNonce, request.getPayload());
            digestBuilder.append("\", qop=\"").append("auth-int");
            digestBuilder.append("\", nc=\"").append(nc);
            digestBuilder.append("\", cnonce=\"").append(cNonce);
        } else {
            // unspecified method
            response = generateResponseUnspecifiedQop(httpMethod, uri, nonce, ha1);
        }

        digestBuilder.append("\", response=\"").append(response);
        digestBuilder.append("\", opaque=\"").append(opaque);
        digestBuilder.append('"');

        request.setHeader("Authorization", digestBuilder.toString());
    }

    private String generateResponseAuthIntQop(String method, String url, String ha1, String nonce, String nc,
                                              String cNonce, Payload payload) {
        String body = payload == null || payload.isEmpty() ? "" : payload.isString();
        if (body == null) {
            // TODO: Try to convert the JavaScriptObject to String before throwing the exception
            throw new AuthenticationException("Cannot convert a JavaScriptObject payload to a String");
        }
        final String hBody = MD5.hash(body);
        final String ha2 = MD5.hash(method + ':' + url + ':' + hBody);
        // MD5(ha1:nonce:nonceCount:clientNonce:qop:ha2)
        // FIXME: Disable checkstyle rule 'check that a space is left after a colon on an assembled error message'
        return MD5.hash(ha1 + ':' + nonce + ':' + nc + ':' + cNonce + ':' + "auth-int" + ':' + ha2);
    }

    private String generateResponseAuthQop(String method, String url, String ha1, String nonce, String nc,
                                           String cNonce) {
        final String ha2 = MD5.hash(method + ':' + url);
        // MD5(ha1:nonce:nonceCount:clientNonce:qop:ha2)
        // FIXME: Disable checkstyle rule 'check that a space is left after a colon on an assembled error message'
        return MD5.hash(ha1 + ':' + nonce + ':' + nc + ':' + cNonce + ':' + "auth" + ':' + ha2);
    }

    private String generateResponseUnspecifiedQop(String method, String url, String nonce, String ha1) {
        final String ha2 = MD5.hash(method + ':' + url);
        // MD5(ha1:nonce:ha2)
        return MD5.hash(ha1 + ':' + nonce + ':' + ha2);
    }

    private String generateHa1(String realm) {
        final String a1 = user + ':' + realm + ':' + password;
        GWT.log(" A1: " + a1);
        final String ha1 = MD5.hash(a1);
        GWT.log("HA1: " + ha1);
        return ha1;
    }

    private String getNonceCount(String nonce) {
        nonceCount = nonce.equals(lastNonce) ? nonceCount + 1 : 1;
        lastNonce = nonce;
        return NC_FORMAT.format(nonceCount);
    }

    private String generateClientNonce(String nonce, String nc) {
        return MD5.hash(nc + nonce + Duration.currentTimeMillis() + getRandom());
    }

    private String[] readQop(String authHeader) {
        final String qop = extractValue(authHeader, "qop");
        return qop == null ? new String[0] : qop.split(",");
    }

    private String readNonce(String authHeader) {
        return extractValue(authHeader, "nonce");
    }

    private String readOpaque(String authHeader) {
        return extractValue(authHeader, "opaque");
    }

    private String readRealm(String authHeader) {
        return extractValue(authHeader, "realm");
    }

    private static String extractValue(String header, String param) {
        int startIdx = header.indexOf(param + '=') + param.length() + 1;

        if (startIdx == param.length())
            return null;

        int endIdx;
        if (header.charAt(startIdx) == '"') {
            startIdx++;
            endIdx = header.indexOf('"', startIdx);
        } else {
            endIdx = header.indexOf(',', startIdx);
        }
        if (endIdx == -1) endIdx = header.length();
        return header.substring(startIdx, endIdx).trim();
    }

    private static int getRandom() {
        return 10 + (int) (Math.random() * ((Integer.MAX_VALUE - 10) + 1));
    }

    private static boolean contains(String[] array, String value) {
        for (String s: array) {
            if (s.equals(value))
                return true;
        }
        return false;
    }

    private static boolean contains(int[] array, int value) {
        for (int i: array) {
            if (i == value)
                return true;
        }
        return false;
    }

    private abstract static class Deferred<T> implements io.reinert.requestor.deferred.Deferred<T> {
        @Override
        public void notifyDownload(RequestProgress progress) {
        }

        @Override
        public void notifyUpload(RequestProgress progress) {
        }

        @Override
        public void setHttpConnection(HttpConnection connection) {
        }

        @Override
        public Promise<T> getPromise() {
            return null;
        }
    }
}
