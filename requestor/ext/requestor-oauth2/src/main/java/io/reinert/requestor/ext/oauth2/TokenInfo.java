/*
 * Copyright 2015 Danilo Reinert
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
package io.reinert.requestor.ext.oauth2;

/**
 * Encapsulates information of an access token, its token type and when it will expire.
 */
public class TokenInfo {

    private String tokenType;
    private String accessToken;
    private String expires;

    String asString() {
        return tokenType + "-----" + accessToken + "-----" + (expires == null ? "" : expires);
    }

    static TokenInfo fromString(String val) {
        String[] parts = val.split("-----");
        TokenInfo info = new TokenInfo();
        info.tokenType = parts[0];
        info.accessToken = parts[1];
        info.expires = parts.length > 2 ? parts[2] : null;
        return info;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
