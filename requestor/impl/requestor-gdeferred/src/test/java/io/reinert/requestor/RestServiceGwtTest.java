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
import java.util.Date;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.impl.gdeferred.DoneCallback;
import io.reinert.requestor.impl.gdeferred.ListDoneCallback;

/**
 * Integration tests of {@link RestService}.
 */
public class RestServiceGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 1000;

    private RestService<Book, Integer, List> bookService;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        Requestor requestor = Requestor.newInstance();

        requestor.register(BookJsonSerializer.getInstance());

        bookService = requestor.newRestService(
                "https://605740e1055dbd0017e8493a.mockapi.io/requestor/tests/books",
                Book.class,
                Integer.class,
                List.class);
        // The mockapi service requires us to explicitly inform the content type header
        bookService.setMediaType("application/json");
    }

    public void testPostBooks() {
        Book book = new Book(null, "RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));

        bookService.post(book).done(new DoneCallback<SerializedResponse>() {
            @Override
            public void onDone(SerializedResponse response) {
                assertNotNull(response);

                // The server should return a 201 status code
                assertEquals(Response.Status.CREATED, response.getStatus());

                // The response returns the created book in the server with the generated ID
                Book createdResource = response.getPayloadAs(Book.class);

                // Check if the return book has an ID
                assertNotNull(createdResource.getId());

                // Trigger delete test
                manualTestDeleteBook(createdResource.getId());
            }
        });
        delayTestFinish(TIMEOUT * 2);
    }

    public void testGetBooks() {
        // GET /books
        bookService.get().done(new ListDoneCallback<Book>() {
            public void onDone(List<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBooksWithParams() {
        // GET /books?id=20
        bookService.get("id", "20").done(new DoneCallback<Collection<Book>>() {
            @Override
            public void onDone(Collection<Book> result) {
                assertNotNull(result);
                assertEquals(1, result.size());
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBook() {
        // GET /books/1
        bookService.get(1).done(new DoneCallback<Book>() {
            public void onDone(Book result) {
                assertNotNull(result);
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testPutBook() {
        // PUT /books/2
        final Integer id = 2;
        final Book book = new Book(id, "Clean Code", "Robert C. Martin", new Date(1217552400000L));

        bookService.put(id, book).done(new DoneCallback<SerializedResponse>() {
            @Override
            public void onDone(SerializedResponse response) {
                assertNotNull(response);

                // The server should return a 201 status code
                assertEquals(Response.Status.OK, response.getStatus());

                // The response returns the created book in the server with the generated ID
                Book updatedResource = response.getPayloadAs(Book.class);

                // Check if the return book has an ID
                assertEquals(book, updatedResource);

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    private void manualTestDeleteBook(Integer createdId) {
        // DELETE /books/{createdId}
        bookService.delete(createdId).done(new DoneCallback<SerializedResponse>() {
            @Override
            public void onDone(SerializedResponse response) {
                assertNotNull(response);

                // The server should return a 200 status code
                assertEquals(Response.Status.OK, response.getStatus());

                finishTest();
            }
        });
    }
}
