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
package io.reinert.requestor.gwt;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.PollingStrategy;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;

/**
 * Polling tests.
 */
public class PollingGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 8000;

    private Session session;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.gwt.RequestorGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        session = Requestor.newSession();
        session.setMediaType("application/json");
    }

    public void testStopShortPolling() {
        final PollingRequest<Void> request =
                session.req("https://httpbin.org/get")
                        .poll(PollingStrategy.SHORT, 500)
                        .get();

        request.onStatus(200,
                new ResponseCallback() {
                    public void execute(Response response) {
                        // The request can be sent one more time after stopPolling is called in SHORT strategy
                        // depending on the time to receive the responses
                        assertTrue(request.getPollingCount() <= 3);

                        if (request.getPollingCount() == 2) {
                            request.stopPolling();
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testStopLongPolling() {
        final PollingRequest<Void> request =
                session.req("https://httpbin.org/get")
                        .poll(PollingStrategy.LONG, 500)
                        .get();

        request.onStatus(200,
                new ResponseCallback() {
                    public void execute(Response response) {
                        assertTrue(request.getPollingCount() <= 2);

                        if (request.getPollingCount() == 2) {
                            request.stopPolling();
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testShortPollingLimit() {
        session.req("https://httpbin.org/get")
                .poll(PollingStrategy.SHORT, 500, 3)
                .get()
                .onStatus(200, new ResponseRequestCallback<Void>() {
                    public void execute(Response response, PollingRequest<Void> request) {
                        assertTrue(request.getPollingCount() <= 3);

                        if (request.getPollingCount() == 3) {
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testLongPollingLimit() {
        session.req("https://httpbin.org/get")
                .poll(PollingStrategy.LONG, 0, 3)
                .get()
                .onStatus(200, new ResponseRequestCallback<Void>() {
                    public void execute(Response response, PollingRequest<Void> request) {
                        assertTrue(request.getPollingCount() <= 3);

                        if (request.getPollingCount() == 3) {
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }
}
