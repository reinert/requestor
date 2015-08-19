/*
 * Copyright 2015 Danilo Reinert
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
import java.util.Collections;
import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.Serializer;

/**
 * Integration tests for Requestor gwt-jackson processor.
 *
 * @author Danilo Reinert
 */
public class IntegrationGwtTest extends GWTTestCase {

//    public static final String APP_JSON = "app*/json*";
//    public static final String JAVASCRIPT = "*/javascript*";

    private SerdesManagerImpl serdesManager = new SerdesManagerImpl();
    private final ProviderManagerImpl providerManager = new ProviderManagerImpl();

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorWithGwtJacksonTest";
    }

    @Override
    public void gwtSetUp() throws Exception {
        bind(new TestSerializationModuleImpl());
    }

    public void testSerializerShouldBeAvailableBySerdesManager() {
        final Serializer<Animal> serializer = serdesManager.getSerializer(Animal.class, "application/json");
        assertNotNull(serializer);
    }

    public void testDeserializerShouldBeAvailableBySerdesManager() {
        final Deserializer<Animal> deserializer = serdesManager.getDeserializer(Animal.class, "application/json");
        assertNotNull(deserializer);
    }

    // This test will prevent us to every time test the same behaviour for both Serializer and Deserializer
    // (since they are the same)
    public void testSerializerAndDeserializerShouldBeTheSameInstance() {
        final Serializer<Animal> serializer = serdesManager.getSerializer(Animal.class, "application/json");
        final Deserializer<Animal> deserializer = serdesManager.getDeserializer(Animal.class, "application/json");
        assertSame(serializer, deserializer);
    }

//    public void testSerializerShouldSupportMediaTypeValuesFromJsonAnnotation() {
//        final Serializer<Animal> serializer = serdesManager.getSerializer(Animal.class, "application/json");
//        assertTrue(Arrays.equals(new String[]{APP_JSON, JAVASCRIPT}, serializer.mediaType()));
//    }

    public void testSerializerShouldHandleAnnotatedType() {
        final Serializer<Animal> serializer = serdesManager.getSerializer(Animal.class, "application/json");
        assertEquals(Animal.class, serializer.handledType());
    }

    @SuppressWarnings("null")
    public void testSingleDeserialization() {
        // Given
        final Deserializer<Animal> deserializer = serdesManager.getDeserializer(Animal.class, "application/json");
        final Animal expected = new Animal("Stuart", 3);
        final String input = "{\"name\":\"Stuart\",\"age\":3}";

        // When
        final Animal output = deserializer.deserialize(input, null);

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings({"null", "unchecked"})
    public void testDeserializationAsList() {
        // Given
        final Deserializer<Animal> deserializer = serdesManager.getDeserializer(Animal.class, "application/json");
        final List<Animal> expected = Arrays.asList(new Animal("Stuart", 3), new Animal("March", 5));
        final String input = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        // When
        List<Animal> output = (List<Animal>) deserializer.deserialize(List.class, input, null);

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings({"null", "unchecked"})
    public void testEmptyArrayDeserializationAsList() {
        // Given
        final Deserializer<Animal> deserializer = serdesManager.getDeserializer(Animal.class, "application/json");
        final List<Animal> expected = Collections.emptyList();
        final String input = "[]";

        // When
        List<Animal> output = (List<Animal>) deserializer.deserialize(List.class, input, null);

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings("null")
    public void testSingleSerialization() {
        // Given
        final Serializer<Animal> serializer = serdesManager.getSerializer(Animal.class, "application/json");
        final String expected = "{\"name\":\"Stuart\",\"age\":3}";
        final Animal input = new Animal("Stuart", 3);

        // When
        final String output = serializer.serialize(input, null);

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings({"null", "unchecked"})
    public void testSerializationAsList() {
        // Given
        final Serializer<Animal> serializer = serdesManager.getSerializer(Animal.class, "application/json");
        final String expected = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";
        List<Animal> input = Arrays.asList(new Animal("Stuart", 3), new Animal("March", 5));

        // When
        String output = serializer.serialize(input, null);

        // Then
        assertEquals(expected, output);
    }

    private void bind(SerializationModule module) {
        for (Serdes<?> serdes : module.getSerdes()) {
            serdesManager.register(serdes);
        }
        for (Provider<?> provider : module.getProviders()) {
            providerManager.register(provider);
        }
    }

    public static class Animal {

        private String name;
        private Integer age;

        public Animal() {
        }

        public Animal(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Animal)) {
                return false;
            }

            final Animal animal = (Animal) o;

            if (age != null ? !age.equals(animal.age) : animal.age != null) {
                return false;
            }
            if (name != null ? !name.equals(animal.name) : animal.name != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (age != null ? age.hashCode() : 0);
            return result;
        }
    }

    @JsonSerializationModule(Animal.class)
    interface TestSerializationModule extends SerializationModule {}
}
