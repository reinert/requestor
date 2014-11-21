package io.reinert.requestor.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reinert.requestor.serialization.json.JsonBooleanSerdes;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class JsonBooleanSerdesTest {

    private final JsonBooleanSerdes serdes = JsonBooleanSerdes.getInstance();

    @Test
    public void serializeValue() throws Exception {
        assertEquals("true", serdes.serialize(true, null));
        assertEquals("false", serdes.serialize(false, null));
    }

    @Test
    public void serializeCollection() throws Exception {
        // Given
        Collection<Boolean> input = Arrays.asList(true, false, false, true, false);

        // When
        String output = serdes.serialize(input, null);

        // Then
        String expected = "[true,false,false,true,false]";
        assertEquals(expected, output);
    }

    @Test
    public void deserializeValue() throws Exception {
        assertEquals(true, serdes.deserialize("true", null));
        assertEquals(false, serdes.deserialize("false", null));
    }

    @Test
    public void deserializeCollection() throws Exception {
        // Prepare mock
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        Mockito.when(context.getInstance(List.class)).thenReturn(new ArrayList());

        // Given
        String input = "[true,false,false,true,false]";

        // When
        @SuppressWarnings("unchecked")
        Collection<Boolean> output = serdes.deserialize(List.class, input, context);

        // Then
        Collection<Boolean> expected = Arrays.asList(true, false, false, true, false);
        assertEquals(expected, output);
    }
}
