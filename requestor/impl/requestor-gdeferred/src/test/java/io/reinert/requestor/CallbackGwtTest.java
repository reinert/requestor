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
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.gdeferred.Promise;
import io.reinert.requestor.impl.gdeferred.AlwaysCallback;
import io.reinert.requestor.impl.gdeferred.DoneCallback;
import io.reinert.requestor.impl.gdeferred.FailCallback;
import io.reinert.requestor.impl.gdeferred.ListDoneCallback;
import io.reinert.requestor.impl.gdeferred.SetDoneCallback;
import io.reinert.requestor.impl.gdeferred.StatusCallback;
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
    // DONE CALLBACKS
    //=========================================================================

    public void testDoneCallback() {
        // GET /books/1
        final Uri uri = uriBuilder.path("1").build();
        requestor.req(uri).get(Book.class).done(new DoneCallback<Book>() {
            public void onDone(Book result) {
                assertNotNull(result);

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testDoneCallbackResponseOverload() {
        // GET /books/1
        final Uri uri = uriBuilder.path("1").build();
        requestor.req(uri).get(Book.class).done(new DoneCallback<Book>() {
            public void onDone(Response<Book> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testDoneCallbackForCollection() {
        // GET /books
        final Uri uri = uriBuilder.build();
        requestor.req(uri).get(Book.class, List.class).done(new DoneCallback<Collection<Book>>() {
            public void onDone(Collection<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testDoneCallbackForCollectionResponseOverload() {
        // GET /books
        final Uri uri = uriBuilder.build();
        requestor.req(uri).get(Book.class, List.class).done(new DoneCallback<Collection<Book>>() {
            public void onDone(Response<Collection<Book>> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertFalse(response.getPayload().isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testListDoneCallback() {
        // GET /books
        final Uri uri = uriBuilder.build();
        requestor.req(uri).get(Book.class, List.class).done(new ListDoneCallback<Book>() {
            public void onDone(List<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testListDoneCallbackResponseOverload() {
        // GET /books
        final Uri uri = uriBuilder.build();
        requestor.req(uri).get(Book.class, List.class).done(new ListDoneCallback<Book>() {
            public void onDone(Response<List<Book>> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertFalse(response.getPayload().isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }
    public void testSetDoneCallback() {
        // GET /books
        final Uri uri = uriBuilder.build();
        requestor.req(uri).get(Book.class, Set.class).done(new SetDoneCallback<Book>() {
            public void onDone(Set<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testSetDoneCallbackResponseOverload() {
        // GET /books
        final Uri uri = uriBuilder.build();
        requestor.req(uri).get(Book.class, Set.class).done(new SetDoneCallback<Book>() {
            public void onDone(Response<Set<Book>> response) {
                assertNotNull(response);
                assertNotNull(response.getPayload());
                assertFalse(response.getPayload().isEmpty());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    //=========================================================================
    // FAIL CALLBACKS
    //=========================================================================

    public void test301FailCallback() {
        requestor.req("http://httpbin.org/status/300").get()
                .done(new DoneCallback<Void>() {
                    public void onDone(Void result) {
                        fail();
                    }
                }).fail(new FailCallback() {
                    public void onFail(Throwable throwable) {
                        assertNotNull(throwable);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void test400FailCallback() {
        requestor.req("http://httpbin.org/status/400").get()
                .done(new DoneCallback<Void>() {
                    public void onDone(Void result) {
                        fail();
                    }
                }).fail(new FailCallback() {
                    public void onFail(Throwable throwable) {
                        assertNotNull(throwable);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void test500FailCallback() {
        requestor.req("http://httpbin.org/status/500").get()
                .done(new DoneCallback<Void>() {
                    public void onDone(Void result) {
                        fail();
                    }
                }).fail(new FailCallback() {
                    public void onFail(Throwable throwable) {
                        assertNotNull(throwable);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    //=========================================================================
    // ALWAYS CALLBACKS
    //=========================================================================

    public void testAlwaysCallbackOnSuccessfulResponse() {
        requestor.req("http://httpbin.org/status/200").get(String.class)
                .always(new AlwaysCallback<String>() {
                    public void onAlways(Promise.State state, String body, Throwable throwable) {
                        assertEquals(Promise.State.RESOLVED, state);
                        assertNotNull(body);
                        assertNull(throwable);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testAlwaysCallbackOnUnsuccessfulResponse() {
        requestor.req("http://httpbin.org/status/400").get(String.class)
                .always(new AlwaysCallback<String>() {
                    public void onAlways(Promise.State state, String body, Throwable throwable) {
                        assertEquals(Promise.State.REJECTED, state);
                        assertNull(body);
                        assertNotNull(throwable);

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    //=========================================================================
    // RESPONSE CALLBACKS
    //=========================================================================

    public void testDoneResponseCallback() {
        requestor.req("http://httpbin.org/status/200").get(String.class)
                .doneResponse(new io.reinert.gdeferred.DoneCallback<Response<String>>() {
                    @Override
                    public void onDone(Response<String> response) {
                        assertNotNull(response);
                        assertNotNull(response.getPayload());
                        assertEquals(200, response.getStatusCode());

                        finishTest();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testFailResponseCallback() {
        requestor.req("http://httpbin.org/status/400").get(String.class)
                .failResponse(new io.reinert.gdeferred.FailCallback<SerializedResponse>() {
                    public void onFail(SerializedResponse response) {
                        assertNotNull(response);
                        assertNotNull(response.getPayload());
                        assertEquals(400, response.getStatusCode());

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
                .status(200, new StatusCallback() {
                    public void onStatus(Response<?> response) {
                        assertNotNull(response);
                        assertEquals(200, response.getStatusCode());
                        Response<String> r = (Response<String>) response;
                        assertNotNull(r.getPayload());

                        finishTest();
                    }
                }).status(400, new StatusCallback() {
                    public void onStatus(Response<?> response) {
                        fail();
                    }
                });
        delayTestFinish(TIMEOUT);
    }

    public void testStatusCallback400() {
        requestor.req("http://httpbin.org/status/400").get(String.class)
                .status(200, new StatusCallback() {
                    public void onStatus(Response<?> response) {
                        fail();
                    }
                }).status(400, new StatusCallback() {
                    public void onStatus(Response<?> response) {
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
                .status(200, new StatusCallback() {
                    public void onStatus(Response<?> response) {
                        fail();
                    }
                }).status(400, new StatusCallback() {
                    public void onStatus(Response<?> response) {
                        fail();
                    }
                }).status(500, new StatusCallback() {
                    public void onStatus(Response<?> response) {
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
