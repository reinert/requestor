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

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.callback.PayloadCallback;
import io.reinert.requestor.payload.SerializedPayload;

/**
 * Integration tests for deserializing special types like Headers, RawResponse, SerializedResponse and Response.
 */
public class SpecialTypeResponsesGwtTest extends GWTTestCase {

    private Session session;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        session = new GwtSession();
    }

    public void testResponseTypeAsHeaders() {
        session.req("http://httpbin.org/get")
                .get(Headers.class)
                .success(new PayloadCallback<Headers>() {
                    public void execute(Headers result) {
                        assertNotNull(result);
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsPayload() {
        session.req("http://httpbin.org/get")
                .get(SerializedPayload.class)
                .success(new PayloadCallback<SerializedPayload>() {
                    public void execute(SerializedPayload result) {
                        assertNotNull(result);
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsRawResponse() {
        session.req("http://httpbin.org/get")
                .get(RawResponse.class)
                .success(new PayloadCallback<RawResponse>() {
                    public void execute(RawResponse result) {
                        assertNotNull(result);
                        assertFalse(result.getSerializedPayload().isEmpty());
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsSerializedResponse() {
        session.req("http://httpbin.org/get")
                .get(SerializedResponse.class)
                .success(new PayloadCallback<SerializedResponse>() {
                    public void execute(SerializedResponse result) {
                        assertNotNull(result);
                        assertFalse(result.getSerializedPayload().isEmpty());
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }

    public void testResponseTypeAsResponse() {
        session.req("http://httpbin.org/get")
                .get(Response.class)
                .success(new PayloadCallback<Response>() {
                    public void execute(Response result) {
                        assertNotNull(result);
                        assertFalse(result.getSerializedPayload().isEmpty());
                        finishTest();
                    }
                });
        delayTestFinish(500);
    }
}
