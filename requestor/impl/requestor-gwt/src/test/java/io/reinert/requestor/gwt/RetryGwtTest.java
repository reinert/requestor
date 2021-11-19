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

import java.util.logging.Logger;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.core.DelaySequence;
import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.PollingStrategy;
import io.reinert.requestor.core.RequestEvent;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.StatusFamily;
import io.reinert.requestor.core.callback.ResponseRequestCallback;

/**
 * Retry tests.
 */
public class RetryGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 10000;
    private static final Logger logger = Logger.getLogger(RetryGwtTest.class.getName());

    private Session session;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.gwt.RequestorGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        session = new GwtSession();
        session.setMediaType("application/json");
    }

    public void testRetry() {
        session.req("https://httpbin.org/status/400")
                .retry(DelaySequence.fixed(1, 2), RequestEvent.FAIL)
                .get()
                .onFail(new ResponseRequestCallback<Void>() {
                    public void execute(Response response, PollingRequest<Void> request) {
                        assertEquals(2, request.getRetryCount());
                        finishTest();
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testRetryWithShortPolling() {
        session.req("https://httpbin.org/status/400")
                .poll(PollingStrategy.SHORT, 500, 3)
                .retry(DelaySequence.fixed(1), Status.BAD_REQUEST)
                .get()
                .onFail(new ResponseRequestCallback<Void>() {
                    public void execute(Response response, PollingRequest<Void> request) {
                        assertEquals(1, request.getRetryCount());
                        assertTrue(request.getPollingCount() <= 3);

                        if (request.getPollingCount() == 3) {
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }

    public void testRetryWithLongPolling() {
        session.req("https://httpbin.org/status/400")
                .poll(PollingStrategy.LONG, 0, 2)
                .retry(DelaySequence.fixed(1), StatusFamily.CLIENT_ERROR)
                .get()
                .onFail(new ResponseRequestCallback<Void>() {
                    public void execute(Response response, PollingRequest<Void> request) {
                        assertEquals(1, request.getRetryCount());
                        assertTrue(request.getPollingCount() <= 2);

                        if (request.getPollingCount() == 2) {
                            finishTest();
                        }
                    }
                });

        delayTestFinish(TIMEOUT);
    }
}
