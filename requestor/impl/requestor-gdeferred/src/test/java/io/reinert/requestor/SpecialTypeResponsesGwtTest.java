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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.impl.gdeferred.RequestDoneCallback;

/**
 * Integration tests for deserializing special types like Headers, RawResponse, SerializedResponse and Response.
 */
public class SpecialTypeResponsesGwtTest extends GWTTestCase {

    private Requestor requestor;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        requestor = GWT.create(Requestor.class);
    }

    public void testResponseTypeAsHeaders() {
        requestor.req("http://httpbin.org/get")
                .get(Headers.class)
                .done(new RequestDoneCallback<Headers>() {
                    public void onDone(Headers result) {
                        assertNotNull(result);
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsPayload() {
        requestor.req("http://httpbin.org/get")
                .get(Payload.class)
                .done(new RequestDoneCallback<Payload>() {
                    public void onDone(Payload result) {
                        assertNotNull(result);
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsRawResponse() {
        requestor.req("http://httpbin.org/get")
                .get(RawResponse.class)
                .done(new RequestDoneCallback<RawResponse>() {
                    public void onDone(RawResponse result) {
                        assertNotNull(result);
                        assertFalse(result.getPayload().isEmpty());
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsSerializedResponse() {
        requestor.req("http://httpbin.org/get")
                .get(SerializedResponse.class)
                .done(new RequestDoneCallback<SerializedResponse>() {
                    public void onDone(SerializedResponse result) {
                        assertNotNull(result);
                        assertFalse(result.getPayload().isEmpty());
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsResponse() {
        requestor.req("http://httpbin.org/get")
                .get(Response.class)
                .done(new RequestDoneCallback<Response>() {
                    public void onDone(Response result) {
                        assertNotNull(result);
                        SerializedResponse response = (SerializedResponse) result;
                        assertFalse(response.getPayload().isEmpty());
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    private static <T> boolean isInstanceOf(Class<T> type, Object object) {
        try {
            T objectAsType = (T) object;
        } catch (ClassCastException exception) {
            return false;
        }
        return true;
    }
}
