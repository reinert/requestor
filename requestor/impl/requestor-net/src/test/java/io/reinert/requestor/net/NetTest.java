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

import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;
import io.reinert.requestor.core.callback.VoidCallback;

import static org.junit.Assert.fail;

/**
 * Base class for requestor-net tests.
 *
 * @author Danilo Reinert
 */
public class NetTest {

    public static class TestResult {
        private Boolean succeeded;
        private Throwable error;

        public synchronized void success() {
            if (succeeded != null) throw new IllegalStateException("Cannot finish TestResult twice.");
            succeeded = true;
        }

        public boolean hasFailed() {
            return !succeeded;
        }

        public synchronized void fail(Throwable e) {
            if (succeeded != null) throw new IllegalStateException("Cannot finish TestResult twice.");
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

    public abstract static class TestPayloadCallback<E> implements PayloadCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestPayloadCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(E payload);

        //@Override
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

    public abstract static class TestPayloadResponseCallback<E> implements PayloadResponseCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestPayloadResponseCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(E payload, Response response);

        //@Override
        public void execute(E payload, Response response) {
            try {
                test(payload, response);
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

    public abstract static class TestPayloadResponseRequestCallback<E> implements PayloadResponseRequestCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestPayloadResponseRequestCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(E payload, Response response, PollingRequest<E> request);

        //@Override
        public void execute(E payload, Response response, PollingRequest<E> request) {
            try {
                test(payload, response, request);
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

    public abstract static class TestResponseCallback implements ResponseCallback {

        private final Thread thread;
        private final TestResult result;

        public TestResponseCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(Response response);

        //@Override
        public void execute(Response response) {
            try {
                test(response);
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

    public abstract static class TestResponseRequestCallback<E> implements ResponseRequestCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestResponseRequestCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(Response response, PollingRequest<E> request);

        //@Override
        public void execute(Response response, PollingRequest<E> request) {
            try {
                test(response, request);
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

    public abstract static class TestExceptionCallback implements ExceptionCallback {

        private final Thread thread;
        private final TestResult result;

        public TestExceptionCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(RequestException exception);

        //@Override
        public void execute(RequestException exception) {
            try {
                test(exception);
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

    public abstract static class TestExceptionRequestCallback<E> implements ExceptionRequestCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestExceptionRequestCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(RequestException exception, PollingRequest<E> request);

        //@Override
        public void execute(RequestException exception, PollingRequest<E> request) {
            try {
                test(exception, request);
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

    public abstract static class TestTimeoutCallback implements TimeoutCallback {

        private final Thread thread;
        private final TestResult result;

        public TestTimeoutCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(RequestTimeoutException exception);

        //@Override
        public void execute(RequestTimeoutException exception) {
            try {
                test(exception);
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

    public abstract static class TestTimeoutRequestCallback<E> implements TimeoutRequestCallback<E> {

        private final Thread thread;
        private final TestResult result;

        public TestTimeoutRequestCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test(RequestTimeoutException exception, PollingRequest<E> request);

        //@Override
        public void execute(RequestTimeoutException exception, PollingRequest<E> request) {
            try {
                test(exception, request);
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

    public abstract static class TestVoidCallback implements VoidCallback {

        private final Thread thread;
        private final TestResult result;

        public TestVoidCallback(TestResult result) {
            this.thread = Thread.currentThread();
            this.result = result;
        }

        public abstract void test();

        //@Override
        public void execute() {
            try {
                test();
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

    protected void finishTest(TestResult result, long timeout) {
        try {
            Thread.sleep(timeout);
            throw new Error("Test timeout after " + timeout + " milliseconds.");
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

    protected ExceptionCallback failOnError(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new ExceptionCallback() {
            public void execute(RequestException e) {
                result.fail(e);
                thread.interrupt();
            }
        };
    }

    protected VoidCallback failOnEvent(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new VoidCallback() {
            public void execute() {
                result.fail(new Error("Request event not expected to be triggered."));
                thread.interrupt();
            }
        };
    }

    protected TimeoutCallback failOnTimeout(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new TimeoutCallback() {
            public void execute(RequestTimeoutException e) {
                result.fail(e);
                thread.interrupt();
            }
        };
    }

    protected ExceptionCallback succeedOnError(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new ExceptionCallback() {
            public void execute(RequestException e) {
                result.success();
                thread.interrupt();
            }
        };
    }

    protected VoidCallback succeedOnEvent(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new VoidCallback() {
            public void execute() {
                result.success();
                thread.interrupt();
            }
        };
    }

    protected TimeoutCallback succeedOnTimeout(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return new TimeoutCallback() {
            public void execute(RequestTimeoutException e) {
                result.success();
                thread.interrupt();
            }
        };
    }
}
