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

import io.reinert.requestor.callback.ResponseCallback;

/**
 * Integration tests of {@link RequestFilter}.
 */
public class RequestGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 5000;

    private Requestor requestor;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        requestor = GWT.create(Requestor.class);
        requestor.setMediaType("application/json");
    }

    public void testStopPolling() {
        requestor.req("https://httpbin.org/get").poll(500).get().status(200, new ResponseCallback() {
            public void execute(Response response) {
                // The request is polled one more time after stopPoll is called
                assertTrue(response.getRequest().getPollCounter() <= 3);

                if (response.getRequest().getPollCounter() == 2) {
                    response.getRequest().stopPoll();
                }

                if (response.getRequest().getPollCounter() == 3) {
                    finishTest();
                }
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testPollLimit() {
        requestor.req("https://httpbin.org/get").poll(500, 2).get().status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertTrue(response.getRequest().getPollCounter() <= 2);
                if (response.getRequest().getPollCounter() == 2) {
                    finishTest();
                }
            }
        });

        delayTestFinish(TIMEOUT);
    }
}
