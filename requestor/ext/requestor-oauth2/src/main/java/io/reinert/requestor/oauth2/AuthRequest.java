/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.reinert.requestor.oauth2;

/**
 * Represents a request for authentication to an OAuth 2.0 provider server.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
class AuthRequest {

    private final String authUrl;
    private final String clientId;
    private String[] scopes;
    private String scopeDelimiter = " ";

    /**
     * @param authUrl  URL of the OAuth 2.0 provider server
     * @param clientId Your application's unique client ID
     */
    public AuthRequest(String authUrl, String clientId) {
        this.authUrl = authUrl;
        this.clientId = clientId;
    }

    /**
     * Set some OAuth 2.0 scopes to request access to.
     */
    public AuthRequest withScopes(String... scopes) {
        this.scopes = scopes;
        return this;
    }

    /**
     * <p> Since some OAuth providers expect multiple scopes to be delimited with spaces (conforming with spec),
     * or spaces, or plus signs, you can set the scope delimiter here that will be used for this AuthRequest. </p>
     *
     * <p> By default, this will be a single space, in conformance with the latest draft of the OAuth 2.0 spec. </p>
     */
    public AuthRequest withScopeDelimiter(String scopeDelimiter) {
        this.scopeDelimiter = scopeDelimiter;
        return this;
    }

    /**
     * Returns a URL representation of this request, appending the client ID and scopes to the original authUrl.
     */
    String toUrl(Auth.UrlCodex urlCodex) {
        return authUrl + (authUrl.contains("?") ? "&" : "?") + "client_id=" + urlCodex.encode(clientId) + "&" +
                "response_type=token&scope=" + scopesToString(urlCodex);
    }

    /**
     * Returns a unique representation of this request for use as a cookie name.
     */
    String asString() {
        // Don't need to URL-encode the scopes since they're just stored here.
        return clientId + "-----" + scopesToString(null);
    }

    /**
     * Returns a comma-delimited list of scopes.
     * <p/>
     * <p>These scopes will be URL-encoded if the given codex is not null.</p>
     */
    private String scopesToString(Auth.UrlCodex urlCodex) {
        if (scopes == null || scopes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean needsSeparator = false;
        for (String scope : scopes) {
            if (needsSeparator) {
                sb.append(scopeDelimiter);
            }
            needsSeparator = true;

            // Use the URL codex to encode each scope, if provided.
            sb.append(urlCodex == null ? scope : urlCodex.encode(scope));
        }
        return sb.toString();
    }

    /**
     * Returns an {@link AuthRequest} represented by the string serialization.
     */
    static AuthRequest fromString(String str) {
        String[] parts = str.split("-----");
        String clientId = parts[0];
        String[] scopes = parts.length == 2 ? parts[1].split(",") : new String[0];
        AuthRequest req = new AuthRequest("", clientId).withScopes(scopes);
        return req;
    }
}
