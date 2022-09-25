/*
 * Copyright 2021-2022 Danilo Reinert
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

import java.util.Objects;

/**
 * A place to save/retrieve any object by key.
 *
 * @author Danilo Reinert
 */
public interface Store extends Saver {

    enum Level {
        PARENT,
        ROOT;
    }

    class Data {
        private final Object value;
        private final long ttl;
        private final long createdAt;

        public Data(Object value, long ttl) {
            this.value = value;
            this.ttl = ttl;
            createdAt = System.currentTimeMillis();
        }

        public Object getValue() {
            return value;
        }

        public long getTtl() {
            return ttl;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public boolean isExpired() {
            return ttl > 0L && System.currentTimeMillis() > createdAt + ttl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return Objects.equals(value, data.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    interface Event {
        String getKey();
        <T> T getOldData();
        <T> T getNewData();

        class Impl implements Event {
            private final String key;
            private final Object oldData;
            private final Object newData;

            public Impl(String key, Object oldData) {
                this.key = key;
                this.oldData = oldData;
                this.newData = null;
            }

            public Impl(String key, Object oldData, Object newData) {
                this.key = key;
                this.oldData = oldData;
                this.newData = newData;
            }

            @Override
            public String getKey() {
                return key;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T getOldData() {
                return (T) oldData;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T getNewData() {
                return (T) newData;
            }
        }
    }

    interface Callback {
        void execute(Event event);
    }

    /**
     * Queries an object associated with the given key.
     *
     * @param key The key associated with the data
     * @param <T> The type to typecast the object returned
     * @return The object associated with the key. <code>Null</code> otherwise.
     */
    <T> T retrieve(String key);

    /**
     * Saves the value into the store associated with the key.
     * Being a request scope store, the data will be available during the request/response lifecycle only.
     *
     * @param key   A key to associate the data
     * @param value The data to be persisted
     */
    Store save(String key, Object value);

    /**
     * Saves the value into the store associated with the key.
     * Being a request scope store, the data will be available during the request/response lifecycle only.
     * If you want to persist it in the immediate parent store, set the level param to <code>Level.PARENT</code>.
     * If you want to persist it in the root store, set the level param to <code>Level.ROOT</code>.
     *
     * @param key   A key to associate the data
     * @param value The data to be persisted
     * @param level Whether the data should be persisted in the underlying stores or not
     */
    Store save(String key, Object value, Level level);

    /**
     * Saves the value into the store associated with the key.
     * Being a request scope store, the data will be available during the request/response lifecycle only.
     * If you want to persist it in the immediate parent store, set the level param to <code>Level.PARENT</code>.
     * If you want to persist it in the root store, set the level param to <code>Level.ROOT</code>.
     *
     * @param key   A key to associate the data
     * @param value The data to be persisted
     * @param ttl   Time to live, i.e., the period when the data will still be valid
     * @param level Whether the data should be persisted in the underlying stores or not
     */
    Store save(String key, Object value, long ttl, Level level);

    /**
     * Saves the value into the store associated with the key during the TTL period.
     *
     * @param key   A key to associate the data
     * @param value The data to be persisted
     * @param ttl   Time to live, i.e., the period when the data will still be valid
     */
    Store save(String key, Object value, long ttl);

    /**
     * Checks if there's an object associated with the given key.
     *
     * @param key The key associated with the object
     * @return <code>True</code> if there's any data associated with the key. <code>False</code> otherwise.
     */
    boolean exists(String key);

    /**
     * Checks if there's an object associated with the given key and if it's equals to the given value.
     *
     * @param key   The key associated with the object
     * @param value The value to compare equality
     * @return <code>True</code> if there's an object equals to the given value associated with the key.
     *         <code>False</code> otherwise.
     */
    boolean exists(String key, Object value);

    /**
     * Removes the data associated with this key.
     * Being a request scope store, only the data that was added in the request/response lifecycle is erased.
     * Being a session scope store, any data in the store is erased.
     *
     * @param key The key associated with the data
     * @return <code>True</code> if any data was removed. <code>False</code> otherwise.
     */
    boolean remove(String key);

    /**
     * Clears the data associated with this Store.
     * Being a request scope store, only the data that was added in the request/response lifecycle is erased.
     * Being a session scope store, any data in the store is erased.
     */
    void clear();

    /**
     * Registers a callback to be executed <i><b>after</b></i> a new data is <b>saved</b> into the store.
     *
     * @param callback The callback to be executed
     * @return This store
     */
    Store onSaved(String key, Callback callback);

    /**
     * Registers a callback to be executed <i><b>after</b></i> a new data is <b>removed</b> from the store.
     *
     * @param callback The callback to be executed
     * @return This store
     */
    Store onRemoved(String key, Callback callback);

}
