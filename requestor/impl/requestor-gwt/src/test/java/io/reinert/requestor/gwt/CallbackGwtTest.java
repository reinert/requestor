/*
 * Copyright 2021-2022 Danilo Reinert
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

import java.util.Collection;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestFilter;
import io.reinert.requestor.core.RequestInProcess;
import io.reinert.requestor.core.RequestTimeoutException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.core.uri.UriBuilder;

/**
 * Tests for basic request events.
 */
public class CallbackGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 2500;

    private Session session;
    private UriBuilder uriBuilder;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.gwt.RequestorGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        session = Requestor.newSession();

        session.register(BookJsonSerializer.getInstance());

        // The mockapi service requires us to explicitly inform the content type header
        session.setMediaType("application/json");

        // Delay requests to avoid 429 Too Many Requests
        session.setDelay(1000);

        uriBuilder = UriBuilder.fromUri("https://605740e1055dbd0017e8493a.mockapi.io/requestor/tests/books");
    }

    //=========================================================================
    // SUCCESS CALLBACKS
    //=========================================================================

    public void testSuccessCallback() {
        // GET /books/1
        final Uri uri = uriBuilder.path("1").build();
        session.req(uri).get(Book.class).onSuccess(new PayloadCallback<Book>() {
            public void execute(Book result) {
                assertNotNull(result);

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testSuccessCallbackForCollection() {
        // GET /books
        final Uri uri = uriBuilder.build();
        session.req(uri).get(List.class, Book.class).onSuccess(new PayloadCallback<Collection<Book>>() {
            public void execute(Collection<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    //=========================================================================
    // ERROR CALLBACKS
    //=========================================================================

    public void test301ErrorCallback() {
        session.req("http://httpbin.org/status/300").get()
                .onSuccess(new PayloadCallback<Void>() {
                    public void execute(Void result) {
                        fail();
                    }
                }).onStatus(300, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void test400ErrorCallback() {
        session.req("http://httpbin.org/status/400").get()
                .onSuccess(new PayloadCallback<Void>() {
                    public void execute(Void result) {
                        fail();
                    }
                }).onStatus(400, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void test500ErrorCallback() {
        session.req("http://httpbin.org/status/500").get()
                .onSuccess(new PayloadCallback<Void>() {
                    public void execute(Void result) {
                        fail();
                    }
                }).onStatus(500, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    //=========================================================================
    // STATUS CALLBACKS
    //=========================================================================

    public void testStatusCallback200() {
        session.req("http://httpbin.org/status/200").get(String.class)
                .onStatus(200, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);
                        assertEquals(200, response.getStatusCode());
                        assertNotNull(response.getPayload());

                        finishTest();
                    }
                }).onStatus(400, new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testStatusCallback400() {
        session.req("http://httpbin.org/status/400").get(String.class)
                .onStatus(200, new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                }).onStatus(400, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);
                        assertEquals(400, response.getStatusCode());
                        assertNotNull(response.getSerializedPayload());

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testStatusCallback500() {
        session.req("http://httpbin.org/status/500").get(String.class)
                .onStatus(200, new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                }).onStatus(400, new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                }).onStatus(500, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);
                        assertEquals(500, response.getStatusCode());
                        assertNotNull(response.getSerializedPayload());

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    //=========================================================================
    // EXCEPTION CALLBACKS
    //=========================================================================

    public void testAbortCallback() {
        session.register(new RequestFilter() {
            public void filter(RequestInProcess request) {
                throw new RuntimeException();
            }
        });

        session.req("http://httpbin.org/status/200").get(String.class)
                .onTimeout(new TimeoutCallback() {
                    public void execute(RequestTimeoutException timeoutException) {
                        fail();
                    }
                }).onCancel(new ExceptionCallback() {
                    public void execute(RequestException exception) {
                        fail();
                    }
                }).onAbort(new ExceptionCallback() {
                    public void execute(RequestException exception) {
                        assertNotNull(exception);

                        finishTest();
                    }
                }).onLoad(new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

      // This test must fail with RequestAbortException
//    public void testNoAbortCallback() {
//        session.register(new RequestFilter() {
//            public void filter(RequestInProcess request) {
//                throw new RuntimeException();
//            }
//        });
//
//        session.req("http://httpbin.org/status/200").get(String.class)
//                .onTimeout(new TimeoutCallback() {
//                    public void execute(RequestTimeoutException timeoutException) {
//                        fail();
//                    }
//                }).onCancel(new ExceptionCallback() {
//                    @Override
//                    public void execute(RequestException exception) {
//                        fail();
//                    }
//                }).onLoad(new ResponseCallback() {
//                    public void execute(Response response) {
//                        fail();
//                    }
//                });
//
//        delayTestFinish(TIMEOUT);
//    }
}
