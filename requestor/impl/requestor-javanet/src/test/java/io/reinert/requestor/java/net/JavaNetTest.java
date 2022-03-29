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

import io.reinert.requestor.core.PollingRequest;
import io.reinert.requestor.core.ReadProgress;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.WriteProgress;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ReadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;
import io.reinert.requestor.core.callback.VoidCallback;
import io.reinert.requestor.core.callback.WriteCallback;

/**
 * Base class for requestor-javanet tests.
 *
 * @author Danilo Reinert
 */
public class JavaNetTest {

    public static class TestResult {
        private Boolean succeeded;
        private Throwable error;

        public synchronized void success() {
            if (hasFinished()) return;
            succeeded = true;
        }

        public boolean hasFailed() {
            return !succeeded;
        }

        public boolean hasFinished() {
            return succeeded != null;
        }

        public synchronized void fail(Throwable e) {
            if (hasFinished()) return;
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

    interface TestPayloadCallback<E> {
        void execute(E payload) throws Throwable;
    }

    interface TestPayloadResponseCallback<E> {
        void execute(E payload, Response response) throws Throwable;
    }

    interface TestPayloadResponseRequestCallback<E> {
        void execute(E payload, Response response, PollingRequest<E> request) throws Throwable;
    }

    interface TestResponseCallback {
        void execute(Response response) throws Throwable;
    }

    interface TestResponseRequestCallback<E> {
        void execute(Response response, PollingRequest<E> request) throws Throwable;
    }

    interface TestExceptionCallback {
        void execute(RequestException exception) throws Throwable;
    }

    interface TestExceptionRequestCallback<E> {
        void execute(RequestException exception, PollingRequest<E> request) throws Throwable;
    }

    interface TestTimeoutCallback {
        void execute(RequestTimeoutException exception) throws Throwable;
    }

    interface TestTimeoutRequestCallback<E> {
        void execute(RequestTimeoutException exception, PollingRequest<E> request) throws Throwable;
    }

    interface TestVoidCallback {
        void execute() throws Throwable;
    }

    interface TestReadCallback {
        void execute(ReadProgress p) throws Throwable;
    }

    interface TestWriteCallback {
        void execute(WriteProgress p) throws Throwable;
    }

    protected void finishTest(TestResult result, long timeout) throws Throwable {
        try {
            Thread.sleep(timeout);
            throw new Error("Test timeout after " + timeout + " milliseconds.");
        } catch (InterruptedException e) {
            if (!result.hasFinished()) {
                throw new Error("Test wasn't finished either as success or failure.");
            }

            if (result.hasFailed()) {
                if (result.hasError()) {
                    throw result.getError();
                } else {
                    throw new Error("Test failed with no exception trace.");
                }
            }
        }
    }

    protected ExceptionCallback failOnError(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return e -> {
            result.fail(e);
            thread.interrupt();
        };
    }

    protected VoidCallback failOnEvent(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return () -> {
            result.fail(new Error("Request event not expected to be triggered."));
            thread.interrupt();
        };
    }

    protected TimeoutCallback failOnTimeout(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return e -> {
            result.fail(e);
            thread.interrupt();
        };
    }

    protected ExceptionCallback succeedOnError(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return e -> {
            result.success();
            thread.interrupt();
        };
    }

    protected VoidCallback succeedOnEvent(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return () -> {
            result.success();
            thread.interrupt();
        };
    }

    protected TimeoutCallback succeedOnTimeout(final TestResult result) {
        final Thread thread = Thread.currentThread();
        return e -> {
            result.success();
            thread.interrupt();
        };
    }

    protected VoidCallback test(final TestResult result, final TestVoidCallback callback) {
        final Thread thread = Thread.currentThread();
        return () -> {
            try {
                callback.execute();
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected <E> PayloadCallback<E> test(final TestResult result, final TestPayloadCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return e -> {
            try {
                callback.execute(e);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected <E> PayloadResponseCallback<E> test(final TestResult result,
                                                  final TestPayloadResponseCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, res) -> {
            try {
                callback.execute(e, res);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected <E> PayloadResponseRequestCallback<E> test(final TestResult result,
                                                         final TestPayloadResponseRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, res, req) -> {
            try {
                callback.execute(e, res, req);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected ResponseCallback test(final TestResult result, final TestResponseCallback callback) {
        final Thread thread = Thread.currentThread();
        return res -> {
            try {
                callback.execute(res);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected <E> ResponseRequestCallback<E> test(final TestResult result,
                                                  final TestResponseRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (res, req) -> {
            try {
                callback.execute(res, req);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected ExceptionCallback test(final TestResult result, final TestExceptionCallback callback) {
        final Thread thread = Thread.currentThread();
        return e -> {
            try {
                callback.execute(e);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected <E> ExceptionRequestCallback<E> test(final TestResult result,
                                                   final TestExceptionRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, req) -> {
            try {
                callback.execute(e, req);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected TimeoutCallback test(final TestResult result, final TestTimeoutCallback callback) {
        final Thread thread = Thread.currentThread();
        return e -> {
            try {
                callback.execute(e);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected <E> TimeoutRequestCallback<E> test(final TestResult result,
                                                 final TestTimeoutRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, req) -> {
            try {
                callback.execute(e, req);
                result.success();
            } catch (Throwable error) {
                result.fail(error);
            } finally {
                thread.interrupt();
            }
        };
    }

    protected ReadCallback assay(final TestResult result, final TestReadCallback callback) {
        final Thread thread = Thread.currentThread();
        return p -> {
            try {
                callback.execute(p);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected WriteCallback assay(final TestResult result, final TestWriteCallback callback) {
        final Thread thread = Thread.currentThread();
        return p -> {
            try {
                callback.execute(p);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected VoidCallback assay(final TestResult result, final TestVoidCallback callback) {
        final Thread thread = Thread.currentThread();
        return () -> {
            try {
                callback.execute();
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected <E> PayloadResponseCallback<E> assay(final TestResult result,
                                                   final TestPayloadResponseCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, res) -> {
            try {
                callback.execute(e, res);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected <E> PayloadResponseRequestCallback<E> assay(final TestResult result,
                                                          final TestPayloadResponseRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, res, req) -> {
            try {
                callback.execute(e, res, req);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected ResponseCallback assay(final TestResult result, final TestResponseCallback callback) {
        final Thread thread = Thread.currentThread();
        return res -> {
            try {
                callback.execute(res);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected <E> ResponseRequestCallback<E> assay(final TestResult result,
                                                   final TestResponseRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (res, req) -> {
            try {
                callback.execute(res, req);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected ExceptionCallback assay(final TestResult result, final TestExceptionCallback callback) {
        final Thread thread = Thread.currentThread();
        return e -> {
            try {
                callback.execute(e);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected <E> ExceptionRequestCallback<E> assay(final TestResult result,
                                                    final ExceptionRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, req) -> {
            try {
                callback.execute(e, req);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected TimeoutCallback assay(final TestResult result, final TimeoutCallback callback) {
        final Thread thread = Thread.currentThread();
        return e -> {
            try {
                callback.execute(e);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }

    protected <E> TimeoutRequestCallback<E> assay(final TestResult result, final TimeoutRequestCallback<E> callback) {
        final Thread thread = Thread.currentThread();
        return (e, req) -> {
            try {
                callback.execute(e, req);
            } catch (Throwable error) {
                result.fail(error);
                thread.interrupt();
            }
        };
    }
}
