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

import io.reinert.requestor.core.BaseService;
import io.reinert.requestor.core.Request;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.RestService;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.uri.Uri;

import junit.framework.TestCase;

/**
 * Integration tests of {@link RestService}.
 */
public class BaseServiceGwtTest extends GWTTestCase {

    private static class BookService extends BaseService {

        public BookService(Session session) {
            super(session, "https://605740e1055dbd0017e8493a.mockapi.io/requestor/tests/books");
        }

        public Request<Collection<Book>> getBooks(String... authors) {
            Uri uri = getUriBuilder()
                    .queryParam("author", (String[]) authors) // append ?author={author}
                    .build();
            return req(uri).get(List.class, Book.class);
        }

        public Request<Book> getBookById(Integer id) {
            Uri uri = getUriBuilder()
                    .segment(id) // add a path segment with the book id like /api/books/123
                    .build();
            return req(uri).get(Book.class);
        }

        public Request<Book> createBook(Book book) {
            Uri uri = getUriBuilder().build();
            return req(uri).payload(book).post(Book.class);
        }

        public Request<Void> updateBook(Integer id, Book book) {
            Uri uri = getUriBuilder()
                    .segment(id) // add a path segment with the book id like /api/books/123
                    .build();
            return req(uri).payload(book).put();
        }

        public Request<Void> deleteBook(Integer id) {
            Uri uri = getUriBuilder()
                    .segment(id) // add a path segment with the book id like /api/books/123
                    .build();
            return req(uri).delay(getDelay() * 2).delete();
        }
    }

    private static final int TIMEOUT = 8000;
    private static final int DELAY = 2500;

    private BookService bookService;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.gwt.RequestorGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        Session session = Requestor.newSession();

        session.register(BookJsonSerializer.getInstance());

        bookService = new BookService(session);

        // The mockapi service requires us to explicitly inform the content type header
        bookService.setMediaType("application/json");

        // Delay requests to avoid 429 Too Many Requests from mockapi.io
        bookService.setDelay(DELAY);
    }

    public void testPostBooks() {
        final Book book = new Book(null, "RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));

        bookService.createBook(book).onSuccess(new PayloadCallback<Book>() {
            public void execute(final Book created) {
                assertNotNull(created);

                // Trigger delete test
                manualTestDeleteBook(created.getId());
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
        bookService.getBooks().onSuccess(new PayloadCallback<Collection<Book>>() {
            public void execute(Collection<Book> books) {
                assertNotNull(books);
                assertFalse(books.isEmpty());
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBooksWithParams() {
        final String author = "Robert";

        // GET /books?author=Robert
        bookService.getBooks(author).onSuccess(new PayloadCallback<Collection<Book>>() {
            public void execute(Collection<Book> books) {
                assertNotNull(books);

                for (Book book : books) {
                    assertTrue(book.getAuthor().contains(author));
                }

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBookById() {
        // GET /books/1
        bookService.getBookById(1).onSuccess(new PayloadCallback<Book>() {
            public void execute(Book result) {
                assertNotNull(result);
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void tesUpdateBook() {
        // PUT /books/2
        final Integer id = 2;
        final Book book = new Book(id, "Clean Code", "Robert C. Martin", new Date(1217552400000L));

        bookService.updateBook(id, book).onStatus(200, new ResponseCallback() {
            public void execute(Response response) {
                assertNotNull(response);

                // The server should return a 200 status code
                TestCase.assertEquals(Status.OK, response.getStatus());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    private void manualTestDeleteBook(Integer createdId) {
        // DELETE /books/{createdId}
        bookService.deleteBook(createdId).onStatus(200, new ResponseCallback() {
            public void execute(Response response) {
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
