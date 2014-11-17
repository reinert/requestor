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

import io.reinert.gdeferred.DoneCallback;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.serialization.SerializationException;
import io.reinert.requestor.test.mock.ResponseMock;
import io.reinert.requestor.test.mock.ServerStub;
import io.reinert.requestor.test.model.Person;
import io.reinert.requestor.test.model.PersonSerdes;

/**
 * @author Danilo Reinert
 */
public class ContentTypeAcceptPatternsTest extends GWTTestCase {

    final String uri = "/person";
    final Person person = new Person(1, "John Doe", 6.3, new Date(329356800));
    final String serializedResponse = "{\"id\":1, \"name\":\"John Doe\", \"weight\":6.3, \"birthday\":329356800}";
    final PersonSerdes personSerdes = new PersonSerdes();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    public void testWildcardBothParts() {
        personSerdes.setContentTypePatterns("*/*");

        prepareStub("application/json");
        final Requestor requestor = getRequestor();

        final boolean[] callbackSuccessCalled = new boolean[1];

        try {
            requestor.request(uri).payload(person).post(Person.class).done(new DoneCallback<Person>() {
                @Override
                public void onDone(Person person) {
                    callbackSuccessCalled[0] = true;
                }
            });
            ServerStub.triggerPendingRequest();
        } catch (SerializationException e) {
            // This piece of code should not be called
            assertNull(e);
        }

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testWildcardFirstPart() {
        personSerdes.setContentTypePatterns("*/json");

        prepareStub("application/json");
        final Requestor requestor = getRequestor();

        final boolean[] callbackSuccessCalled = new boolean[1];

        try {
            requestor.request(uri).contentType("anything/json").accept("anything/json").payload(person)
                    .post(Person.class).done(new DoneCallback<Person>() {
                @Override
                public void onDone(Person person) {
                    callbackSuccessCalled[0] = true;
                }
            });
            ServerStub.triggerPendingRequest();
        } catch (SerializationException e) {
            // This piece of code should not be called
            assertNull(e);
        }

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testWildcardLastPart() {
        personSerdes.setContentTypePatterns("application/*");

        prepareStub("application/json");
        final Requestor requestor = getRequestor();

        final boolean[] callbackSuccessCalled = new boolean[1];

        try {
            requestor.request(uri)
                    .contentType("application/anything")
                    .accept("application/anything")
                    .payload(person).post(Person.class).done(new DoneCallback<Person>() {
                @Override
                public void onDone(Person person) {
                    callbackSuccessCalled[0] = true;
                }
            });
            ServerStub.triggerPendingRequest();
        } catch (SerializationException e) {
            // This piece of code should not be called
            assertNull(e);
        }

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testContentTypeInvalidBothParts() {
        personSerdes.setContentTypePatterns("app/js");

        prepareStub("application/json");
        Requestor requestor = getRequestor();

        final boolean[] callbackSuccessCalled = new boolean[1];

        try {
            requestor.request(uri)
                    .contentType("application/json")
                    .accept("application/json")
                    .payload(person).post(Person.class)
                    .done(new DoneCallback<Person>() {
                        @Override
                        public void onDone(Person person) {
                            callbackSuccessCalled[0] = true;
                        }
                    });
            ServerStub.triggerPendingRequest();
        } catch (SerializationException e) {
            assertNotNull(e);
        }

        assertFalse(callbackSuccessCalled[0]);

        personSerdes.setContentTypePatterns("application/json");

        prepareStub("application/json");
        requestor = getRequestor();

        try {
            requestor.request(uri)
                    .contentType("app/js")
                    .accept("application/json")
                    .payload(person).post(Person.class)
                    .done(new DoneCallback<Person>() {
                        @Override
                        public void onDone(Person person) {
                            callbackSuccessCalled[0] = true;
                        }
                    });
            ServerStub.triggerPendingRequest();
        } catch (SerializationException e) {
            assertNotNull(e);
        }

        assertFalse(callbackSuccessCalled[0]);
    }

    private Requestor getRequestor() {
        final Requestor requestor = GWT.create(Requestor.class);
        requestor.addSerdes(personSerdes);
        return requestor;
    }

    private void prepareStub(String responseContentType) {
        ServerStub.clearStub();
        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader(responseContentType)));
    }
}
