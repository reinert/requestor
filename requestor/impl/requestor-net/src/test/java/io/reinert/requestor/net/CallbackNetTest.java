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
package io.reinert.requestor.net;

import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Tests for basic request events.
 */
public class CallbackNetTest {

    static class TestResult {
        private boolean succeeded = false;
        private Throwable error;

        public void success() {
            succeeded = true;
        }

        public boolean hasFailed() {
            return !succeeded;
        }

        public void fail(Throwable e) {
            succeeded = false;
            error = e;
        }

        public Throwable getError() {
            return error;
        }

        public boolean hasError() {
            return error != null;
        }
    }

    abstract static class TestPayloadCallback<E> implements PayloadCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestPayloadCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(E payload);

        public void execute(E payload) {
            try {
                test(payload);
                result.success();
            } catch (RuntimeException error) {
                result.fail(error);
            } catch (Error error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        }
    }

    private static final int TIMEOUT = 30000;

    //=========================================================================
    // SUCCESS CALLBACKS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testSuccessCallback() {
        TestResult result = new TestResult();

        Session session = new NetSession();

        session.get("https://httpbin.org/get", String.class)
                .onSuccess(new TestPayloadCallback<String>(result) {
                    @Override
                    public void test(String payload) {
                        System.out.println(payload);
                        assertNotNull(payload);
                    }
                }).onError(failExceptionCallback(result));

        finishTest(result);
    }

    @Test(timeout = TIMEOUT)
    public void testPostCallback() {
        TestResult result = new TestResult();

        Session session = new NetSession();

        session.setMediaType("application/json");

        session.post("https://httpbin.org/post", "TESTE", String.class)
                .onSuccess(new TestPayloadCallback<String>(result) {
                    @Override
                    public void test(String payload) {
                        System.out.println(payload);
                        assertNotNull(payload);
                    }
                }).onError(failExceptionCallback(result));

        finishTest(result);
    }

    private void finishTest(TestResult result) {
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            if (result.hasFailed()) {
                if (result.hasError()) {
                    Throwable error = result.getError();
                    if (error instanceof RuntimeException) {
                        throw (RuntimeException) error;
                    }
                    throw (Error) error;
                } else {
                    fail();
                }
            }
        }
    }

    protected ExceptionCallback failExceptionCallback(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new ExceptionCallback() {
            public void execute(RequestException e) {
                result.fail(e);
                thread.interrupt();
            }
        };
    }
}
