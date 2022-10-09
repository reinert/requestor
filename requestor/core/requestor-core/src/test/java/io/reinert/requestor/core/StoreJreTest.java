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
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests of {@link SerializerManagerImpl}.
 */
public class StoreJreTest {

    private final Store store = new RootStore(new AsyncRunner() {
        @Override
        public void run(Runnable runnable, long delayMillis) {
            runnable.run();
        }

        @Override
        public void sleep(long millis) {
            // no-op
        }

        @Override
        public void shutdown() {
            // no-op
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public Lock getLock() {
            return null;
        }
    });

    @Test
    public void get_SetValue_ShouldReturnSetValueAndKeepIt() {
        int expected = 1;
        String key = "key";

        // Given
        store.save(key, expected);

        // When
        int returned = store.getValue(key);

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
        Store.Data removed = store.remove(key);

        // Then
        assertNotNull(removed);
        assertFalse(store.exists(key));
    }

    @Test
    public void onSaved_save_ShouldTriggerSavedHandler() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.onSaved(key, new Store.Handler() {
            public void execute(Store.Event event) {
                called.set(true);
                assertNull(event.getOldData());
                assertEquals(data, event.getNewData().getValue());
            }
        });

        // When
        store.save(key, data);

        // Then
        assertTrue(called.get());
    }

    @Test
    public void onSaved_saveAfterSave_ShouldTriggerSavedHandlerWithOldData() {
        final String key = "key";
        final Object oldData = new Object();
        final Object newData = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.save(key, oldData);
        store.onSaved(key, new Store.Handler() {
            public void execute(Store.Event event) {
                called.set(true);
                assertEquals(oldData, event.getOldData().getValue());
                assertEquals(newData, event.getNewData().getValue());
            }
        });

        // When
        store.save(key, newData);

        // Then
        assertTrue(called.get());
    }

    @Test
    public void onRemoved_remove_ShouldTriggerRemovedHandler() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.save(key, data);
        store.onRemoved(key, new Store.Handler() {
            public void execute(Store.Event event) {
                called.set(true);
                assertEquals(data, event.getOldData().getValue());
            }
        });

        // When
        store.remove(key);

        // Then
        assertTrue(called.get());
    }

    @Test
    public void saveWithTtl_ShouldTriggerOnExpired() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.onExpired(key, new Store.Handler() {
            public void execute(Store.Event event) {
                called.set(true);
                assertEquals(data, event.getOldData().getValue());
            }
        });

        // When
        store.save(key, data, 1);

        // Then
        assertTrue(called.get());
        assertFalse(store.exists(key));
    }

    @Test
    public void cancel_ShouldNotTriggerHandler() {
        final String key = "key";
        final Object firstData = new Object();
        final Object secondData = new Object();
        final Object thirdData = new Object();
        final AtomicInteger calls = new AtomicInteger(0);

        // Given
        store.onSaved(key, new Store.Handler() {
            public void execute(Store.Event event) {
                // Then
                assertEquals(firstData, event.getNewData().getValue());

                cancel();
            }
        });
        store.onSaved(key, new Store.Handler() {
            public void execute(Store.Event event) {
                // Then
                if (calls.incrementAndGet() == 1) {
                    assertEquals(firstData, event.getNewData().getValue());
                    return;
                }

                if (calls.get() == 2) {
                    assertEquals(firstData, event.getOldData().getValue());
                    assertEquals(secondData, event.getNewData().getValue());
                    return;
                }

                assertEquals(secondData, event.getOldData().getValue());
                assertEquals(thirdData, event.getNewData().getValue());
            }
        });

        // When
        store.save(key, firstData);
        store.save(key, secondData);
        store.save(key, thirdData);
    }

    @Test
    public void refresh_ShouldUpdateRefreshedAtWithOriginalTtl() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.onExpired(key, new Store.Handler() {
            public void execute(Store.Event event) {
                called.set(true);
                Store.Data expiredData = event.getOldData();

                assertEquals(expiredData.getCreatedAt(), expiredData.getRefreshedAt());
                assertEquals(0, expiredData.getTimesRefreshed());

                event.getStore().refresh(key);

                assertEquals(1, expiredData.getTimesRefreshed());
            }
        });

        // When
        store.save(key, data, 1);

        // Then
        assertTrue(called.get());
        assertFalse(store.exists(key));
    }

    @Test
    public void refreshWithTtl_ShouldUpdateRefreshedAtWithTtl() {
        final String key = "key";
        final Object data = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);

        // Given
        store.onExpired(key, new Store.Handler() {
            public void execute(Store.Event event) {
                called.set(true);
                Store.Data expiredData = event.getOldData();

                assertEquals(expiredData.getCreatedAt(), expiredData.getRefreshedAt());
                assertEquals(0, expiredData.getTimesRefreshed());

                event.getStore().refresh(key, 5);

                assertEquals(1, expiredData.getTimesRefreshed());
                assertEquals(5, expiredData.getTtl());
            }
        });

        // When
        store.save(key, data, 1);

        // Then
        assertTrue(called.get());
        assertFalse(store.exists(key));
    }
}
