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
public class TransientStoreJreTest {

    private static final String KEY = "key";
    private static final int VALUE = 1;

    private final SessionStore store = new SessionStore();

    @Before
    public void setUp() {
        store.save(KEY, VALUE);
    }

    @Test
    public void get_SetValueInParent_ShouldReturnSetValue() {
        // Given
        TransientStore transientStore = new TransientStore(store);

        // When
        int returned = transientStore.get(KEY);

        // Then
        assertEquals(VALUE, returned);
        assertTrue(transientStore.has(KEY));
    }

    @Test
    public void remove_SetValueInParent_ShouldKeepIt() {
        // Given
        TransientStore transientStore = new TransientStore(store);

        // When
        boolean removed = transientStore.delete(KEY);

        // Then
        assertFalse(removed);
        assertTrue(transientStore.has(KEY));
    }

    @Test
    public void remove_SetValue_ShouldRemoveIt() {
        int expected = 2;
        String key = "key2";

        // Given
        TransientStore transientStore = new TransientStore(store);
        transientStore.save(key, expected);

        // When
        boolean returned = transientStore.delete(key);

        // Then
        assertTrue(returned);
        assertFalse(transientStore.has(key));
    }

    @Test
    public void remove_SetValueOverridingParent_ShouldRemoveLocalAndKeepParentValue() {
        int localExpected = 2;

        // Given
        TransientStore transientStore = new TransientStore(store);
        transientStore.save(KEY, localExpected);

        // When
        boolean removed = transientStore.delete(KEY);
        boolean parentRemoved = transientStore.delete(KEY);

        // Then
        assertTrue(removed);
        assertFalse(parentRemoved);
        assertTrue(transientStore.has(KEY));
    }

    @Test
    public void remove_SetValueOverridingVolatileParentAsPersistent_ShouldRemoveLocalAndKeepParentValue() {
        int parentExpected = 2;
        int localExpected = 3;

        // Given
        TransientStore volatileParent = new TransientStore(store);
        volatileParent.save(KEY, parentExpected);

        TransientStore transientStore = new TransientStore(volatileParent);
        transientStore.save(KEY, localExpected);

        // When
        boolean removed = transientStore.delete(KEY);
        boolean parentRemoved = transientStore.delete(KEY);

        // Then
        assertTrue(removed);
        assertFalse(parentRemoved);
        assertTrue(volatileParent.has(KEY));
    }
}