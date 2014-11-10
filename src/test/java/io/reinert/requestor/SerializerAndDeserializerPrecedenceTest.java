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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.requestor.header.ContentTypeHeader;
import io.reinert.requestor.test.mock.ResponseMock;
import io.reinert.requestor.test.mock.ServerStub;

/**
 * @author Danilo Reinert
 */
public class SerializerAndDeserializerPrecedenceTest extends GWTTestCase {

    final String uri = "/string";
    final String string = "text response";
    final String serializedResponseAsJson = "\"text response\"";
    final String serializedResponseAsText = "text response";

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    public void testJsonDeserializing() {
        prepareStub("application/json", serializedResponseAsJson);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[2];

        requestor.request(uri).get(String.class).done(new DoneCallback<String>() {
            public void onDone(String result) {
                callbackCalled[1] = true;
                assertEquals(string, result);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    public void testTextPlainDeserializing() {
        prepareStub("text/plain", serializedResponseAsText);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[2];

        requestor.request(uri).get(String.class).done(new DoneCallback<String>() {
            public void onDone(String result) {
                callbackCalled[1] = true;
                assertEquals(string, result);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    public void testNotMappedDeserializing() {
        // As TextDeserializer matches */*, this response should be deserialized by it.
        prepareStub("content-type/not-mapped", serializedResponseAsText);
        final Requestor requestor = getRequestor();

        final boolean[] callbackCalled = new boolean[2];

        requestor.request(uri).get(String.class).done(new DoneCallback<String>() {
            public void onDone(String result) {
                callbackCalled[1] = true;
                assertEquals(string, result);
            }
        });
        ServerStub.triggerPendingRequest();

        assertFalse(callbackCalled[0]);
        assertTrue(callbackCalled[1]);
    }

    private Requestor getRequestor() {
        return GWT.create(Requestor.class);
    }

    private void prepareStub(String responseContentType, String serializedResponse) {
        ServerStub.clearStub();
        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader(responseContentType)));
    }
}
