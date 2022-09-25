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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests of {@link SerializerManagerImpl}.
 */
public class StoreJreTest {

    private final Store store = new RootStore();

    @Test
    public void get_SetValue_ShouldReturnSetValueAndKeepIt() {
        int expected = 1;
        String key = "key";

        // Given
        store.save(key, expected);

        // When
        int returned = store.retrieve(key);

        // Then
        assertEquals(expected, returned);
        assertTrue(store.exists(key));
    }

    @Test
    public void remove_SetValue_ShouldRemoveIt() {
        boolean expected = true;
        String key = "key";

        // Given
        store.save(key, expected);

        // When
        boolean removed = store.remove(key);

        // Then
        assertTrue(removed);
        assertFalse(store.exists(key));
    }

    @Test
    public void onSaved_save_ShouldTriggerSavedCallback() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.onSaved(key, new Store.SaveCallback() {
            public void execute(Store.SaveEvent event) {
                called.set(true);
                assertNull(event.getOldData());
                assertEquals(data, event.getNewData());
            }
        });

        // When
        store.save(key, data);

        // Then
        assertTrue(called.get());
    }

    @Test
    public void onSaved_saveAfterSave_ShouldTriggerSavedCallbackWithOldData() {
        final String key = "key";
        final Object oldData = new Object();
        final Object newData = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.save(key, oldData);
        store.onSaved(key, new Store.SaveCallback() {
            public void execute(Store.SaveEvent event) {
                called.set(true);
                assertEquals(oldData, event.getOldData());
                assertEquals(newData, event.getNewData());
            }
        });

        // When
        store.save(key, newData);

        // Then
        assertTrue(called.get());
    }

    @Test
    public void onRemoved_remove_ShouldTriggerRemovedCallback() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.save(key, data);
        store.onRemoved(key, new Store.RemoveCallback() {
            public void execute(Store.RemoveEvent event) {
                called.set(true);
                assertEquals(data, event.getOldData());
            }
        });

        // When
        store.remove(key);

        // Then
        assertTrue(called.get());
    }
}
