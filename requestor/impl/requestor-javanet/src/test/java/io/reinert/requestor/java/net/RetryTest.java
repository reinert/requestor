/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.java.net;

import java.util.concurrent.Executors;

import io.reinert.requestor.core.DelaySequence;
import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.PollingStrategy;
import io.reinert.requestor.core.RequestEvent;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.Status;

import org.junit.Assert;
import org.junit.Test;

/**
 * Retry tests.
 */
public class RetryTest extends JavaNetTest {

    private static final int TIMEOUT = 10000;

    @Test(timeout = TIMEOUT)
    public void testRetry() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = Requestor.newSession(Executors.newSingleThreadScheduledExecutor());

        session.req("https://httpbin.org/status/400")
                .retry(DelaySequence.fixed(1, 1, 1), RequestEvent.FAIL)
                .get()
                .onFail(test(result, (Response res, PollingRequest<Void> req) ->
                        Assert.assertEquals(3, req.getRetryCount())));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testRetryWithShortPolling() throws Throwable {
        final Thread thread = Thread.currentThread();
        final TestResult result = new TestResult();

        final Session session = Requestor.newSession(Executors.newSingleThreadScheduledExecutor());

        session.req("https://httpbin.org/status/400")
                .poll(PollingStrategy.SHORT, 500, 2)
                .retry(DelaySequence.fixed(1, 2), Status.BAD_REQUEST)
                .get()
                .onFail(assay(result, (Response res, PollingRequest<Void> req) ->
                        Assert.assertTrue(req.getRetryCount() <= 2)))
                .onFail(assay(result, (Response res, PollingRequest<Void> req) ->
                        Assert.assertTrue(req.getPollingCount() <= 2)))
                .onFail(assay(result, (Response res, PollingRequest<Void> req) -> {
                    if (req.getPollingCount() == 2) {
                        result.success();
                        thread.interrupt();
                    }
                }));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testRetryWithLongPolling() throws Throwable {
        final Thread thread = Thread.currentThread();
        final TestResult result = new TestResult();

        final Session session = Requestor.newSession(Executors.newSingleThreadScheduledExecutor());

        session.req("https://httpbin.org/status/400")
                .poll(PollingStrategy.LONG, 0, 2)
                .retry(DelaySequence.fixed(1, 2), Status.BAD_REQUEST)
                .get()
                .onFail(assay(result, (Response res, PollingRequest<Void> req) ->
                        Assert.assertTrue(req.getRetryCount() <= 2)))
                .onFail(assay(result, (Response res, PollingRequest<Void> req) ->
                        Assert.assertTrue(req.getPollingCount() <= 2)))
                .onFail(assay(result, (Response res, PollingRequest<Void> req) -> {
                    if (req.getPollingCount() == 2) {
                        result.success();
                        thread.interrupt();
                    }
                }));

        finishTest(result, TIMEOUT);
    }
}
