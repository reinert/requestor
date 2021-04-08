/*
 * Copyright 2021 Danilo Reinert
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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests of {@link SerializerManagerImpl}.
 */
public class VolatileStoreJreTest {

    private static final String KEY = "key";
    private static final int VALUE = 1;

    private final PersistentStore store = new PersistentStore();

    @Before
    public void setUp() {
        store.set(KEY, VALUE);
    }

    @Test
    public void get_SetValueInParent_ShouldReturnSetValue() {
        // Given
        VolatileStore volatileStore = new VolatileStore(store);

        // When
        int returned = volatileStore.get(KEY);

        // Then
        assertEquals(VALUE, returned);
        assertTrue(volatileStore.has(KEY));
    }

    @Test
    public void pop_SetValueInParent_ShouldReturnSetValueAndKeepIt() {
        // Given
        VolatileStore volatileStore = new VolatileStore(store);

        // When
        int returned = volatileStore.pop(KEY);

        // Then
        assertEquals(VALUE, returned);
        assertTrue(volatileStore.has(KEY));
    }

    @Test
    public void pop_SetValue_ShouldReturnSetValueAndRemoveIt() {
        int expected = 2;
        String key = "key2";

        // Given
        VolatileStore volatileStore = new VolatileStore(store);
        volatileStore.set(key, expected);

        // When
        int returned = volatileStore.pop(key);

        // Then
        assertEquals(expected, returned);
        assertFalse(volatileStore.has(key));
    }

    @Test
    public void pop_SetValueOverridingParent_ShouldReturnLocalSetValueAndKeepParentValue() {
        int localExpected = 2;

        // Given
        VolatileStore volatileStore = new VolatileStore(store);
        volatileStore.set(KEY, localExpected);

        // When
        int returned = volatileStore.pop(KEY);
        int parentValue = volatileStore.pop(KEY);

        // Then
        assertEquals(localExpected, returned);
        assertEquals(VALUE, parentValue);
        assertTrue(volatileStore.has(KEY));
    }

    @Test
    public void pop_SetValueOverridingVolatileParentAsPersistent_ShouldReturnLocalSetValueAndKeepParentValue() {
        int parentExpected = 2;
        int localExpected = 3;

        // Given
        VolatileStore volatileParent = new VolatileStore(store);
        volatileParent.set(KEY, parentExpected);

        VolatileStore volatileStore = new VolatileStore(volatileParent);
        volatileStore.set(KEY, localExpected);

        // When
        int returned = volatileStore.pop(KEY);
        int parentValue = volatileStore.pop(KEY);

        // Then
        assertEquals(localExpected, returned);
        assertEquals(parentExpected, parentValue);
        assertTrue(volatileParent.has(KEY));
    }
}
