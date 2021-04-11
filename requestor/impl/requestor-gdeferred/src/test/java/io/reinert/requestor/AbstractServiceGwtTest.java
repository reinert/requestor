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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.impl.gdeferred.ListDoneCallback;
import io.reinert.requestor.impl.gdeferred.RequestDoneCallback;
import io.reinert.requestor.impl.gdeferred.RequestFailCallback;
import io.reinert.requestor.uri.Uri;

/**
 * Integration tests of {@link RestService}.
 */
public class AbstractServiceGwtTest extends GWTTestCase {

    private static class BookService extends AbstractService {

        public BookService(Requestor requestor) {
            super(requestor, "https://605740e1055dbd0017e8493a.mockapi.io/requestor/tests/books");
        }

        public Promise<Collection<Book>> getBooks(String... authors) {
            Uri uri = getUriBuilder()
                    .queryParam("author", authors) // append ?author={author}
                    .build();
            return request(uri).get(Book.class, List.class);
        }

        public Promise<Book> getBookById(Integer id) {
            Uri uri = getUriBuilder()
                    .segment(id) // add a path segment with the book id like /api/books/123
                    .build();
            return request(uri).get(Book.class);
        }

        public Promise<Book> createBook(Book book) {
            Uri uri = getUriBuilder().build();
            return request(uri).payload(book).post(Book.class);
        }

        public Promise<Void> updateBook(Integer id, Book book) {
            Uri uri = getUriBuilder()
                    .segment(id) // add a path segment with the book id like /api/books/123
                    .build();
            return request(uri).payload(book).put();
        }

        public Promise<Void> deleteBook(Integer id) {
            Uri uri = getUriBuilder()
                    .segment(id) // add a path segment with the book id like /api/books/123
                    .build();
            return request(uri).delete();
        }
    }

    private static final int TIMEOUT = 6000;
    private static final int DELAY = 3000;

    private BookService bookService;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorByGDeferredTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        Requestor requestor = Requestor.newInstance();

        requestor.register(BookJsonSerializer.getInstance());

        bookService = new BookService(requestor);

        // The mockapi service requires us to explicitly inform the content type header
        bookService.setMediaType("application/json");

        // Delay requests to avoid 429 Too Many Requests from mockapi.io
        bookService.setDelay(DELAY);
    }

    public void testPostBooks() {
        final Book book = new Book(null, "RESTful Web Services", "Leonard Richardson", new Date(1179795600000L));

        bookService.createBook(book).done(new RequestDoneCallback<Book>() {
            @Override
            public void onDone(final Book created) {
                assertNotNull(created);

                // Trigger delete test
                manualTestDeleteBook(created.getId());
            }
        });

        delayTestFinish(TIMEOUT * 2);
    }

    public void testGetBooks() {
        // GET /books
        bookService.getBooks().done(new ListDoneCallback<Book>() {
            public void onDone(List<Book> books) {
                assertNotNull(books);
                assertFalse(books.isEmpty());
                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBooksWithParams() {
        // GET /books?author=Leonard
        bookService.getBooks("Leonard").done(new RequestDoneCallback<Collection<Book>>() {
            @Override
            public void onDone(Collection<Book> books) {
                assertNotNull(books);

                for (Book book : books) {
                    assertTrue(book.getAuthor().contains("Leonard"));
                }

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    public void testGetBookById() {
        // GET /books/1
        bookService.getBookById(1).done(new RequestDoneCallback<Book>() {
            public void onDone(Book result) {
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

        bookService.updateBook(id, book).done(new RequestDoneCallback<Void>() {
            @Override
            public void onDone(Response<Void> response) {
                assertNotNull(response);

                // The server should return a 200 status code
                assertEquals(Status.OK, response.getStatus());

                finishTest();
            }
        });
        delayTestFinish(TIMEOUT);
    }

    private void manualTestDeleteBook(Integer createdId) {
        // DELETE /books/{createdId}
        bookService.deleteBook(createdId).done(new RequestDoneCallback<Void>() {
            @Override
            public void onDone(Response<Void> response) {
                assertNotNull(response);

                // The server should return a 200 status code
                assertEquals(Status.OK, response.getStatus());

                finishTest();
            }
        }).fail(new RequestFailCallback() {
            public void onFail(Throwable throwable) {
                GWT.log(">>>>>>>>>>>>>>>> DELETE");
                GWT.log(throwable.getMessage());
                GWT.log(UnsuccessfulResponseException.cast(throwable).getStatus().toString());
                GWT.log(UnsuccessfulResponseException.cast(throwable).getResponse().getPayloadAs(String.class));
                GWT.log(">>>>>>>>>>>>>>>>");
            }
        });
    }
}
