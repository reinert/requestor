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
        store.put(KEY, VALUE);
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
    public void remove_SetValueInParent_ShouldKeepIt() {
        // Given
        VolatileStore volatileStore = new VolatileStore(store);

        // When
        boolean removed = volatileStore.remove(KEY);

        // Then
        assertFalse(removed);
        assertTrue(volatileStore.has(KEY));
    }

    @Test
    public void remove_SetValue_ShouldRemoveIt() {
        int expected = 2;
        String key = "key2";

        // Given
        VolatileStore volatileStore = new VolatileStore(store);
        volatileStore.put(key, expected);

        // When
        boolean returned = volatileStore.remove(key);

        // Then
        assertTrue(returned);
        assertFalse(volatileStore.has(key));
    }

    @Test
    public void remove_SetValueOverridingParent_ShouldRemoveLocalAndKeepParentValue() {
        int localExpected = 2;

        // Given
        VolatileStore volatileStore = new VolatileStore(store);
        volatileStore.put(KEY, localExpected);

        // When
        boolean removed = volatileStore.remove(KEY);
        boolean parentRemoved = volatileStore.remove(KEY);

        // Then
        assertTrue(removed);
        assertFalse(parentRemoved);
        assertTrue(volatileStore.has(KEY));
    }

    @Test
    public void remove_SetValueOverridingVolatileParentAsPersistent_ShouldRemoveLocalAndKeepParentValue() {
        int parentExpected = 2;
        int localExpected = 3;

        // Given
        VolatileStore volatileParent = new VolatileStore(store);
        volatileParent.put(KEY, parentExpected);

        VolatileStore volatileStore = new VolatileStore(volatileParent);
        volatileStore.put(KEY, localExpected);

        // When
        boolean removed = volatileStore.remove(KEY);
        boolean parentRemoved = volatileStore.remove(KEY);

        // Then
        assertTrue(removed);
        assertFalse(parentRemoved);
        assertTrue(volatileParent.has(KEY));
    }
}
