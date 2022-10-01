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
import java.util.Date;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.RestService;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;

import io.reinert.requestor.core.callback.ResponseCallback;

import junit.framework.TestCase;

/**
 * Integration tests of {@link RestService}.
 */
public class RestServiceGwtTest extends GWTTestCase {

    private static final int TIMEOUT = 10000;
    private static final int DELAY = 3500;

    private RestService<Book, Integer> bookService;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.gwt.RequestorGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        Session session = Requestor.newSession();

        session.register(BookJsonSerializer.getInstance());

        bookService = RestService
                .of(Book.class, Integer.class, List.class)
                .at("https://605740e1055dbd0017e8493a.mockapi.io/requestor/tests/books")
                .on(session);

        // The mockapi service requires us to explicitly inform the content type header
        bookService.setMediaType("application/json");

        // Delay requests to avoid 429 Too Many Requests from mockapi.io
        bookService.setDelay(DELAY);
    }

    public void testPostBooks() {
        Book book = new Book(null, "RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));

        bookService.post(book).onSuccess(new PayloadResponseCallback<Book>() {
            public void execute(Book returnedBook, Response response) {
                assertNotNull(response);

                // The server should return a 201 status code
                TestCase.assertEquals(Status.CREATED, response.getStatus());

                // Check if the return book has an ID
                assertNotNull(returnedBook.getId());

                // Trigger delete test
                manualTestDeleteBook(returnedBook.getId());
            }
        }).onFail(new ResponseCallback() {
            public void execute(Response response) throws Throwable {
                System.out.println(">>>>>>>>>>>> POST FAIL");
                System.out.println(response.getStatus());
            }
        }).onError(new ExceptionCallback() {
            public void execute(RequestException exception) throws Throwable {
                System.out.println(">>>>>>>>>>>> POST ERROR");
                exception.printStackTrace();
            }
        });
        delayTestFinish(TIMEOUT * 2);
    }

    public void testGetBooks() {
        // GET /books
        bookService.get().onSuccess(new PayloadCallback<Collection<Book>>() {
            public void execute(Collection<Book> result) {
                assertNotNull(result);
                assertFalse(result.isEmpty());
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBooksWithParams() {
        // GET /books?id=20
        bookService.get("id", 5).onSuccess(new PayloadCallback<Collection<Book>>() {
            public void execute(Collection<Book> result) {
                assertNotNull(result);
                assertEquals(1, result.size());
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBook() {
        // GET /books/1
        bookService.get(1).onSuccess(new PayloadCallback<Book>() {
            public void execute(Book result) {
                assertNotNull(result);
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testPatchBook() {
        // PATCH /books/2
        final Integer id = 2;
        final Book book = new Book(id, "Clean Code", "Robert C. Martin", new Date(1217552400000L));

        bookService.patch(id, book).onSuccess(new PayloadResponseCallback<Book>() {
            public void execute(Book returnedBook, Response response) {
                assertNotNull(response);

                // The server should return a 201 status code
                assertEquals(Status.OK, response.getStatus());

                // Check if the return book has an ID
                assertEquals(book, returnedBook);

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testPutBook() {
        // PUT /books/2
        final Integer id = 2;
        final Book book = new Book(id, "Clean Code", "Robert C. Martin", new Date(1217552400000L));

        bookService.put(id, book).onSuccess(new PayloadResponseCallback<Book>() {
            public void execute(Book returnedBook, Response response) {
                assertNotNull(response);

                // The server should return a 201 status code
                assertEquals(Status.OK, response.getStatus());

                // Check if the return book has an ID
                assertEquals(book, returnedBook);

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    private void manualTestDeleteBook(Integer createdId) {
        // DELETE /books/{createdId}
        bookService.delete(createdId).onSuccess(new PayloadResponseCallback<Void>() {
            public void execute(Void unused, Response response) {
                assertNotNull(response);

                // The server should return a 200 status code
                assertEquals(Status.OK, response.getStatus());

                finishTest();
            }
        }).onFail(new ResponseCallback() {
            public void execute(Response response) throws Throwable {
                System.out.println(">>>>>>>>>>>> DELETE FAIL");
                System.out.println(response.getStatus());
            }
        }).onError(new ExceptionCallback() {
            public void execute(RequestException exception) throws Throwable {
                System.out.println(">>>>>>>>>>>> DELETE ERROR");
                exception.printStackTrace();
            }
        });
    }
}
