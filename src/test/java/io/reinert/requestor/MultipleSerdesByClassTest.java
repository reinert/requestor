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
package io.reinert.requestor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.FailCallback;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.test.books.Book;
import io.reinert.requestor.test.books.BookJsonSerdes;
import io.reinert.requestor.test.books.BookXmlSerdes;
import io.reinert.requestor.test.mock.ResponseMock;
import io.reinert.requestor.test.mock.ServerStub;

/**
 * @author Danilo Reinert
 */
public class MultipleSerdesByClassTest extends GWTTestCase {

    final String uri = "/book";

    final List<Book> bookList = new ArrayList<Book>(2);
    final Book firstBook = new Book(1, "RESTful Web Services", "Leonard Richardson");
    final Book secondBook = new Book(2, "Agile Software Development: Principles, Patterns and Practices",
            "Robert C. Martin");

    final String firstBookSerializedAsXml = "<book>" +
            "<id>1</id>" +
            "<title>RESTful Web Services</title>" +
            "<author>Leonard Richardson</author>" +
            "</book>";
    final String secondBookSerializedAsXml = "<book>" +
            "<id>2</id>" +
            "<title>Agile Software Development: Principles, Patterns and Practices</title>" +
            "<author>Robert C. Martin</author>" +
            "</book>";

    final String bookArraySerializedAsXml = "<books>" +
            firstBookSerializedAsXml + secondBookSerializedAsXml +
            "</books>";

    final String firstBookSerializedAsJson = "{\"id\":1,\"title\":\"RESTful Web Services\",\"author\":\"Leonard " +
            "Richardson\"}";

    final String secondBookSerializedAsJson = "{\"id\":2,\"title\":\"Agile Software Development: Principles, " +
            "Patterns and Practices\",\"author\":\"Robert C. Martin\"}";

    final String bookArraySerializedAsJson = "[" + firstBookSerializedAsJson + "," + secondBookSerializedAsJson + "]";

    final Serdes<Book> jsonSerdes = new BookJsonSerdes();
    final Serdes<Book> xmlSerdes = new BookXmlSerdes();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    @Override
    public void gwtSetUp() throws Exception {
        bookList.add(firstBook);
        bookList.add(secondBook);
    }

    public void testXmlDeserializingMatching() {
        prepareStub("application/xml", firstBookSerializedAsXml);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[3];

        requestor.request(uri).get(Book.class).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable throwable) {
                callbackCalled[0] = true;
            }
        }).done(new DoneCallback<Book>() {
            @Override
            public void onDone(Book book) {
                callbackCalled[1] = true;
                assertEquals(firstBook, book);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    public void testJsonDeserializingMatching() {
        prepareStub("application/json", firstBookSerializedAsJson);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[3];

        requestor.request(uri).get(Book.class).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable throwable) {
                callbackCalled[0] = true;
            }
        }).done(new DoneCallback<Book>() {
            @Override
            public void onDone(Book book) {
                callbackCalled[1] = true;
                assertEquals(firstBook, book);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    public void testXmlArrayDeserializingMatching() {
        prepareStub("application/xml", bookArraySerializedAsXml);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[3];

        requestor.request(uri).get(Book.class, List.class).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable throwable) {
                callbackCalled[0] = true;
            }
        }).done(new DoneCallback<Collection<Book>>() {
            @Override
            public void onDone(Collection<Book> books) {
                callbackCalled[1] = true;
                assertEquals(bookList, books);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    public void testJsonArrayDeserializingMatching() {
        prepareStub("application/json", bookArraySerializedAsJson);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[3];

        requestor.request(uri).get(Book.class, List.class).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable throwable) {
                callbackCalled[0] = true;
            }
        }).done(new DoneCallback<Collection<Book>>() {
            @Override
            public void onDone(Collection<Book> books) {
                callbackCalled[1] = true;
                assertEquals(bookList, books);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    public void testXmlSerializingMatching() {
        prepareStub("text/plain", "response ignored");
        final Requestor requestor = getRequestor();

        requestor.request(uri).contentType("application/xml").payload(firstBook).post();
        ServerStub.triggerPendingRequest();

        assertEquals(firstBookSerializedAsXml, ServerStub.getRequestData(uri).getData());
    }

    public void testXmlArraySerializingMatching() {
        prepareStub("text/plain", "response ignored");
        final Requestor requestor = getRequestor();

        requestor.request(uri).contentType("application/xml").payload(bookList).post();
        ServerStub.triggerPendingRequest();

        assertEquals(bookArraySerializedAsXml, ServerStub.getRequestData(uri).getData());
    }

    public void testJsonSerializingMatching() {
        prepareStub("text/plain", "response ignored");
        final Requestor requestor = getRequestor();

        requestor.request(uri).contentType("application/json").payload(firstBook).post();
        ServerStub.triggerPendingRequest();

        assertEquals(firstBookSerializedAsJson, ServerStub.getRequestData(uri).getData());
    }

    public void testJsonArraySerializingMatching() {
        prepareStub("text/plain", "response ignored");
        final Requestor requestor = getRequestor();

        requestor.request(uri).contentType("application/json").payload(bookList).post();
        ServerStub.triggerPendingRequest();

        assertEquals(bookArraySerializedAsJson, ServerStub.getRequestData(uri).getData());
    }

    private Requestor getRequestor() {
        final Requestor requestor = GWT.create(Requestor.class);
        requestor.addSerdes(jsonSerdes);
        requestor.addSerdes(xmlSerdes);
        return requestor;
    }

    private void prepareStub(String responseContentType, String serializedResponse) {
        ServerStub.clearStub();
        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader(responseContentType)));
    }
}
