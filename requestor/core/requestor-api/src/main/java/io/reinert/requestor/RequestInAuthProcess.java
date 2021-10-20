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

import java.util.logging.Logger;

import io.reinert.requestor.header.Header;
import io.reinert.requestor.payload.SerializedPayload;
import io.reinert.requestor.payload.type.PayloadType;
import io.reinert.requestor.uri.Uri;

public class RequestInAuthProcess<R> implements ProcessableRequest {

    private static final Logger LOGGER = Logger.getLogger(RequestInAuthProcess.class.getName());

    private final MutableSerializedRequest request;
    private final PayloadType responsePayloadType;
    private final RequestDispatcher dispatcher;
    private final Deferred<R> deferred;

    public RequestInAuthProcess(MutableSerializedRequest request, PayloadType responsePayloadType,
                                RequestDispatcher dispatcher, Deferred<R> deferred) {
        this.request = request;
        this.responsePayloadType = responsePayloadType;
        this.dispatcher = dispatcher;
        this.deferred = deferred;
    }

    @Override
    public void process() {
        Auth auth = getAuth();
        auth.auth(new PreparedRequestImpl<R>(dispatcher, this, deferred, responsePayloadType));
    }

    @Override
    public void abort(RawResponse response) {
        dispatcher.evalResponse(response);
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
    public void setThrottleInterval(int intervalMillis) {
        request.setThrottleInterval(intervalMillis);
    }

    @Override
    public void setThrottleLimit(int throttleLimit) {
        request.setThrottleLimit(throttleLimit);
    }

    @Override
    public int incrementThrottleCounter() {
        return request.incrementThrottleCounter();
    }
    @Override
    public final void setPayload(Object payload) {
        request.setPayload(payload);
    }

    @Override
    public void setSerializedPayload(SerializedPayload serializedPayload) {
        request.setSerializedPayload(serializedPayload);
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
    public void serializePayload(SerializedPayload serializedPayload) {
        try {
            ((SerializableRequest) request).serializePayload(serializedPayload);
        } catch (ClassCastException e) {
            LOGGER.warning("Cannot serialize payload. Delegated request is not a SerializableRequest.");
        }
    }

    @Override
    public SerializedPayload getSerializedPayload() {
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
    public int getThrottleInterval() {
        return request.getThrottleInterval();
    }

    @Override
    public int getThrottleLimit() {
        return request.getThrottleLimit();
    }

    @Override
    public int getThrottleCounter() {
        return request.getThrottleCounter();
    }

    @Override
    public void stopThrottle() {
        request.stopThrottle();
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

    public RequestDispatcher getDispatcher() {
        return dispatcher;
    }

    public Deferred<R> getDeferred() {
        return deferred;
    }
}
