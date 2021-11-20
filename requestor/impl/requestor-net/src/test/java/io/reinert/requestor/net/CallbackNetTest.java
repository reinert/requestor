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

    static class Result {
        private boolean failed = false;

        public void fail() {
            failed = true;
        }

        public boolean hasFailed() {
            return failed;
        }
    }

    private static final int TIMEOUT = 3000;

    //=========================================================================
    // SUCCESS CALLBACKS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testSuccessCallback() {
        final Result result = new Result();

        final Session session = new NetSession();

        session.setDelay(500);

        final Thread thread = Thread.currentThread();

        session.get("https://httpbin.org/get", String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String body) {
                        try {
                            assertNotNull(body);
                        } catch (AssertionError e) {
                            result.fail();
                        } finally {
                            thread.interrupt();
                        }
                    }
                }).onError(new ExceptionCallback() {
                    public void execute(RequestException exception) {
                        try {
                            fail();
                        } catch (AssertionError e) {
                            result.fail();
                        } finally {
                            thread.interrupt();
                        }
                    }
                });

        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            if (result.hasFailed()) fail();
        }
    }
}
