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
import io.reinert.requestor.serialization.SerializationException;
import io.reinert.requestor.serialization.Serializer;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link SerdesManager}.
 */
public class SerdesManagerTest {

    private final SerdesManager manager = new SerdesManager();

    @Test(expected = SerializationException.class)
    public void getDeserializer_UnregisteredClassAndMediaType_ShouldThrowSerializationException() {
        manager.getDeserializer(Object.class, "unregistered/type");
    }

    @Test(expected = SerializationException.class)
    public void getSerializer_UnregisteredClassAndMediaType_ShouldThrowSerializationException() {
        manager.getSerializer(Object.class, "unregistered/type");
    }

    @Test
    public void getDeserializer_RegisteredClassAndMediaType_ShouldReturnRegisteredDeserializer() {
        // Given
        Deserializer expected = mock(Deserializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.addDeserializer(expected);

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
        manager.addSerializer(expected);

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
        manager.addDeserializer(expected);

        Deserializer noise = mock(Deserializer.class);
        when(noise.handledType()).thenReturn(Object.class);
        when(noise.mediaType()).thenReturn(new String[]{"different/type"});
        manager.addDeserializer(noise);

        // When
        Deserializer output = manager.getDeserializer(Object.class, "custom/type");

        // Then
        assertTrue(expected != noise);
        assertTrue(expected == output);
    }

    @Test
    public void getSerializer_MultipleSerializersByClassWithDistinctMediaTypes_ShouldMatchByMediaType() {
        // Given
        Serializer expected = mock(Serializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.addSerializer(expected);

        Serializer noise = mock(Serializer.class);
        when(noise.handledType()).thenReturn(Object.class);
        when(noise.mediaType()).thenReturn(new String[]{"different/type"});
        manager.addSerializer(noise);

        // When
        Serializer output = manager.getSerializer(Object.class, "custom/type");

        // Then
        assertTrue(expected != noise);
        assertTrue(expected == output);
    }

    @Test
    public void getDeserializer_MultipleDeserializersByClassWithIntersectingMediaTypes_ShouldMatchByPrecedence() {
        // Given
        Deserializer expected = mock(Deserializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.addDeserializer(expected);

        Deserializer noise = mock(Deserializer.class);
        when(noise.handledType()).thenReturn(Object.class);
        when(noise.mediaType()).thenReturn(new String[]{"*/type"});
        manager.addDeserializer(noise);

        Deserializer noise2 = mock(Deserializer.class);
        when(noise2.handledType()).thenReturn(Object.class);
        when(noise2.mediaType()).thenReturn(new String[]{"custom/*"});
        manager.addDeserializer(noise2);

        Deserializer noise3 = mock(Deserializer.class);
        when(noise3.handledType()).thenReturn(Object.class);
        when(noise3.mediaType()).thenReturn(new String[]{"*/*"});
        manager.addDeserializer(noise3);

        // When
        Deserializer output = manager.getDeserializer(Object.class, "custom/type");

        // Then
        assertTrue(expected != noise);
        assertTrue(expected != noise2);
        assertTrue(expected != noise3);
        assertTrue(expected == output);
    }

    @Test
    public void getSerializer_MultipleSerializersByClassWithIntersectingMediaTypes_ShouldMatchByPrecedence() {
        // Given
        Serializer expected = mock(Serializer.class);
        when(expected.handledType()).thenReturn(Object.class);
        when(expected.mediaType()).thenReturn(new String[]{"custom/type"});
        manager.addSerializer(expected);

        Serializer noise = mock(Serializer.class);
        when(noise.handledType()).thenReturn(Object.class);
        when(noise.mediaType()).thenReturn(new String[]{"*/type"});
        manager.addSerializer(noise);

        Serializer noise2 = mock(Serializer.class);
        when(noise2.handledType()).thenReturn(Object.class);
        when(noise2.mediaType()).thenReturn(new String[]{"custom/*"});
        manager.addSerializer(noise2);

        Serializer noise3 = mock(Serializer.class);
        when(noise3.handledType()).thenReturn(Object.class);
        when(noise3.mediaType()).thenReturn(new String[]{"*/*"});
        manager.addSerializer(noise3);

        // When
        Serializer output = manager.getSerializer(Object.class, "custom/type");

        // Then
        assertTrue(expected != noise);
        assertTrue(expected != noise2);
        assertTrue(expected != noise3);
        assertTrue(expected == output);
    }
}
