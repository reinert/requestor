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

import com.google.gwt.core.client.Callback;

import io.reinert.requestor.PreparedRequest;
import io.reinert.requestor.RequestException;
import io.reinert.requestor.oauth2.AuthRequest;
import io.reinert.requestor.oauth2.TokenInfo;

/**
 * OAuth2 authentication.
 *
 * @author Danilo Reinert
 */
public abstract class OAuth2Base implements Auth {

    private static final io.reinert.requestor.oauth2.Auth AUTH = io.reinert.requestor.oauth2.Auth.get();

    private final AuthRequest authRequest;

    public OAuth2Base(String authUrl, String clientId, String... scopes) {
        this.authRequest = new AuthRequest(authUrl, clientId).withScopes(scopes);
    }

    /**
     * Use token to perform the request authorization.
     * Implementers must not call preparedRequest#send().
     *
     * @param preparedRequest  request to be authorized
     * @param tokenInfo     token information retrieved from OAuth login
     */
    protected abstract void doAuth(PreparedRequest preparedRequest, TokenInfo tokenInfo);

    public OAuth2Base withScopeDelimiter(String delimiter) {
        authRequest.withScopeDelimiter(delimiter);
        return this;
    }

    @Override
    public void auth(final PreparedRequest preparedRequest) {
        AUTH.login(authRequest, new Callback<TokenInfo, Throwable>() {
            @Override
            public void onFailure(Throwable reason) {
                preparedRequest.abort(new RequestException("Unable to authorize the request using OAuth2.", reason));
            }

            @Override
            public void onSuccess(TokenInfo tokenInfo) {
                doAuth(preparedRequest, tokenInfo);
                preparedRequest.send();
            }
        });
    }
}
