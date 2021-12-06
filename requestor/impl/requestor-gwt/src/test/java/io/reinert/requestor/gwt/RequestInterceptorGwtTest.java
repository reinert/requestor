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

import io.reinert.requestor.core.RequestInterceptor;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.SerializedRequestInProcess;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.ResponseCallback;

/**
 * Integration tests of {@link RequestInterceptor}.
 */
public class RequestInterceptorGwtTest extends GWTTestCase {

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

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                request.getStore().save(storeKey, expectedStoreValue);
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onStatus(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertFalse(response.getPayload().isEmpty());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testTwoInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                request.getStore().save(storeKey, expectedStoreValue);
                request.proceed();
            }
        });

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                // Test previous intercept
                assertEquals(expectedStoreValue, request.getStore().get(storeKey));
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onStatus(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertFalse(response.getPayload().isEmpty());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testThreeInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                request.getStore().save(storeKey, expectedStoreValue);
                request.proceed();
            }
        });

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                request.setHeader("Test2", "test2");
                request.proceed();
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onLoad(new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertFalse(response.getPayload().isEmpty());
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertTrue(response.getPayload().toString().contains("\"Test2\": \"test2\""));
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

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                request.getStore().save(storeKey, expectedStoreValue);
                request.proceed();
            }
        });

        session.register(new RequestInterceptor() {
            public void intercept(final SerializedRequestInProcess request) {
                new Timer() {
                    public void run() {
                        assertEquals(expectedStoreValue, request.getStore().get(storeKey));
                        request.getStore().save(storeKey2, expectedStoreValue2);
                        request.proceed();
                    }
                }.schedule(500);
            }
        });

        session.register(new RequestInterceptor() {
            public void intercept(SerializedRequestInProcess request) {
                // Test previous intercept
                assertEquals(expectedStoreValue2, request.getStore().get(storeKey2));
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        session.register(new RequestInterceptor() {
            public void intercept(final SerializedRequestInProcess request) {
                new Timer() {
                    @Override
                    public void run() {
                        request.setHeader("Test2", "test2");
                        request.proceed();
                    }
                }.schedule(500);
            }
        });

        session.req("https://httpbin.org/get").get(String.class).onLoad(new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertFalse(response.getPayload().isEmpty());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertEquals(expectedStoreValue2, response.getStore().get(storeKey2));
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                assertTrue(response.getPayload().toString().contains("\"Test2\": \"test2\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }
}