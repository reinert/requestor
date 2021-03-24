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
public class StorageJreTest {

    private final Storage storage = new PersistentStorage();

    @Test
    public void get_SetValue_ShouldReturnSetValueAndKeepIt() {
        int expected = 1;
        String key = "key";

        // Given
        storage.set(key, expected);

        // When
        int returned = storage.get(key);

        // Then
        assertEquals(expected, returned);
        assertTrue(storage.has(key));
    }

    @Test
    public void pop_SetValue_ShouldReturnSetValueAndRemoveIt() {
        int expected = 1;
        String key = "key";

        // Given
        storage.set(key, expected);

        // When
        int returned = storage.pop(key);

        // Then
        assertEquals(expected, returned);
        assertFalse(storage.has(key));
    }
}
