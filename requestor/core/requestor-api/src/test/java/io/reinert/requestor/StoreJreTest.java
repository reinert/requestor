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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests of {@link SerializerManagerImpl}.
 */
public class StoreJreTest {

    private final Store store = new PersistentStore();

    @Test
    public void get_SetValue_ShouldReturnSetValueAndKeepIt() {
        int expected = 1;
        String key = "key";

        // Given
        store.put(key, expected);

        // When
        int returned = store.get(key);

        // Then
        assertEquals(expected, returned);
        assertTrue(store.has(key));
    }

    @Test
    public void remove_SetValue_ShouldRemoveIt() {
        boolean expected = true;
        String key = "key";

        // Given
        store.put(key, expected);

        // When
        boolean removed = store.remove(key);

        // Then
        assertTrue(removed);
        assertFalse(store.has(key));
    }
}
