/*
 * Copyright 2015-2021 Danilo Reinert
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import io.reinert.requestor.core.Auth;
import io.reinert.requestor.core.AuthException;
import io.reinert.requestor.core.HttpMethod;
import io.reinert.requestor.core.MutableSerializedRequest;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.RawResponse;
import io.reinert.requestor.core.RequestAbortException;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.callback.DualCallback;
import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.header.SimpleHeader;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.uri.Uri;

/**
 * HTTP Digest Authentication implementation. <br>
 * This class supports digest authentication based on <a href="http://tools.ietf.org/html/rfc2617">RFC 2617</a>.
 * <p></p>
 * You can register {@link HashFunction}s by calling {@link DigestAuth#setHashFunction(String, HashFunction)}.
 *
 * @author Danilo Reinert
 */
public class DigestAuth implements Auth {

    private static final Logger logger = Logger.getLogger(DigestAuth.class.getName());

    public interface HashFunction {
        String hash(String input);
    }

    /**
     * An array containing the codes which are considered expected for the first attempt to retrieve the nonce.
     *
     * The default expected code is unauthorized response 401.
     * 404 is put here because some might prevent the browser from prompting the user for the credentials,
     * which will commonly happen if the server returns a 401 response.
     */
    public static int[] EXPECTED_CODES = new int[]{ 401, 404 };

    /**
     * The default number of max attempts for authenticating using one {@link DigestAuth} instance.
     *
     * It's normally two because the first attempt fails and returns the info for performing the authentication next.
     */
    public static int DEFAULT_MAX_CHALLENGE_CALLS = 2;

    private static Map<String, HashFunction> hashFunctions;

    public static void setHashFunction(String algorithm, HashFunction function) {
        if (algorithm == null) throw new NullPointerException("Algorithm string argument cannot be null.");
        if (function == null) throw new NullPointerException("HashFunction argument cannot be null.");
        if (hashFunctions == null) hashFunctions = new HashMap<String, HashFunction>();
        hashFunctions.put(algorithm.toLowerCase(), function);
    }

    public static HashFunction getHashFunction(String algorithm) {
        if (algorithm == null) throw new NullPointerException("Algorithm string argument cannot be null.");
        if (hashFunctions == null || !hashFunctions.containsKey(algorithm)) {
            throw new UnsupportedOperationException("Algorithm '" + algorithm.toUpperCase() + "' is not supported." +
                    " You can register a HashFunction for it by calling" +
                    " DigestAuth.setHashFunction(String algorithm, HashFunction function).");
        }
        return hashFunctions.get(algorithm);
    }

    public static Provider newProvider(final String user, final String password, final String algorithm) {
        return new Provider() {
            public Auth getInstance() {
                return new DigestAuth(user, password, algorithm);
            }
        };
    }

    public static Provider newProvider(final String user, final String password, final String algorithm,
                                       final boolean withCredentials) {
        return new Provider() {
            public Auth getInstance() {
                return new DigestAuth(user, password, algorithm, withCredentials);
            }
        };
    }

    private final String user;
    private final String password;
    private final String algorithm;
    private final boolean withCredentials;

    private String uriPath;
    private String httpMethod;

    private int maxChallengeCalls = DEFAULT_MAX_CHALLENGE_CALLS;
    private int challengeCalls = 1;
    private int nonceCount;
    private String lastNonce;

    public DigestAuth(String user, String password, String algorithm) {
        this(user, password, algorithm, false);
    }

    public DigestAuth(String user, String password, String algorithm, boolean withCredentials) {
        this.user = user;
        this.password = password;
        this.algorithm = algorithm;
        this.withCredentials = withCredentials;
    }

    public void auth(PreparedRequest request) {
        request.setWithCredentials(withCredentials);
        logger.warning(this.algorithm);
        logger.warning(String.valueOf(hashFunctions.containsKey("md5")));
        attempt(request, null, request.getDispatcher());
    }

    /**
     * Set the max number of attempts to auth.
     * The default is the value of the constant #DEFAULT_MAX_CHALLENGE_CALLS (which is 2 at first).
     *
     * @param maxChallengeCalls  max number of attempt calls
     */
    public void setMaxChallengeCalls(int maxChallengeCalls) {
        this.maxChallengeCalls = maxChallengeCalls;
    }

    private void attempt(PreparedRequest originalRequest, Response attemptResponse, RequestDispatcher dispatcher) {
        try {
            if (challengeCalls < maxChallengeCalls) {
                final MutableSerializedRequest attemptRequest = copyRequest(originalRequest, attemptResponse);

                sendAttemptRequest(originalRequest, attemptRequest, dispatcher);
            } else {
                final Header authHeader = getAuthorizationHeader(originalRequest.getUri(), originalRequest.getMethod(),
                        originalRequest.getSerializedPayload(), attemptResponse);

                if (authHeader != null) {
                    originalRequest.setHeader(authHeader);
                }

                resetChallengeCalls();
                originalRequest.send();
            }

            challengeCalls++;
        } catch (AuthException e) {
            originalRequest.abort(new RequestAbortException(originalRequest,
                    "DigestAuth has failed to authenticate the request", e));
        }
    }

    private void resetChallengeCalls() {
        challengeCalls = 1;
    }

    private MutableSerializedRequest copyRequest(PreparedRequest originalRequest, Response attemptResponse) {
        MutableSerializedRequest request = originalRequest.getMutableCopy();

        final Header authHeader = getAuthorizationHeader(request.getUri(), request.getMethod(),
                request.getSerializedPayload(), attemptResponse);

        if (authHeader != null) {
            request.setHeader(authHeader);
        }

        return request;
    }

    private void sendAttemptRequest(final PreparedRequest originalRequest, MutableSerializedRequest attemptRequest,
                                    final RequestDispatcher dispatcher) {
        dispatcher.dispatch(attemptRequest, true, new DualCallback() {
            public void onError(RequestException error) {
                resetChallengeCalls();
                originalRequest.abort(new RequestAbortException(originalRequest, "Unable to authenticate request" +
                        " using DigestAuth. See previous exception.", error));
            }

            public void onLoad(Response response) {
                if (contains(EXPECTED_CODES, response.getStatusCode())) {
                    // If the error response code is expected, then continue trying to authenticate
                    attempt(originalRequest, response, dispatcher);
                    return;
                }

                resetChallengeCalls();

                if (isSuccessful(response)) {
                    // If the attempt succeeded, then abort the original request with the successful response
                    originalRequest.abort((RawResponse) response);
                    return;
                }

                // Otherwise, throw an AuthException and abort the request with it
                throw new AuthException("Received a non successful status code in an attempt request: "
                        + response.getStatusCode());
            }
        });
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusCode() / 100 == 2;
    }

    private Header getAuthorizationHeader(Uri uri, HttpMethod method, SerializedPayload serializedPayload,
                                          Response attemptResp) {
        if (attemptResp == null) {
            return null;
        }

        final String authHeader = attemptResp.getHeader("WWW-Authenticate");
        if (authHeader == null) {
            resetChallengeCalls();
            throw new AuthException("It was not possible to retrieve the 'WWW-Authenticate' header from "
                    + "server response. If you're using CORS, make sure your server allows the client to access this "
                    + "header by adding \"Access-Control-Expose-Headers: WWW-Authenticate\" to the response headers.");
        }

        final StringBuilder digestBuilder = new StringBuilder("Digest username=\"").append(user);

        if (uriPath == null) {
            uriPath = uri.getPath();
            httpMethod = method.getValue();
        }

        final String realm = readRealm(authHeader);
        final String opaque = readOpaque(authHeader);
        final String nonce = readNonce(authHeader);
        final String[] qop = readQop(authHeader);

        final String nc = getNonceCount(nonce);
        final String cNonce = generateClientNonce(nonce, nc);

        digestBuilder.append("\", realm=\"").append(realm);
        digestBuilder.append("\", nonce=\"").append(nonce);
        digestBuilder.append("\", uri=\"").append(uriPath);

        // Calculate HA1
        String ha1 = generateHa1(realm);

        String response;
        if (contains(qop, "auth")) {
            // "auth" method
            response = generateResponseAuthQop(httpMethod, uriPath, ha1, nonce, nc, cNonce);
            digestBuilder.append("\", qop=\"").append("auth");
            digestBuilder.append("\", nc=\"").append(nc);
            digestBuilder.append("\", cnonce=\"").append(cNonce);
        } else if (contains(qop, "auth-int")) {
            // "auth-int" method
            response = generateResponseAuthIntQop(httpMethod, uriPath, ha1, nonce, nc, cNonce, serializedPayload);
            digestBuilder.append("\", qop=\"").append("auth-int");
            digestBuilder.append("\", nc=\"").append(nc);
            digestBuilder.append("\", cnonce=\"").append(cNonce);
        } else {
            // unspecified method
            response = generateResponseUnspecifiedQop(httpMethod, uriPath, nonce, ha1);
        }

        digestBuilder.append("\", response=\"").append(response);
        digestBuilder.append("\", opaque=\"").append(opaque);
        digestBuilder.append('"');

        return new SimpleHeader("Authorization", digestBuilder.toString());
    }

    private String generateResponseAuthIntQop(String method, String url, String ha1, String nonce, String nc,
                                              String cNonce, SerializedPayload serializedPayload) {
        String body = serializedPayload.toString();
        if (body == null) {
            throw new AuthException("Response body in Digest auth Int Qop method should not be empty.");
        }
        final HashFunction hashFunction = getHashFunction(this.algorithm);
        final String hBody = hashFunction.hash(body);
        final String ha2 = hashFunction.hash(method + ':' + url + ':' + hBody);
        // HASH(ha1:nonce:nonceCount:clientNonce:qop:ha2)
        // TODO: Disable checkstyle rule 'check that a space is left after a colon on an assembled error message'
        return hashFunction.hash(ha1 + ':' + nonce + ':' + nc + ':' + cNonce + ':' + "auth-int" + ':' + ha2);
    }

    private String generateResponseAuthQop(String method, String url, String ha1, String nonce, String nc,
                                           String cNonce) {
        final HashFunction hashFunction = getHashFunction(this.algorithm);
        final String ha2 = hashFunction.hash(method + ':' + url);
        // HASH(ha1:nonce:nonceCount:clientNonce:qop:ha2)
        // TODO: Disable checkstyle rule 'check that a space is left after a colon on an assembled error message'
        return hashFunction.hash(ha1 + ':' + nonce + ':' + nc + ':' + cNonce + ':' + "auth" + ':' + ha2);
    }

    private String generateResponseUnspecifiedQop(String method, String url, String nonce, String ha1) {
        final HashFunction hashFunction = getHashFunction(this.algorithm);
        final String ha2 = hashFunction.hash(method + ':' + url);
        // HASH(ha1:nonce:ha2)
        return hashFunction.hash(ha1 + ':' + nonce + ':' + ha2);
    }

    private String generateHa1(String realm) {
        final HashFunction hashFunction = getHashFunction(this.algorithm);
        return hashFunction.hash(user + ':' + realm + ':' + password);
    }

    private String getNonceCount(String nonce) {
        nonceCount = nonce.equals(lastNonce) ? nonceCount + 1 : 1;
        lastNonce = nonce;
        String nc = String.valueOf(nonceCount);
        logger.warning("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        logger.warning(nc);
        if (nc.length() < 8) nc = new String(new char[8 - nc.length()]).replace('\0', '0') + nc;
        logger.warning(nc);
        return nc;
    }

    private String generateClientNonce(String nonce, String nc) {
        final HashFunction hashFunction = getHashFunction(this.algorithm);
        return hashFunction.hash(nc + nonce + new Date().getTime() + getRandom());
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
}
