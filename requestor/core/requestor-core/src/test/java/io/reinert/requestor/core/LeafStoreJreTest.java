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
package io.reinert.requestor.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests of {@link SerializerManagerImpl}.
 */
public class LeafStoreJreTest {

    private static final String KEY = "key";
    private static final int VALUE = 1;

    private final RootStore store = new RootStore(null);

    @Before
    public void setUp() {
        store.save(KEY, VALUE);
    }

    @Test
    public void get_SetValueInParent_ShouldReturnSetValue() {
        // Given
        LeafStore leafStore = new LeafStore(store, false, null);

        // When
        int returned = leafStore.retrieve(KEY);

        // Then
        assertEquals(VALUE, returned);
        assertTrue(leafStore.exists(KEY));
    }

    @Test
    public void remove_SetValueInParent_ShouldKeepIt() {
        // Given
        LeafStore leafStore = new LeafStore(store, false, null);

        // When
        boolean removed = leafStore.remove(KEY);

        // Then
        assertFalse(removed);
        assertTrue(leafStore.exists(KEY));
    }

    @Test
    public void remove_SetValue_ShouldRemoveIt() {
        int expected = 2;
        String key = "key2";

        // Given
        LeafStore leafStore = new LeafStore(store, false, null);
        leafStore.save(key, expected);

        // When
        boolean returned = leafStore.remove(key);

        // Then
        assertTrue(returned);
        assertFalse(leafStore.exists(key));
    }

    @Test
    public void remove_SetValueOverridingParent_ShouldRemoveLocalAndKeepParentValue() {
        int localExpected = 2;

        // Given
        LeafStore leafStore = new LeafStore(store, false, null);
        leafStore.save(KEY, localExpected);

        // When
        boolean removed = leafStore.remove(KEY);
        boolean parentRemoved = leafStore.remove(KEY);

        // Then
        assertTrue(removed);
        assertFalse(parentRemoved);
        assertTrue(leafStore.exists(KEY));
    }

    @Test
    public void remove_SetValueOverridingVolatileParentAsPersistent_ShouldRemoveLocalAndKeepParentValue() {
        int parentExpected = 2;
        int localExpected = 3;

        // Given
        LeafStore volatileParent = new LeafStore(store, false, null);
        volatileParent.save(KEY, parentExpected);

        LeafStore leafStore = new LeafStore(volatileParent, false, null);
        leafStore.save(KEY, localExpected);

        // When
        boolean removed = leafStore.remove(KEY);
        boolean parentRemoved = leafStore.remove(KEY);

        // Then
        assertTrue(removed);
        assertFalse(parentRemoved);
        assertTrue(volatileParent.exists(KEY));
    }
}
