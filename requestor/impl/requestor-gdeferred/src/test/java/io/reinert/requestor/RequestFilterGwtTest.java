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
 * Integration tests of {@link RequestFilter}.
 */
public class RequestFilterGwtTest extends GWTTestCase {

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

    public void testOneFilter() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.getStore().put(storeKey, expectedStoreValue);
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testTwoFilters() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.getStore().put(storeKey, expectedStoreValue);
                request.proceed();
            }
        });

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                // Test previous filter
                assertEquals(expectedStoreValue, request.getStore().get(storeKey));
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testThreeFilters() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.getStore().put(storeKey, expectedStoreValue);
                request.proceed();
            }
        });

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.setHeader("Test2", "test2");
                request.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertTrue(response.getPayload().toString().contains("\"Test\": \"test\""));
                assertEquals(expectedStoreValue, response.getStore().get(storeKey));
                assertTrue(response.getPayload().toString().contains("\"Test2\": \"test2\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testAsyncFilters() {
        final String storeKey = "testData";
        final String expectedStoreValue = "testData";
        final String storeKey2 = "testData2";
        final String expectedStoreValue2 = "testData2";

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.getStore().put(storeKey, expectedStoreValue);
                request.proceed();
            }
        });

        requestor.register(new RequestFilter() {
            @Override
            public void filter(final RequestInProcess request) {
                new Timer() {
                    public void run() {
                        assertEquals(expectedStoreValue, request.getStore().get(storeKey));
                        request.getStore().put(storeKey2, expectedStoreValue2);
                        request.proceed();
                    }
                }.schedule(500);
            }
        });

        requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                // Test previous filter
                assertEquals(expectedStoreValue2, request.getStore().get(storeKey2));
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.register(new RequestFilter() {
            @Override
            public void filter(final RequestInProcess request) {
                new Timer() {
                    @Override
                    public void run() {
                        request.setHeader("Test2", "test2");
                        request.proceed();
                    }
                }.schedule(500);
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).status(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
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
