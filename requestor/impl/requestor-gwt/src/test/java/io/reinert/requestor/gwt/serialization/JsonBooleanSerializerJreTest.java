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
import java.util.Collection;
import java.util.List;

import io.reinert.requestor.core.serialization.DeserializationContext;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests of {@link JsonBooleanSerializer}.
 */
public class JsonBooleanSerializerJreTest {

    private final JsonBooleanSerializer serializer = new JsonBooleanSerializer();

    @Test
    public void deserializeCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        Mockito.when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[true,false,false,true,false]";
        Collection<Boolean> expected = Arrays.asList(true, false, false, true, false);

        @SuppressWarnings("unchecked")
        Collection<Boolean> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    public void deserializeValue() throws Exception {
        assertEquals(true, serializer.deserialize("true", null));
        assertEquals(false, serializer.deserialize("false", null));
    }

    @Test
    public void serializeCollection() throws Exception {
        Collection<Boolean> input = Arrays.asList(true, false, false, true, false);
        String expected = "[true,false,false,true,false]";

        String output = serializer.serialize(input, null).asText();

        assertEquals(expected, output);
    }

    @Test
    public void serializeValue() throws Exception {
        assertEquals("true", serializer.serialize(true, null).asText());
        assertEquals("false", serializer.serialize(false, null).asText());
    }
}
