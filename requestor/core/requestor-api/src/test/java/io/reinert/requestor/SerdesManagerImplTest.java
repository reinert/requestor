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

import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serializer;

import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link SerdesManagerImpl}.
 */
public class SerdesManagerImplTest {

    private final SerdesManagerImpl manager = new SerdesManagerImpl();

    @Test
    public void getDeserializer_RegisteredClassAndMediaType_ShouldReturnRegisteredDeserializer() {
        // Given
        Deserializer expected = mock(Deserializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.register(expected);

        // When
        Deserializer output = manager.getDeserializer(Object.class, "custom/type");

        // Then
        assertTrue(expected == output);
    }

    @Test
    public void getSerializer_RegisteredClassAndMediaType_ShouldReturnRegisteredSerializer() {
        // Given
        Serializer expected = mock(Serializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.register(expected);

        // When
        Serializer output = manager.getSerializer(Object.class, "custom/type");

        // Then
        assertTrue(expected == output);
    }

    @Test
    public void getDeserializer_MultipleDeserializersByClassWithDistinctMediaTypes_ShouldMatchByMediaType() {
        // Given
        Deserializer expected = mock(Deserializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.register(expected);

        Deserializer noise = mock(Deserializer.class);
        when(noise.handledType()).thenReturn(Object.class);
        when(noise.mediaType()).thenReturn(new String[]{"different/type"});
        manager.register(noise);

        // When
        Deserializer output = manager.getDeserializer(Object.class, "custom/type");

        // Then
        assertNotSame(expected, noise);
        assertSame(expected, output);
    }

    @Test
    public void getSerializer_MultipleSerializersByClassWithDistinctMediaTypes_ShouldMatchByMediaType() {
        // Given
        Serializer expected = mock(Serializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.register(expected);

        Serializer noise = mock(Serializer.class);
        when(noise.handledType()).thenReturn(Object.class);
        when(noise.mediaType()).thenReturn(new String[]{"different/type"});
        manager.register(noise);

        // When
        Serializer output = manager.getSerializer(Object.class, "custom/type");

        // Then
        assertNotSame(expected, noise);
        assertSame(expected, output);
    }

    @Test
    public void getDeserializer_MultipleDeserializersByClassWithIntersectingMediaTypes_ShouldMatchByPrecedence() {
        // Given
        Deserializer expected = mock(Deserializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.register(expected);

        Deserializer expected2 = mock(Deserializer.class);
        when(expected2.handledType()).thenReturn(Object.class);
        when(expected2.mediaType()).thenReturn(new String[]{"*/type"});
        manager.register(expected2);

        Deserializer expected3 = mock(Deserializer.class);
        when(expected3.handledType()).thenReturn(Object.class);
        when(expected3.mediaType()).thenReturn(new String[]{"custom/*"});
        manager.register(expected3);

        Deserializer expected4 = mock(Deserializer.class);
        when(expected4.handledType()).thenReturn(Object.class);
        when(expected4.mediaType()).thenReturn(new String[]{"*/*"});
        manager.register(expected4);

        // When
        Deserializer output = manager.getDeserializer(Object.class, "custom/type");
        Deserializer output2 = manager.getDeserializer(Object.class, "other/type");
        Deserializer output3 = manager.getDeserializer(Object.class, "custom/other");
        Deserializer output4 = manager.getDeserializer(Object.class, "any/other");

        // Then
        assertSame(expected, output);
        assertSame(expected2, output2);
        assertSame(expected3, output3);
        assertSame(expected4, output4);
    }

    @Test
    public void getSerializer_MultipleSerializersByClassWithIntersectingMediaTypes_ShouldMatchByPrecedence() {
        // Given
        Serializer expected = mock(Serializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.register(expected);

        Serializer expected2 = mock(Serializer.class);
        when(expected2.handledType()).thenReturn(Object.class);
        when(expected2.mediaType()).thenReturn(new String[]{"*/type"});
        manager.register(expected2);

        Serializer expected3 = mock(Serializer.class);
        when(expected3.handledType()).thenReturn(Object.class);
        when(expected3.mediaType()).thenReturn(new String[]{"custom/*"});
        manager.register(expected3);

        Serializer expected4 = mock(Serializer.class);
        when(expected4.handledType()).thenReturn(Object.class);
        when(expected4.mediaType()).thenReturn(new String[]{"*/*"});
        manager.register(expected4);

        // When
        Serializer output = manager.getSerializer(Object.class, "custom/type");
        Serializer output2 = manager.getSerializer(Object.class, "other/type");
        Serializer output3 = manager.getSerializer(Object.class, "custom/other");
        Serializer output4 = manager.getSerializer(Object.class, "any/other");

        // Then
        assertSame(expected, output);
        assertSame(expected2, output2);
        assertSame(expected3, output3);
        assertSame(expected4, output4);
    }
}
