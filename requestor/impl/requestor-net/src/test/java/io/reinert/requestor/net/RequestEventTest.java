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
package io.reinert.requestor.net;

import io.reinert.requestor.core.RequestFilter;
import io.reinert.requestor.core.Session;

import org.junit.Test;

/**
 * Tests for basic request events.
 */
public class RequestEventTest extends NetTest {

    private static final int TIMEOUT = 3000;

    //=========================================================================
    // LOAD EVENTS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testLoadEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/200")
                .onStatus(200, succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testSuccessEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/200")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testFailEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/400")
                .onSuccess(failOnEvent(result))
                .onFail(succeedOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testStatusEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/200")
                .onStatus(200, succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    //=========================================================================
    // ERROR EVENTS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testAbortEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.register((RequestFilter) request -> {
            throw new RuntimeException("Request should be aborted.");
        });

        session.get("https://httpbin.org/status/200")
                .onLoad(failOnEvent(result))
                .onCancel(failOnError(result))
                .onTimeout(failOnTimeout(result))
                .onAbort(succeedOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testTimeoutEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.setTimeout(50);

        session.get("https://httpstat.us/408?sleep=1000")
                .onLoad(failOnEvent(result))
                .onAbort(failOnError(result))
                .onCancel(failOnError(result))
                .onTimeout(succeedOnTimeout(result));

        finishTest(result, TIMEOUT);
    }
}
