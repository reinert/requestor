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
package io.reinert.requestor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import io.reinert.requestor.callback.ResponseCallback;

/**
 * Integration tests of {@link ResponseInterceptor}.
 */
public class ResponseInterceptorGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 5000;

    private Requestor requestor;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        requestor = GWT.create(Requestor.class);
        requestor.setMediaType("application/json");
    }

    public void testOneInterceptor() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                response.getStore().put(storeKey, expectedStoreValue);
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertEquals("test", response.getHeader("Test"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testTwoInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                response.getStore().put(storeKey, expectedStoreValue);
                response.proceed();
            }
        });

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                // Test previous intercept
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertEquals("test", response.getHeader("Test"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testThreeInterceptors() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                assertEquals("test", response.getHeader("Test"));
                response.getStore().put(storeKey, expectedStoreValue);
                response.proceed();
            }
        });

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                assertEquals("test", response.getHeader("Test"));
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                response.setHeader("Test2", "test2");
                response.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).load(new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals("test", response.getHeader("Test"));
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
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

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                response.getStore().put(storeKey, expectedStoreValue);
                response.proceed();
            }
        });

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(final SerializedResponseInProcess response) {
                new Timer() {
                    public void run() {
                        assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                        response.getStore().put(storeKey2, expectedStoreValue2);
                        response.proceed();
                    }
                }.schedule(500);
            }
        });

        requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                // Test previous intercept
                assertEquals(expectedStoreValue2, response.getStore().get(storeKey2));
                response.setHeader("Test", "test");
                response.proceed();
            }
        });

        requestor.register(new ResponseInterceptor() {
            @Override
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

        requestor.req("https://httpbin.org/get").get(String.class).load(new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertEquals(expectedStoreValue2, response.getStore().get(storeKey2));
                assertEquals("test", response.getHeader("Test"));
                assertEquals("test2", response.getHeader("Test2"));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }
}