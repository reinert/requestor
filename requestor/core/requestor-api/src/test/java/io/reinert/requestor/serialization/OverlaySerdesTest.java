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
package io.reinert.requestor.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.serialization.json.JsonSerdes;
import io.reinert.requestor.serialization.json.OverlaySerdes;

/**
 * Unit tests of {@link OverlaySerdes}.
 */
public class OverlaySerdesTest extends GWTTestCase {

    private final OverlaySerdes serdes = OverlaySerdes.getInstance();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorApiTest";
    }

    @SuppressWarnings("unchecked")
    public void testDeserializeCollection() throws Exception {
        DeserializationContext ctx = new DeserializationContext(JavaScriptObject.class) {
            @Override
            public <T> T getInstance(Class<T> type) {
                return (T) new ArrayList<Object>();
            }
        };

        String input = "[{\"name\":\"John Doe\",\"age\":31},{\"name\":\"Alice\",\"age\":27}]";

        JsArray<JavaScriptObject> expected = (JsArray<JavaScriptObject>) JavaScriptObject.createArray();
        expected.push(create("John Doe", 31));
        expected.push(create("Alice", 27));

        List<JavaScriptObject> output = serdes.deserialize(List.class, input, ctx);
        JsArray<JavaScriptObject> outputArray = (JsArray<JavaScriptObject>) JavaScriptObject.createArray();
        outputArray.push(output.get(0));
        outputArray.push(output.get(1));

        assertEquals(JsonSerdes.stringify(expected), JsonSerdes.stringify(outputArray));
    }

    public void testDeserializeValue() throws Exception {
        final String input = "{\"name\":\"John Doe\",\"age\":31}";
        final JavaScriptObject expected = create("John Doe", 31);

        final JavaScriptObject output = serdes.deserialize(input, null);

        assertEquals(JsonSerdes.stringify(expected), JsonSerdes.stringify(output));
    }

    public void testSerializeCollection() throws Exception {
        List<JavaScriptObject> input = Arrays.asList(create("John Doe", 31), create("Alice", 27));
        String expected = "[{\"name\":\"John Doe\",\"age\":31},{\"name\":\"Alice\",\"age\":27}]";

        String output = serdes.serialize(input, null);

        assertEquals(expected, output);
    }

    public void testSerializeValue() throws Exception {
        final JavaScriptObject input = create("John Doe", 31);
        final String expected = "{\"name\":\"John Doe\",\"age\":31}";

        final String output = serdes.serialize(input, null);

        assertEquals(expected, output);
    }

    static native JavaScriptObject create(String name, int age) /*-{
        return {name: name, age: age};
    }-*/;
}
