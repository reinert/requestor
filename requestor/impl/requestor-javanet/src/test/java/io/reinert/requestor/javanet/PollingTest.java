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
package io.reinert.requestor.javanet;

import io.reinert.requestor.core.PollingStrategy;
import io.reinert.requestor.core.Session;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the polling feature.
 */
public class PollingTest extends NetTest {

    private static final int TIMEOUT = 5000;

    @Test(timeout = TIMEOUT)
    public void testStopShortPolling() throws Throwable {
        final Thread thread = Thread.currentThread();
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.req("https://httpbin.org/get")
                .poll(PollingStrategy.SHORT, 500)
                .get()
                .onSuccess(assay(result, (none, res, req) -> Assert.assertTrue(req.getPollingCount() <= 3)))
                .onSuccess((none, res, req) -> {
                    if (req.getPollingCount() == 3) {
                        req.stopPolling();
                        result.success();
                        thread.interrupt();
                    }
                });

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testStopLongPolling() throws Throwable {
        final Thread thread = Thread.currentThread();
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.req("https://httpbin.org/get")
                .poll(PollingStrategy.LONG, 500)
                .get()
                .onSuccess(assay(result, (none, res, req) -> Assert.assertTrue(req.getPollingCount() <= 3)))
                .onSuccess((none, res, req) -> {
                    if (req.getPollingCount() == 3) {
                        req.stopPolling();
                        result.success();
                        thread.interrupt();
                    }
                });

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testShortPollingLimit() throws Throwable {
        final Thread thread = Thread.currentThread();
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.req("https://httpbin.org/get")
                .poll(PollingStrategy.SHORT, 500, 3)
                .get()
                .onSuccess(assay(result, (none, res, req) -> Assert.assertTrue(req.getPollingCount() <= 3)))
                .onSuccess((none, res, req) -> {
                    if (req.getPollingCount() == 3) {
                        result.success();
                        thread.interrupt();
                    }
                });

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testLongPollingLimit() throws Throwable {
        final Thread thread = Thread.currentThread();
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.req("https://httpbin.org/get")
                .poll(PollingStrategy.SHORT, 0, 3)
                .get()
                .onSuccess(assay(result, (none, res, req) -> Assert.assertTrue(req.getPollingCount() <= 3)))
                .onSuccess((none, res, req) -> {
                    if (req.getPollingCount() == 3) {
                        result.success();
                        thread.interrupt();
                    }
                });

        finishTest(result, TIMEOUT);
    }
}
