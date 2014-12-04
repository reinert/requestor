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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.serialization.json.OverlaySerdes;

import org.turbogwt.core.collections.JsArrayList;
import org.turbogwt.core.util.Overlays;

/**
 * Unit tests of {@link OverlaySerdes}.
 */
public class OverlaySerdesTest extends GWTTestCase {

    private final OverlaySerdes serdes = OverlaySerdes.getInstance();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    public void testDeserializeCollection() throws Exception {
        String input = "[{\"name\":\"John Doe\",\"age\":31},{\"name\":\"Alice\",\"age\":27}]";
        JsArrayList<JavaScriptObject> expected = new JsArrayList<JavaScriptObject>(create("John Doe", 31),
                create("Alice", 27));

        @SuppressWarnings("unchecked")
        JsArrayList<JavaScriptObject> output = serdes.deserialize(JsArrayList.class, input, null);

        assertEquals(Overlays.stringify(expected.asJsArray()), Overlays.stringify(output.asJsArray()));
    }

    public void testDeserializeValue() throws Exception {
        final String input = "{\"name\":\"John Doe\",\"age\":31}";
        final JavaScriptObject expected = create("John Doe", 31);

        final JavaScriptObject output = serdes.deserialize(input, null);

        assertEquals(Overlays.stringify(expected), Overlays.stringify(output));
    }

    public void testSerializeCollection() throws Exception {
        JsArrayList<JavaScriptObject> input = new JsArrayList<JavaScriptObject>(create("John Doe", 31),
                create("Alice", 27));
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
