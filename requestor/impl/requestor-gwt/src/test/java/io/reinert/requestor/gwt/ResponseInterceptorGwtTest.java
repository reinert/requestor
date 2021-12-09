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
import com.google.gwt.user.client.Timer;

import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.ResponseInterceptor;
import io.reinert.requestor.core.SerializedResponseInProcess;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.ResponseCallback;

/**
 * Integration tests of {@link ResponseInterceptor}.
 */
public class ResponseInterceptorGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 5000;

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

    public void testOneInterceptor() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                response.save(storeKey, expectedStoreValue);
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onStatus(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.retrieve(storeKey));
                assertEquals("test", response.getHeader("Test"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testTwoInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                response.save(storeKey, expectedStoreValue);
                response.proceed();
            }
        });

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                // Test previous intercept
                assertEquals(expectedStoreValue, response.retrieve(storeKey));
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onStatus(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.retrieve(storeKey));
                assertEquals("test", response.getHeader("Test"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testThreeInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                assertEquals("test", response.getHeader("Test"));
                response.save(storeKey, expectedStoreValue);
                response.proceed();
            }
        });

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                assertEquals("test", response.getHeader("Test"));
                assertEquals(expectedStoreValue, response.retrieve(storeKey));
                response.setHeader("Test2", "test2");
                response.proceed();
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onLoad(new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals("test", response.getHeader("Test"));
                assertEquals(expectedStoreValue, response.retrieve(storeKey));
                assertEquals("test2", response.getHeader("Test2"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testAsyncInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";
        final String storeKey2 = "testData2";
        final String expectedStoreValue2 = "testData2";

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                response.save(storeKey, expectedStoreValue);
                response.proceed();
            }
        });

        session.register(new ResponseInterceptor() {
            public void intercept(final SerializedResponseInProcess response) {
                new Timer() {
                    public void run() {
                        assertEquals(expectedStoreValue, response.retrieve(storeKey));
                        response.save(storeKey2, expectedStoreValue2);
                        response.proceed();
                    }
                }.schedule(500);
            }
        });

        session.register(new ResponseInterceptor() {
            public void intercept(SerializedResponseInProcess response) {
                // Test previous intercept
                assertEquals(expectedStoreValue2, response.retrieve(storeKey2));
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        session.register(new ResponseInterceptor() {
            public void intercept(final SerializedResponseInProcess response) {
                new Timer() {
                    @Override
                    public void run() {
                        response.setHeader("Test2", "test2");
                        response.proceed();
                    }
                }.schedule(500);
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onLoad(new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.retrieve(storeKey));
                assertEquals(expectedStoreValue2, response.retrieve(storeKey2));
                assertEquals("test", response.getHeader("Test"));
                assertEquals("test2", response.getHeader("Test2"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }
}
