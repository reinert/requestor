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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.HandlesSubTypes;
import io.reinert.requestor.serialization.Serializer;

/**
 * Unit tests for {@link io.reinert.requestor.rebind.JsonAutoBeanGenerator}.
 */
public class JsonAutoBeanGeneratorTest extends GWTTestCase {

    public static final String APP_JSON = "app*/json*";
    public static final String JAVASCRIPT = "*/javascript*";

    private SerializerManagerImpl serializerManager;
    private ProviderManagerImpl providerManager;

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorWithAutoBeanTest";
    }

    @Override
    public void gwtSetUp() throws Exception {
        serializerManager = new SerializerManagerImpl();
        providerManager = new ProviderManagerImpl();
        GeneratedJsonSerializerBinder.bind(serializerManager, providerManager);
    }

    public void testProviderShouldBeAvailableByProviderManager() {
        final Provider<Animal> animalProvider = providerManager.get(Animal.class);
        assertNotNull(animalProvider);

        Animal animal = animalProvider.getInstance();
        assertNotNull(animal);

        animal.setAge(2);
        animal.setName("Doug");
        assertEquals(new Integer(2), animal.getAge());
        assertEquals("Doug", animal.getName());
    }

    public void testSerializerShouldBeAvailableBySerializerManager() {
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");
        assertNotNull(serializer);
    }

    public void testDeserializerShouldBeAvailableBySerializerManager() {
        final Deserializer<Animal> deserializer = serializerManager.getDeserializer(Animal.class, "application/json");
        assertNotNull(deserializer);
    }

    // This test will prevent us to every time test the same behaviour for both Serializer and Deserializer
    // (since they are the same)
    public void testSerializerAndDeserializerShouldBeTheSameInstance() {
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");
        final Deserializer<Animal> deserializer = serializerManager.getDeserializer(Animal.class, "application/json");
        assertSame(serializer, deserializer);
    }

    public void testSerializerShouldSupportMediaTypeValuesFromJsonAnnotation() {
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");
        assertTrue(Arrays.equals(new String[]{APP_JSON, JAVASCRIPT}, serializer.mediaType()));
    }

    public void testSerializerShouldHandleAnnotatedType() {
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");
        assertEquals(Animal.class, serializer.handledType());
    }

    @SuppressWarnings("null")
    public void testSerializerShouldHandleAutoBeanProxyTypeAsImpl() {
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");

        assertTrue(serializer instanceof HandlesSubTypes);

        // We test if runtime class name contains generated class name, since in runtime '$1' is appended to the end of
        // the class name. SerializerManagerImpl already handle this issue for matching.
        final Animal autoBeanInstance = providerManager.get(Animal.class).getInstance();
        final String autoBeanRunTimeClassName = autoBeanInstance.getClass().getName();
        final String autoBeanGeneratedClassName = ((HandlesSubTypes) serializer).handledSubTypes()[0].getName();
        assertTrue(autoBeanRunTimeClassName.contains(autoBeanGeneratedClassName));
    }

    @SuppressWarnings("null")
    public void testSingleDeserialization() {
        // Given
        final Deserializer<Animal> deserializer = serializerManager.getDeserializer(Animal.class, "application/json");
        final Animal expected = providerManager.get(Animal.class).getInstance();
        expected.setName("Stuart");
        expected.setAge(3);

        final String input = "{\"name\":\"Stuart\",\"age\":3}";

        // When
        final Animal output = deserializer.deserialize(input, null);

        // Then
        assertTrue(isEqual(expected, output));
    }

    @SuppressWarnings({"null", "unchecked"})
    public void testDeserializationAsList() {
        // Given
        final Deserializer<Animal> deserializer = serializerManager.getDeserializer(Animal.class, "application/json");
        final Provider<Animal> animalProvider = providerManager.get(Animal.class);

        final Animal a0 = animalProvider.getInstance();
        a0.setName("Stuart");
        a0.setAge(3);
        final Animal a1 = animalProvider.getInstance();
        a1.setName("March");
        a1.setAge(5);

        final String input = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        // When
        List<Animal> output = deserializer.deserialize(List.class, input, null);

        // Then
        assertTrue(isEqual(a0, output.get(0)));
        assertTrue(isEqual(a1, output.get(1)));
    }

    @SuppressWarnings("null")
    public void testSingleSerialization() {
        // Given
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");

        final String expected = "{\"name\":\"Stuart\",\"age\":3}";

        final Animal input = providerManager.get(Animal.class).getInstance();
        input.setName("Stuart");
        input.setAge(3);

        // When
        final String output = serializer.serialize(input, null);

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings({"null", "unchecked"})
    public void testSerializationAsList() {
        // Given
        final Serializer<Animal> serializer = serializerManager.getSerializer(Animal.class, "application/json");
        final Provider<Animal> animalProvider = providerManager.get(Animal.class);

        final String expected = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        final Animal a0 = animalProvider.getInstance();
        a0.setName("Stuart");
        a0.setAge(3);
        final Animal a1 = animalProvider.getInstance();
        a1.setName("March");
        a1.setAge(5);
        List<Animal> input = Arrays.asList(a0, a1);

        // When
        String output = serializer.serialize(input, null);

        // Then
        assertEquals(expected, output);
    }

    @Json({APP_JSON, JAVASCRIPT})
    interface Animal {
        Integer getAge();
        String getName();
        void setAge(Integer age);
        void setName(String name);
    }

    private boolean isEqual(Animal a, Animal b) {
        return a.getName().equals(b.getName()) && a.getAge().equals(b.getAge());
    }
}
