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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.test.mock.ResponseMock;
import io.reinert.requestor.test.mock.ServerStub;
import io.reinert.requestor.test.model.Person;
import io.reinert.requestor.test.model.PersonSerdes;

/**
 * @author Danilo Reinert
 */
public class SerializerAndDeserializerMatchTest extends GWTTestCase {

    final String uri = "/person";
    final Person person = new Person(1, "John Doe", 6.3, new Date(329356800));
    final String serializedResponse = "{\"id\":1, \"name\":\"John Doe\", \"weight\":6.3, \"birthday\":329356800}";
    final PersonSerdes personSerdes = new PersonSerdes();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

//    public void testWildcardBothParts() {
//        personSerdes.setAcceptPatterns("*/*");
//        personSerdes.setContentTypePatterns("*/*");
//
//        prepareStub("application/json");
//        final Requestor requestory = getRequestory();
//
//        final boolean[] callbackSuccessCalled = new boolean[1];
//
//        try {
//            requestory.request(Person.class, Person.class).path(uri).post(person, new AsyncCallback<Person>() {
//                @Override
//                public void onFailure(Throwable caught) {
//                }
//
//                @Override
//                public void onSuccess(Person result) {
//                    callbackSuccessCalled[0] = true;
//                }
//            });
//        } catch (SerializationException e) {
//            // This piece of code should not be called
//            assertNull(e);
//        }
//
//        assertTrue(callbackSuccessCalled[0]);
//    }
//
//    public void testWildcardFirstPart() {
//        personSerdes.setAcceptPatterns("*/json");
//        personSerdes.setContentTypePatterns("*/json");
//
//        prepareStub("application/json");
//        final Requestor requestory = getRequestory();
//
//        final boolean[] callbackSuccessCalled = new boolean[1];
//
//        try {
//            requestory.request(Person.class, Person.class).path(uri)
//                    .contentType("anything/json")
//                    .accept("anything/json")
//                    .post(person, new AsyncCallback<Person>() {
//                        @Override
//                        public void onFailure(Throwable caught) {
//                        }
//
//                        @Override
//                        public void onSuccess(Person result) {
//                            callbackSuccessCalled[0] = true;
//                        }
//                    });
//        } catch (SerializationException e) {
//            // This piece of code should not be called
//            assertNull(e);
//        }
//
//        assertTrue(callbackSuccessCalled[0]);
//    }
//
//    public void testWildcardLastPart() {
//        personSerdes.setAcceptPatterns("application/*");
//        personSerdes.setContentTypePatterns("application/*");
//
//        prepareStub("application/json");
//        final Requestor requestory = getRequestory();
//
//        final boolean[] callbackSuccessCalled = new boolean[1];
//
//        try {
//            requestory.request(Person.class, Person.class).path(uri)
//                    .contentType("application/anything")
//                    .accept("application/anything")
//                    .post(person, new AsyncCallback<Person>() {
//                        @Override
//                        public void onFailure(Throwable caught) {
//                        }
//
//                        @Override
//                        public void onSuccess(Person result) {
//                            callbackSuccessCalled[0] = true;
//                        }
//                    });
//        } catch (SerializationException e) {
//            // This piece of code should not be called
//            assertNull(e);
//        }
//
//        assertTrue(callbackSuccessCalled[0]);
//    }
//
//    public void testAcceptInvalidFirstPart() {
//        personSerdes.setAcceptPatterns("anything/json");
//        personSerdes.setContentTypePatterns("application/json");
//
//        prepareStub("application/json");
//        Requestor requestory = getRequestory();
//
//        final boolean[] callbackCalled = new boolean[2];
//
//        requestory.request(Person.class, Person.class).path(uri)
//                .accept("application/json")
//                .contentType("application/json")
//                .post(person, new AsyncCallback<Person>() {
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        callbackCalled[1] = true;
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//                        callbackCalled[0] = true;
//                    }
//                });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//
//        callbackCalled[1] = false;
//        personSerdes.setAcceptPatterns("application/json");
//
//        prepareStub("anything/json");
//        requestory = getRequestory();
//
//        requestory.request(Person.class, Person.class).path(uri)
//                .accept("anything/json")
//                .contentType("application/json")
//                .post(person, new AsyncCallback<Person>() {
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        callbackCalled[1] = true;
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//                        callbackCalled[0] = true;
//                    }
//                });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//    }
//
//    public void testAcceptInvalidLastPart() {
//        personSerdes.setAcceptPatterns("application/xml");
//        personSerdes.setContentTypePatterns("application/json");
//
//        prepareStub("application/json");
//        Requestor requestory = getRequestory();
//
//        final boolean[] callbackCalled = new boolean[2];
//
//        requestory.request(Person.class, Person.class).path(uri)
//                .accept("application/json")
//                .contentType("application/json")
//                .post(person, new AsyncCallback<Person>() {
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        callbackCalled[1] = true;
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//                        callbackCalled[0] = true;
//                    }
//                });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//
//        callbackCalled[1] = false;
//        personSerdes.setAcceptPatterns("application/json");
//
//        prepareStub("application/xml");
//        requestory = getRequestory();
//
//        requestory.request(Person.class, Person.class).path(uri)
//                .accept("application/xml")
//                .contentType("application/json")
//                .post(person, new AsyncCallback<Person>() {
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        callbackCalled[1] = true;
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//                        callbackCalled[0] = true;
//                    }
//                });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//    }
//
//    public void testContentTypeInvalidBothParts() {
//        personSerdes.setAcceptPatterns("application/json");
//        personSerdes.setContentTypePatterns("app/js");
//
//        prepareStub("application/json");
//        Requestor requestory = getRequestory();
//
//        final boolean[] callbackSuccessCalled = new boolean[1];
//
//        try {
//            requestory.request(Person.class, Person.class).path(uri)
//                    .accept("application/json")
//                    .contentType("application/json")
//                    .post(person, new AsyncCallback<Person>() {
//                        @Override
//                        public void onFailure(Throwable caught) {
//                        }
//
//                        @Override
//                        public void onSuccess(Person result) {
//                            callbackSuccessCalled[0] = true;
//                        }
//                    });
//        } catch (SerializationException e) {
//            assertNotNull(e);
//        }
//
//        assertFalse(callbackSuccessCalled[0]);
//
//        personSerdes.setContentTypePatterns("application/json");
//
//        prepareStub("application/json");
//        requestory = getRequestory();
//
//        try {
//            requestory.request(Person.class, Person.class).path(uri)
//                    .accept("application/json")
//                    .contentType("app/js")
//                    .post(person, new AsyncCallback<Person>() {
//                        @Override
//                        public void onFailure(Throwable caught) {
//                        }
//
//                        @Override
//                        public void onSuccess(Person result) {
//                            callbackSuccessCalled[0] = true;
//                        }
//                    });
//        } catch (SerializationException e) {
//            assertNotNull(e);
//        }
//
//        assertFalse(callbackSuccessCalled[0]);
//    }

    private Requestor getRequestor() {
        final Requestor requestory = GWT.create(Requestor.class);
        requestory.addSerdes(personSerdes);
        return requestory;
    }

    private void prepareStub(String responseContentType) {
        ServerStub.clearStub();
        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader(responseContentType)));
    }
}
