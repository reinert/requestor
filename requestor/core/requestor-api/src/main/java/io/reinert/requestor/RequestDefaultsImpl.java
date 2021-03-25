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
package io.reinert.requestor;

import io.reinert.requestor.auth.Auth;
import io.reinert.requestor.header.Header;

public class RequestDefaultsImpl implements RequestDefaults {

    private String mediaType;
    private Auth auth;
    private int timeout;
    private Headers headers = new Headers();

    static RequestDefaultsImpl copy(RequestDefaultsImpl defaults) {
        RequestDefaultsImpl newDefaults = new RequestDefaultsImpl();
        newDefaults.setMediaType(defaults.getMediaType());
        newDefaults.setAuth(defaults.getAuth());
        newDefaults.setTimeout(defaults.getTimeout());
        for (Header h : defaults.headers) {
            newDefaults.addHeader(h);
        }
        return newDefaults;
    }

    static void validateMediaType(String mediaType) {
        int i = mediaType.indexOf('/');
        if (i == -1 || i != mediaType.lastIndexOf('/')) {
            throw new IllegalArgumentException("Media-type must follow the pattern {type}/{subtype}");
        }
    }

    @Override
    public void reset() {
        this.mediaType = null;
        this.auth = null;
        this.timeout = 0;
        headers.clear();
    }

    @Override
    public void setMediaType(String mediaType) {
        if (mediaType != null) {
            validateMediaType(mediaType);

            if (headers.containsKey("Content-Type") || headers.containsKey("Accept")) {
                throw new IllegalStateException(
                        "You cannot set mediaType while having a Content-Type or Accept header set." +
                                " Please remove Content-Type and Accept headers before setting a mediaType.");
            }
        }
        this.mediaType = mediaType;
    }

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Override
    public Auth getAuth() {
        return auth;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void addHeader(Header header) {
        if (header != null && mediaType != null) {
            if ("content-type".equalsIgnoreCase(header.getName()) || "accept".equalsIgnoreCase(header.getName())) {
                throw new IllegalStateException(
                        "You cannot set a Content-Type or Accept header while having a default mediaType set." +
                                " Please make sure to set mediaType to null before adding such headers." +
                                " Currently, mediaType is set to \"" + mediaType + "\".");
            }
        }
        headers.add(header);
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        headers.set(headerName, headerValue);
    }

    @Override
    public Header getHeader(String headerName) {
        return headers.get(headerName);
    }

    @Override
    public void removeHeader(String headerName) {
        headers.pop(headerName);
    }

    public void apply(RequestBuilder request) {
        if (mediaType != null) {
            request.contentType(mediaType);
            request.accept(mediaType);
        }

        if (auth != null) {
            request.auth(auth);
        }

        if (timeout > 0) {
            request.timeout(timeout);
        }

        for (Header h : headers) {
            request.header(h);
        }
    }
}
