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
package io.reinert.requestor;

import java.io.OutputStream;

import io.reinert.requestor.auth.Auth;
import io.reinert.requestor.header.Header;
import io.reinert.requestor.uri.Uri;

public abstract class AbstractProcessingRequest implements ProcessingRequest {

    protected final ProcessingRequest request;

    public AbstractProcessingRequest(ProcessingRequest request) {
        this.request = request;
    }

    protected abstract void process();

    @Override
    public void setContentType(String mediaType) {
        request.setContentType(mediaType);
    }

    @Override
    public void setAccept(String mediaType) {
        request.setAccept(mediaType);
    }

    @Override
    public void addHeader(Header header) {
        request.addHeader(header);
    }

    @Override
    public void setHeader(String header, String value) {
        request.setHeader(header, value);
    }

    @Override
    public void removeHeader(String name) {
        request.removeHeader(name);
    }

    @Override
    public void setAuth(Auth auth) {
        request.setAuth(auth);
    }

    @Override
    public void setTimeout(int timeoutMillis) {
        request.setTimeout(timeoutMillis);
    }

    @Override
    public void setPayload(Object payload) {
        request.setPayload(payload);
    }

    @Override
    public void setMethod(HttpMethod httpMethod) {
        request.setMethod(httpMethod);
    }

    @Override
    public String getAccept() {
        return request.getAccept();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public Headers getHeaders() {
        return request.getHeaders();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public Object getPayload() {
        return request.getPayload();
    }

    @Override
    public int getTimeout() {
        return request.getTimeout();
    }

    @Override
    public Uri getUri() {
        return request.getUri();
    }

    @Override
    public Auth getAuth() {
        return request.getAuth();
    }

    @Override
    public OutputStream getOutputStream() {
        return request.getOutputStream();
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        request.setOutputStream(outputStream);
    }

    @Override
    public final void proceed() {
        process();
        request.proceed();
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