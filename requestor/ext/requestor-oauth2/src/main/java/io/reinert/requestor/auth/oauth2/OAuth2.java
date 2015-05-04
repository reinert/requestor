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
package io.reinert.requestor.auth.oauth2;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;

import io.reinert.requestor.RequestException;
import io.reinert.requestor.RequestOrder;
import io.reinert.requestor.auth.Authentication;

/**
 * OAuth2 authentication.
 *
 * @author Danilo Reinert
 */
public abstract class OAuth2 implements Authentication {

    private static final Auth AUTH = Auth.get();

    private final AuthRequest authRequest;

    public OAuth2(String authUrl, String clientId, String... scopes) {
        this.authRequest = new AuthRequest(authUrl, clientId).withScopes(scopes);
    }

    protected abstract void setAccessToken(RequestOrder requestOrder, String accessToken);

    public OAuth2 withScopeDelimiter(String delimiter) {
        authRequest.withScopeDelimiter(delimiter);
        return this;
    }

    @Override
    public void authenticate(final RequestOrder requestOrder) {
        AUTH.login(authRequest, new Callback<String, Throwable>() {
            @Override
            public void onFailure(Throwable reason) {
                // FIXME(reinert): If user closes auth popup, onFailure isn't getting called.
                requestOrder.abort(new RequestException("Unable to authenticate request using OAuth2. "
                        + "See previous log.", reason));
            }

            @Override
            public void onSuccess(String accessToken) {
                setAccessToken(requestOrder, accessToken);
                requestOrder.send();
            }
        });
    }
}
