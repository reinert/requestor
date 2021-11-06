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

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.callback.ResponseCallback;

/**
 * Integration tests of {@link RequestFilter}.
 */
public class RequestGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 5000;
    private static final Logger logger = Logger.getLogger(RequestGwtTest.class.getName());

    private Session session;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        session = new JsonSession();
        session.setMediaType("application/json");
    }

    public void testStopShortPolling() {
        session.req("https://httpbin.org/get").poll(PollingStrategy.SHORT, 500).get().status(200,
                new ResponseCallback() {
                    public void execute(Response response) {
                        // The request can be sent one more time after stopPolling is called in SHORT strategy
                        // depending on the time to receive the responses
                        assertTrue(response.getRequest().getPollingCounter() <= 3);

                        if (response.getRequest().getPollingCounter() == 2) {
                            response.getRequest().stopPolling();
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testStopLongPolling() {
        session.req("https://httpbin.org/get").poll(PollingStrategy.LONG, 500).get().status(200,
                new ResponseCallback() {
                    public void execute(Response response) {
                        assertTrue(response.getRequest().getPollingCounter() <= 2);

                        if (response.getRequest().getPollingCounter() == 2) {
                            response.getRequest().stopPolling();
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testShortPollingLimit() {
        session.req("https://httpbin.org/get").poll(PollingStrategy.SHORT, 500, 3).get().status(200,
                new ResponseCallback() {
                    public void execute(Response response) {
                        assertTrue(response.getRequest().getPollingCounter() <= 3);

                        if (response.getRequest().getPollingCounter() == 3) {
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testLongPollingLimit() {
        session.req("https://httpbin.org/get").poll(PollingStrategy.LONG, 0, 3).get().status(200,
                new ResponseCallback() {
                    public void execute(Response response) {
                        assertTrue(response.getRequest().getPollingCounter() <= 3);

                        if (response.getRequest().getPollingCounter() == 3) {
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }
}
