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
    private RequestSerializer requestSerializer = new RequestSerializerImpl();

    static RequestDefaultsImpl copy(RequestDefaultsImpl defaults) {
        RequestDefaultsImpl newDefaults = new RequestDefaultsImpl();
        newDefaults.setMediaType(defaults.getMediaType());
        newDefaults.setAuth(defaults.getAuth());
        newDefaults.setTimeout(defaults.getTimeout());
        for (Header h : defaults.headers) {
            newDefaults.putHeader(h);
        }
        newDefaults.setRequestSerializer(defaults.getRequestSerializer());
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
    public void putHeader(Header header) {
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
    public void setHeader(String headerName, String headerValue) {
        headers.set(headerName, headerValue);
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String headerName) {
        return headers.getValue(headerName);
    }

    @Override
    public Header popHeader(String headerName) {
        return headers.pop(headerName);
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        this.requestSerializer = requestSerializer;
    }

    public RequestSerializer getRequestSerializer() {
        return requestSerializer;
    }

    public void apply(RequestBuilder request) {
        if (mediaType != null) {
            if (request.getContentType() == null)
                request.contentType(mediaType);
            if (request.getAccept() == null)
                request.accept(mediaType);
        }

        if (auth != null && request.getAuth() == null) {
            request.auth(auth);
        }

        if (timeout > 0 && request.getTimeout() == 0) {
            request.timeout(timeout);
        }

        for (Header h : headers) {
            if (request.getHeader(h.getName()) == null)
                request.header(h);
        }
    }
}
