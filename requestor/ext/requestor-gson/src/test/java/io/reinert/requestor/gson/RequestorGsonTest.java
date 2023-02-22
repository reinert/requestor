/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.gson;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;

import io.reinert.requestor.annotations.MediaType;
import io.reinert.requestor.core.AsyncRunner;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.SerializationModule;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.Deserializer;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.gson.annotations.GsonSerializationModule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Integration tests for requestor gwt-jackson processor.
 *
 * @author Danilo Reinert
 */
public class RequestorGsonTest {

    static final String APP_JSON = "app*/json*";
    static final String JAVASCRIPT = "*/javascript*";

    @MediaType({APP_JSON, JAVASCRIPT})
    @GsonSerializationModule(Animal.class)
    interface TestSerializationModule extends SerializationModule { }

    private Session session;

    @Before
    public void setUp() {
        session = new Session(Mockito.mock(AsyncRunner.class) , Mockito.mock(RequestDispatcher.Factory.class));
        session.register(TestSerializationModule.class);
    }

    @Test
    public void testSerializerShouldBeAvailableBySerializerManager() {
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        assertNotNull(serializer);
    }

    @Test
    public void testDeserializerShouldBeAvailableBySerializerManager() {
        final Deserializer<Animal> deserializer = session.getDeserializer(Animal.class, "application/json");
        assertNotNull(deserializer);
    }

    // This test will prevent us to every time test the same behaviour for both Serializer and Deserializer
    // (since they are the same)
    @Test
    public void testSerializerAndDeserializerShouldBeTheSameInstance() {
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        final Deserializer<Animal> deserializer = session.getDeserializer(Animal.class, "application/json");
        assertSame(serializer, deserializer);
    }

    @Test
    public void testSerializerShouldSupportMediaTypeValuesFromMediaTypeAnnotation() {
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        assertArrayEquals(new String[]{APP_JSON, JAVASCRIPT}, serializer.mediaType());
    }

    @Test
    public void testSerializerShouldHandleAnnotatedType() {
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        assertEquals(Animal.class, serializer.handledType());
    }

    @SuppressWarnings("null")
    @Test
    public void testSingleDeserialization() {
        // Given
        final Deserializer<Animal> deserializer = session.getDeserializer(Animal.class, "application/json");
        final Animal expected = new Animal("Stuart", 3);
        final String input = "{\"name\":\"Stuart\",\"age\":3}";

        // When
        final Animal output = deserializer.deserialize(new TextSerializedPayload(input), getDeserializationContext());

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings({"null", "unchecked"})
    @Test
    public void testDeserializationAsList() {
        // Given
        final Deserializer<Animal> deserializer = session.getDeserializer(Animal.class, "application/json");
        final List<Animal> expected = Arrays.asList(new Animal("Stuart", 3), new Animal("March", 5));
        final String input = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";

        // When
        List<Animal> output = (List<Animal>) deserializer.deserialize(List.class, new TextSerializedPayload(input),
                getDeserializationCollectionContext());

        // Then
        assertEquals(expected, output);
    }

    @SuppressWarnings({"null", "unchecked"})
    @Test
    public void testEmptyArrayDeserializationAsList() {
        // Given
        final Deserializer<Animal> deserializer = session.getDeserializer(Animal.class, "application/json");
        final List<Animal> expected = Collections.emptyList();
        final String input = "[]";

        // When
        List<Animal> output = (List<Animal>) deserializer.deserialize(List.class, new TextSerializedPayload(input),
                getDeserializationCollectionContext());

        // Then
        assertEquals(expected, output);
    }

    @Test
    public void testSingleSerialization() {
        // Given
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        final String expected = "{\"name\":\"Stuart\",\"age\":3}";
        final Animal input = new Animal("Stuart", 3);

        // When
        final String output = serializer.serialize(input, getSerializationContext()).asString();

        // Then
        assertEquals(expected, output);
    }

    @Test
    public void testSerializationWithFilter() {
        // Given
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        final String expected = "[{\"name\":\"Stuart\"},{\"name\":\"March\"}]";
        List<Animal> input = Arrays.asList(new Animal("Stuart", 3), new Animal("March", 5));

        // When
        String output = serializer.serialize(input, getSerializationCollectionContext("name")).asString();

        // Then
        assertEquals(expected, output);
    }

    @Test
    public void testSerializationAsList() {
        // Given
        final Serializer<Animal> serializer = session.getSerializer(Animal.class, "application/json");
        final String expected = "[{\"name\":\"Stuart\",\"age\":3},{\"name\":\"March\",\"age\":5}]";
        List<Animal> input = Arrays.asList(new Animal("Stuart", 3), new Animal("March", 5));

        // When
        String output = serializer.serialize(input, getSerializationCollectionContext()).asString();

        // Then
        assertEquals(expected, output);
    }

    private DeserializationContext getDeserializationContext() {
        return new DeserializationContext("UTF-8", Animal.class) {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T getInstance(Class<T> type) {
                return (T) new Gson();
            }
        };
    }

    private DeserializationContext getDeserializationCollectionContext() {
        return new DeserializationContext("UTF-8", List.class, Animal.class) {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T getInstance(Class<T> type) {
                return (T) new Gson();
            }
        };
    }

    private SerializationContext getSerializationContext(String... fields) {
        return new SerializationContext("UTF-8", new HashSet<>(Arrays.asList(fields)), Animal.class) {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T getInstance(Class<T> type) {
                return (T) new Gson();
            }
        };
    }

    private SerializationContext getSerializationCollectionContext(String... fields) {
        return new SerializationContext("UTF-8", new HashSet<>(Arrays.asList(fields)), List.class, Animal.class) {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T getInstance(Class<T> type) {
                return (T) new Gson();
            }
        };
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
}
