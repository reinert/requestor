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

import io.reinert.requestor.impl.gdeferred.DoneCallback;

/**
 * Integration tests of {@link RequestInterceptor}.
 */
public class RequestInterceptorGwtTest extends GWTTestCase {

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
        final String storageKey = "testData";
        final String expectedStorageValue = "testData";

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                request.getStorage().set(storageKey, expectedStorageValue);
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).done(new DoneCallback<String>() {
            public void onDone(Response<String> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStorageValue, response.getStorage().get(storageKey));
                assertTrue(response.getPayload().contains("\"Test\": \"test\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testTwoInterceptors() {
        final String storageKey = "testData";
        final String expectedStorageValue = "testData";

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                request.getStorage().set(storageKey, expectedStorageValue);
                request.proceed();
            }
        });

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                // Test previous intercept
                assertEquals(expectedStorageValue, request.getStorage().get(storageKey));
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).done(new DoneCallback<String>() {
            public void onDone(Response<String> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStorageValue, response.getStorage().get(storageKey));
                assertTrue(response.getPayload().contains("\"Test\": \"test\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testThreeInterceptors() {
        final String storageKey = "testData";
        final String expectedStorageValue = "testData";

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                request.getStorage().set(storageKey, expectedStorageValue);
                request.proceed();
            }
        });

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                request.setHeader("Test2", "test2");
                request.proceed();
            }
        });

        requestor.req("https://httpbin.org/get").get(String.class).done(new DoneCallback<String>() {
            public void onDone(Response<String> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertTrue(response.getPayload().contains("\"Test\": \"test\""));
                assertEquals(expectedStorageValue, response.getStorage().get(storageKey));
                assertTrue(response.getPayload().contains("\"Test2\": \"test2\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }

    public void testAsyncInterceptors() {
        final String storageKey = "testData";
        final String expectedStorageValue = "testData";
        final String storageKey2 = "testData2";
        final String expectedStorageValue2 = "testData2";

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                request.getStorage().set(storageKey, expectedStorageValue);
                request.proceed();
            }
        });

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(final SerializedRequestInProcess request) {
                new Timer() {
                    public void run() {
                        assertEquals(expectedStorageValue, request.getStorage().get(storageKey));
                        request.getStorage().set(storageKey2, expectedStorageValue2);
                        request.proceed();
                    }
                }.schedule(500);
            }
        });

        requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                // Test previous intercept
                assertEquals(expectedStorageValue2, request.getStorage().get(storageKey2));
                request.setHeader("Test", "test");
                request.proceed();
            }
        });

        requestor.register(new RequestInterceptor() {
            @Override
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

        requestor.req("https://httpbin.org/get").get(String.class).done(new DoneCallback<String>() {
            public void onDone(Response<String> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertEquals(expectedStorageValue, response.getStorage().get(storageKey));
                assertEquals(expectedStorageValue2, response.getStorage().get(storageKey2));
                assertTrue(response.getPayload().contains("\"Test\": \"test\""));
                assertTrue(response.getPayload().contains("\"Test2\": \"test2\""));
                finishTest();
            }
        });

        delayTestFinish(TIMEOUT);
    }
}
