/*
 * Copyright 2014 Danilo Reinert
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
package io.reinert.requestor.test;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author Danilo Reinert
 */
public class RestTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

//    public void testCreate() {
//        ServerStub.clearStub();
//
//        final Requestor requestor = new RequestorImpl();
//        requestor.register(BookJsonSerializer.getInstance());
//
//        final String uri = "/server/books";
//
//        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK", new ContentTypeHeader("application/json")));
//
//        final String expected = "{\"id\":1,\"title\":\"RESTful Web Services\",\"author\":\"Leonard Richardson\"}";
//
//        final boolean[] callbacksCalled = new boolean[1];
//        final Book data = new Book(1, "RESTful Web Services", "Leonard Richardson");
//
//        requestor.req(uri).payload(data).post().done(new DoneCallback<Void>() {
//            @Override
//            public void onDone(Void aVoid) {
//                callbacksCalled[0] = true;
//            }
//        });
//        ServerStub.triggerPendingRequest();
//
//        assertTrue(callbacksCalled[0]);
//        assertEquals(expected, ServerStub.getRequestData(uri).getData());
//        assertEquals(RequestBuilder.POST, ServerStub.getRequestData(uri).getMethod());
//    }
//
//    public void testDelete() {
//        ServerStub.clearStub();
//
//        final Requestor requestor = new RequestorImpl();
//
//        final String uri = "/server/books/1";
//
//        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK", new ContentTypeHeader("application/json")));
//
//        requestor.req(uri).delete(); // You can optionally dismiss any server response
//        ServerStub.triggerPendingRequest();
//
//        assertEquals(RequestBuilder.DELETE, ServerStub.getRequestData(uri).getMethod());
//    }
//
//    public void testGetAll() {
//        ServerStub.clearStub();
//
//        final Requestor requestor = new RequestorImpl();
//        requestor.register(BookJsonSerializer.getInstance());
//
//        final String uri = "/server/books";
//
//      final String responseText = "[{\"id\":1, \"title\":\"RESTful Web Services\", \"author\":\"Leonard Richardson\"}"
//                + ", {\"id\":2, \"title\":\"Agile Software Development: Principles, Patterns, and Practices\", "
//                + "\"author\":\"Robert C. Martin\"}]";
//        ServerStub.responseFor(uri, ResponseMock.of(responseText, 200, "OK",
//                new ContentTypeHeader("application/json")));
//
//        final List<Book> expected = new ArrayList<Book>(2);
//        expected.add(new Book(1, "RESTful Web Services", "Leonard Richardson"));
//        expected.add(new Book(2, "Agile Software Development: Principles, Patterns, and Practices",
//                "Robert C. Martin"));
//
//        final boolean[] callbacksCalled = new boolean[1];
//
//        requestor.req(uri).get(Book.class, List.class).done(new ListDoneCallback<Book>() {
//            @Override
//            public void onDone(List<Book> books) {
//                callbacksCalled[0] = true;
//                assertEquals(expected, books);
//            }
//        });
//        ServerStub.triggerPendingRequest();
//
//        assertTrue(callbacksCalled[0]);
//        assertEquals(RequestBuilder.GET, ServerStub.getRequestData(uri).getMethod());
//    }
//
//    public void testGetOne() {
//        ServerStub.clearStub();
//
//        final Requestor requestor = new RequestorImpl();
//        requestor.register(BookJsonSerializer.getInstance());
//
//        final String uri = "/server/books/1";
//
//      final String responseText = "{\"id\":1, \"title\":\"RESTful Web Services\", \"author\":\"Leonard Richardson\"}";
//        ServerStub.responseFor(uri, ResponseMock.of(responseText, 200, "OK",
//                new ContentTypeHeader("application/json")));
//
//        final Book expected = new Book(1, "RESTful Web Services", "Leonard Richardson");
//
//        final boolean[] callbacksCalled = new boolean[1];
//
//        requestor.req(uri).get(Book.class).done(new DoneCallback<Book>() {
//            @Override
//            public void onDone(Book book) {
//                callbacksCalled[0] = true;
//                assertEquals(expected, book);
//            }
//        });
//        ServerStub.triggerPendingRequest();
//
//        assertTrue(callbacksCalled[0]);
//        assertEquals(RequestBuilder.GET, ServerStub.getRequestData(uri).getMethod());
//    }
//
//    public void testUpdate() {
//        ServerStub.clearStub();
//
//        final Requestor requestor = new RequestorImpl();
//        requestor.register(BookJsonSerializer.getInstance());
//
//        final String uri = "/server/books/1";
//
//        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK", new ContentTypeHeader("application/json")));
//
//        final String expected = "{\"id\":1,\"title\":\"RESTful Web Services\",\"author\":\"Leonard Richardson\"}";
//
//        final boolean[] callbacksCalled = new boolean[1];
//        final Book data = new Book(1, "RESTful Web Services", "Leonard Richardson");
//
//        requestor.req(uri).payload(data).put().done(new DoneCallback<Void>() {
//            @Override
//            public void onDone(Void result) {
//                callbacksCalled[0] = true;
//            }
//        });
//        ServerStub.triggerPendingRequest();
//
//        assertTrue(callbacksCalled[0]);
//        assertEquals(expected, ServerStub.getRequestData(uri).getData());
//        assertEquals(RequestBuilder.PUT, ServerStub.getRequestData(uri).getMethod());
//    }
}
