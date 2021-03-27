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
import io.reinert.requestor.uri.Uri;

public abstract class AbstractProcessableRequest implements ProcessableRequest {

    protected final ProcessableRequest request;

    public AbstractProcessableRequest(ProcessableRequest request) {
        this.request = request;
    }

    @Override
    public void setUri(Uri uri) {
        request.setUri(uri);
    }

    @Override
    public final void setContentType(String mediaType) {
        request.setContentType(mediaType);
    }

    @Override
    public final void setAccept(String mediaType) {
        request.setAccept(mediaType);
    }

    @Override
    public final void addHeader(Header header) {
        request.addHeader(header);
    }

    @Override
    public final void setHeader(String header, String value) {
        request.setHeader(header, value);
    }

    @Override
    public final void removeHeader(String name) {
        request.removeHeader(name);
    }

    @Override
    public final void setAuth(Auth auth) {
        request.setAuth(auth);
    }

    @Override
    public final void setTimeout(int timeoutMillis) {
        request.setTimeout(timeoutMillis);
    }

    @Override
    public void setPayload(Object payload) {
        request.setPayload(payload);
    }

    @Override
    public void setSerializedPayload(Payload payload) {
        request.setSerializedPayload(payload);
    }

    @Override
    public void serializePayload(Payload payload) {
        request.serializePayload(payload);
    }

    @Override
    public final void setMethod(HttpMethod httpMethod) {
        request.setMethod(httpMethod);
    }

    @Override
    public final String getAccept() {
        return request.getAccept();
    }

    @Override
    public final String getContentType() {
        return request.getContentType();
    }

    @Override
    public final Headers getHeaders() {
        return request.getHeaders();
    }

    @Override
    public final String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public final HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public Object getPayload() {
        return request.getPayload();
    }

    @Override
    public Payload getSerializedPayload() {
        return request.getSerializedPayload();
    }

    @Override
    public final int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public final Uri getUri() {
        return request.getUri();
    }

    @Override
    public final Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public final Storage getStorage() {
        return request.getStorage();
    }

    @Override
    public MutableSerializedRequest copy() {
        return request.copy();
    }

    @Override
    public final void proceed() {
        request.process();
    }

    @Override
    public final void abort(RawResponse response) {
        request.abort(response);
    }

    @Override
    public final void abort(RequestException error) {
        request.abort(error);
    }
}
