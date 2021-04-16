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
import io.reinert.requestor.payload.PayloadType;
import io.reinert.requestor.uri.Uri;

public class RequestInAuthProcess<R, S extends MutableSerializedRequest & SerializableRequest>
        implements ProcessableRequest {

    private final S request;
    private final PayloadType responsePayloadType;
    private final RequestDispatcher dispatcher;
    private final Deferred<R> deferred;

    public RequestInAuthProcess(S request, PayloadType responsePayloadType,
                                RequestDispatcher dispatcher, Deferred<R> deferred) {
        this.request = request;
        this.responsePayloadType = responsePayloadType;
        this.dispatcher = dispatcher;
        this.deferred = deferred;
    }

    @Override
    public void process() {
        Auth auth = getAuth();
        auth.auth(new PreparedRequestImpl<R>(dispatcher, this, deferred, responsePayloadType), dispatcher);
    }

    @Override
    public void abort(RawResponse response) {
        dispatcher.evalResponse(this, deferred, response);
    }

    @Override
    public void abort(RequestException error) {
        deferred.reject(error);
    }

    @Override
    public final void proceed() {
        this.process();
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
    public final void putHeader(Header header) {
        request.putHeader(header);
    }

    @Override
    public final void setHeader(String header, String value) {
        request.setHeader(header, value);
    }

    @Override
    public final Header popHeader(String name) {
        return request.popHeader(name);
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
    public void setDelay(int delayMillis) {
        request.setDelay(delayMillis);
    }

    @Override
    public final void setPayload(Object object) {
        request.setPayload(object);
    }

    @Override
    public void setSerializedPayload(Payload payload) {
        request.setSerializedPayload(payload);
    }

    @Override
    public MutableSerializedRequest copy() {
        return request.copy();
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
    public final String getHeader(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public final HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public final Object getPayload() {
        return request.getPayload();
    }

    @Override
    public void serializePayload(Payload payload) {
        request.serializePayload(payload);
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
    public int getDelay() {
        return request.getDelay();
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
    public final Store getStore() {
        return request.getStore();
    }
}
