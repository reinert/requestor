package io.reinert.requestor.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reinert.requestor.serialization.json.JsonStringSerdes;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class JsonStringSerdesTest {

    private final JsonStringSerdes serdes = JsonStringSerdes.getInstance();

    @Test
    public void deserializeCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        Mockito.when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[\"some\",\"any\"]";
        Collection<String> expected = Arrays.asList("some", "any");

        @SuppressWarnings("unchecked")
        Collection<String> output = serdes.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    public void deserializeValue() throws Exception {
        assertEquals("some", serdes.deserialize("\"some\"", null));
    }

    @Test
    public void serializeCollection() throws Exception {
        Collection<String> input = Arrays.asList("some", "any");
        String expected = "[\"some\",\"any\"]";

        String output = serdes.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeValue() throws Exception {
        assertEquals("\"some\"", serdes.serialize("some", null));
    }
}
