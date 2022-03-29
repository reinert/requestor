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

import io.reinert.requestor.core.Session;

import org.junit.Test;

/**
 * Tests for basic request events.
 */
public class HttpMethodTest extends NetTest {

    private static final int TIMEOUT = 5_000;

    //=========================================================================
    // LOAD EVENTS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testGetMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/get")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testPostMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.post("https://httpbin.org/post")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testPutMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.put("https://httpbin.org/put")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testPatchMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.patch("https://httpbin.org/patch")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testDeleteMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.delete("https://httpbin.org/delete")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testHeadMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.head("https://httpbin.org/headers")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testOptionsMethod() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.options("https://httpbin.org/headers")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }
}
