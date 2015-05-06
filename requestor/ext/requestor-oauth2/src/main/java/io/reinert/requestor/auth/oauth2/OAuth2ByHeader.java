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

import io.reinert.requestor.RequestOrder;
import io.reinert.requestor.oauth2.TokenInfo;

/**
 * OAuth2 authentication through the
 * <a hfref="https://tools.ietf.org/html/rfc6750#section-2.1">Authorization Request Header Field</a> method.
 *
 * @author Danilo Reinert
 */
public class OAuth2ByHeader extends OAuth2 {

    public OAuth2ByHeader(String authUrl, String clientId, String... scopes) {
        super(authUrl, clientId, scopes);
    }

    @Override
    protected void doAuth(RequestOrder requestOrder, TokenInfo tokenInfo) {
        requestOrder.setHeader("Authorization", tokenInfo.getTokenType() + ' ' + tokenInfo.getAccessToken());
    }
}
