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
package io.reinert.requestor.gwt.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;

/**
 * Unit tests of {@link OverlaySerializer}.
 */
public class OverlaySerializerGwtTest extends GWTTestCase {

    private final OverlaySerializer serializer = new OverlaySerializer();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.gwt.RequestorGwtTest";
    }

    @SuppressWarnings("unchecked")
    public void testDeserializeCollection() throws Exception {
        DeserializationContext ctx = new DeserializationContext(null, JavaScriptObject.class) {
            @Override
            public <T> T getInstance(Class<T> type) {
                return (T) new ArrayList<Object>();
            }

            @Override
            public boolean hasProvider(Class<?> type) {
                return true;
            }
        };

        String input = "[{\"name\":\"John Doe\",\"age\":31},{\"name\":\"Alice\",\"age\":27}]";

        JsArray<JavaScriptObject> expected = (JsArray<JavaScriptObject>) JavaScriptObject.createArray();
        expected.push(create("John Doe", 31));
        expected.push(create("Alice", 27));

        List<JavaScriptObject> output = serializer.deserialize(List.class, new TextSerializedPayload(input), ctx);
        JsArray<JavaScriptObject> outputArray = (JsArray<JavaScriptObject>) JavaScriptObject.createArray();
        outputArray.push(output.get(0));
        outputArray.push(output.get(1));

        assertEquals(JsonSerializer.stringify(expected), JsonSerializer.stringify(outputArray));
    }

    public void testDeserializeValue() throws Exception {
        final String input = "{\"name\":\"John Doe\",\"age\":31}";
        final JavaScriptObject expected = create("John Doe", 31);

        final JavaScriptObject output = serializer.deserialize(new TextSerializedPayload(input), null);

        assertEquals(JsonSerializer.stringify(expected), JsonSerializer.stringify(output));
    }

    public void testSerializeCollection() throws Exception {
        List<JavaScriptObject> input = Arrays.asList(create("John Doe", 31), create("Alice", 27));
        String expected = "[{\"name\":\"John Doe\",\"age\":31},{\"name\":\"Alice\",\"age\":27}]";

        String output = serializer.serialize(input, null).asString();

        assertEquals(expected, output);
    }

    public void testSerializeValue() throws Exception {
        final JavaScriptObject input = create("John Doe", 31);
        final String expected = "{\"name\":\"John Doe\",\"age\":31}";

        final String output = serializer.serialize(input, null).asString();

        assertEquals(expected, output);
    }

    static native JavaScriptObject create(String name, int age) /*-{
        return {name: name, age: age};
    }-*/;
}
