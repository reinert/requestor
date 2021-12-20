/*
 * Copyright 2021 Danilo Reinert
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
package io.reinert.requestor.gwt.oauth2;

import com.google.gwt.core.client.Callback;

import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.RequestAbortException;

/**
 * OAuth2 authentication.
 *
 * @author Danilo Reinert
 */
public abstract class OAuth2Base implements io.reinert.requestor.core.Auth {

    private static final Auth AUTH = Auth.get();

    private final AuthRequest authRequest;

    public OAuth2Base(String authUrl, String clientId, String... scopes) {
        this.authRequest = new AuthRequest(authUrl, clientId).withScopes(scopes);
    }

    /**
     * Use token to perform the request authorization.
     * Implementers must call preparedRequest#send() at the end.
     *
     * @param request   request to be authorized
     * @param tokenInfo token information retrieved from OAuth login
     */
    protected abstract void doAuth(PreparedRequest request, TokenInfo tokenInfo);

    public OAuth2Base withScopeDelimiter(String delimiter) {
        authRequest.withScopeDelimiter(delimiter);
        return this;
    }

    public void auth(final PreparedRequest preparedRequest) {
        AUTH.login(authRequest, new Callback<TokenInfo, Throwable>() {
            public void onFailure(Throwable reason) {
                preparedRequest.abort(new RequestAbortException(preparedRequest,
                        "Unable to authorize the request using OAuth2.", reason));
            }

            public void onSuccess(TokenInfo tokenInfo) {
                doAuth(preparedRequest, tokenInfo);
            }
        });
    }
}
