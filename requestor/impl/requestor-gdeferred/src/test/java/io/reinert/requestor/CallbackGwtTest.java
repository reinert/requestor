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

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.callbacks.PayloadCallback;
import io.reinert.requestor.callbacks.ResponseCallback;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriBuilder;

/**
 * Integration tests of {@link RestService}.
 */
public class CallbackGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 2500;

    private Requestor requestor;
    private UriBuilder uriBuilder;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        uriBuilder = UriBuilder.fromUri("https://605740e1055dbd0017e8493a.mockapi.io/requestor/tests/books");

        requestor = GWT.create(Requestor.class);

        requestor.register(BookJsonSerializer.getInstance());

        // The mockapi service requires us to explicitly inform the content type header
        requestor.setMediaType("application/json");

        // Delay requests to avoid 429 Too Many Requests
        requestor.setDelay(1000);
    }

    //=========================================================================
    // SUCCESS CALLBACKS
    //=========================================================================

    public void testSuccessCallback() {
        // GET /books/1
        final Uri uri = uriBuilder.path("1").build();
        requestor.req(uri).get(Book.class).success(new PayloadCallback<Book>() {
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
        requestor.req(uri).get(Book.class, List.class).success(new PayloadCallback<Collection<Book>>() {
            public void execute(Collection<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

//    public void testListSuccessCallback() {
//        // GET /books
//        final Uri uri = uriBuilder.build();
//        requestor.req(uri).get(Book.class, List.class).success(new ListSuccessCallback<Book>() {
//            public void onSuccess(List<Book> result) {
//                assertNotNull(result);
//                assertFalse(result.isEmpty());
//
//                finishTest();
//            }
//        });
//        delayTestFinish(TIMEOUT);
//    }

//    public void testSetSuccessCallback() {
//        // GET /books
//        final Uri uri = uriBuilder.build();
//        requestor.req(uri).get(Book.class, Set.class).success(new SetSuccessCallback<Book>() {
//            public void onSuccess(Set<Book> result) {
//                assertNotNull(result);
//                assertFalse(result.isEmpty());
//
//                finishTest();
//            }
//        });
//        delayTestFinish(TIMEOUT);
//    }

    //=========================================================================
    // ERROR CALLBACKS
    //=========================================================================

    public void test301ErrorCallback() {
        requestor.req("http://httpbin.org/status/300").get()
                .success(new PayloadCallback<Void>() {
                    public void execute(Void result) {
                        fail();
                    }
                }).status(300, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void test400ErrorCallback() {
        requestor.req("http://httpbin.org/status/400").get()
                .success(new PayloadCallback<Void>() {
                    public void execute(Void result) {
                        fail();
                    }
                }).status(400, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void test500ErrorCallback() {
        requestor.req("http://httpbin.org/status/500").get()
                .success(new PayloadCallback<Void>() {
                    public void execute(Void result) {
                        fail();
                    }
                }).status(500, new ResponseCallback() {
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
        requestor.req("http://httpbin.org/status/200").get(String.class)
                .status(200, new ResponseCallback<Object>() {
                    public void execute(Response<Object> response) {
                        assertNotNull(response);
                        assertEquals(200, response.getStatusCode());
                        assertNotNull(response.getPayload());

                        finishTest();
                    }
                }).status(400, new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testStatusCallback400() {
        requestor.req("http://httpbin.org/status/400").get(String.class)
                .status(200, new ResponseCallback<Object>() {
                    public void execute(Response<Object> response) {
                        fail();
                    }
                }).status(400, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);
                        assertEquals(400, response.getStatusCode());
                        SerializedResponse r = (SerializedResponse) response;
                        assertNotNull(r.getPayload());

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testStatusCallback500() {
        requestor.req("http://httpbin.org/status/500").get(String.class)
                .status(200, new ResponseCallback<Object>() {
                    public void execute(Response<Object> response) {
                        fail();
                    }
                }).status(400, new ResponseCallback() {
                    public void execute(Response response) {
                        fail();
                    }
                }).status(500, new ResponseCallback() {
                    public void execute(Response response) {
                        assertNotNull(response);
                        assertEquals(500, response.getStatusCode());
                        SerializedResponse r = (SerializedResponse) response;
                        assertNotNull(r.getPayload());

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

}
