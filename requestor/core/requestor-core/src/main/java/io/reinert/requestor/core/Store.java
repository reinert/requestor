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

    String REMOVE_ON_EXPIRED_DISABLED = "requestor.core.removeOnExpiredDisabled";

    enum Level {
        PARENT,
        ROOT;
    }

    class Data {
        private final String key;
        private final Object value;
        private long ttl;
        private final long createdAt;
        private long refreshedAt;
        private int timesRefreshed;

        public Data(String key, Object value, long ttl) {
            this.key = key;
            this.value = value;
            this.ttl = ttl;
            createdAt = refreshedAt = System.currentTimeMillis();
            timesRefreshed = 0;
        }

        public String getKey() {
            return key;
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

        public long getRefreshedAt() {
            return refreshedAt;
        }

        public int getTimesRefreshed() {
            return timesRefreshed;
        }

        public boolean isExpired() {
            return ttl > 0L && System.currentTimeMillis() > refreshedAt + ttl;
        }

        void refresh() {
            refresh(ttl);
        }

        synchronized void refresh(long ttl) {
            this.ttl = ttl;
            this.refreshedAt = System.currentTimeMillis();
            this.timesRefreshed++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return key.equals(data.key) && value.equals(data.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

    interface Event {
        Store getStore();
        String getKey();
        Data getOldData();
        Data getNewData();

        class Impl implements Event {
            private final Store store;
            private final String key;
            private final Data oldData;
            private final Data newData;

            public Impl(Store store, String key, Data oldData) {
                this.store = store;
                this.key = key;
                this.oldData = oldData;
                this.newData = null;
            }

            public Impl(Store store, String key, Data oldData, Data newData) {
                this.store = store;
                this.key = key;
                this.oldData = oldData;
                this.newData = newData;
            }

            @Override
            public Store getStore() {
                return store;
            }

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Data getOldData() {
                return oldData;
            }

            @Override
            public Data getNewData() {
                return newData;
            }
        }
    }

    abstract class Handler {
        private boolean canceled;

        public abstract void execute(Event event);

        protected void cancel() {
            canceled = true;
        }

        boolean isCanceled() {
            return canceled;
        }
    }

    /**
     * NOTE: Renamed to getValue. It will be removed in version 1.3.
     *
     * @deprecated Use {@link #getValue(String)} instead.
     */
    @Deprecated
    <T> T retrieve(String key);

    /**
     * Retrieves the value of the data saved with the given key.
     *
     * @param key The key associated with the data
     * @param <T> The type to typecast the object returned
     * @return The value saved with the key. <code>Null</code> otherwise.
     */
    <T> T getValue(String key);

    /**
     * Retrieves the data object saved with the given key.
     *
     * @param key The key associated with the data
     * @return The Data object saved with the key. <code>Null</code> otherwise.
     */
    Data getData(String key);

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
     * @param key       A key to associate the data
     * @param value     The data to be persisted
     * @param ttlMillis Time to live, i.e., the period when the data will still be valid, in milliseconds
     * @param level     Whether the data should be persisted in the underlying stores or not
     */
    Store save(String key, Object value, long ttlMillis, Level level);

    /**
     * Saves the value into the store associated with the key during the TTL period.
     *
     * @param key       A key to associate the data
     * @param value     The data to be persisted
     * @param ttlMillis Time to live, i.e., the period when the data will still be valid, in milliseconds
     */
    Store save(String key, Object value, long ttlMillis);

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
     * @return The data that was removed or <code>null</code> if there was no data associated with the given key.
     */
    Data remove(String key);

    /**
     * Refreshes the data saved with this key extending its valid time for the given TTL.
     * It affects only the local store, i.e., it's not residually executed in the upstream stores.
     *
     * @param key       The key associated with the data
     * @param ttlMillis Time to live, i.e., the period when the data will still be valid, in milliseconds
     * @return The data that was refreshed or <code>null</code> if there was no data associated with the given key.
     */
    Data refresh(String key, long ttlMillis);

    /**
     * Refreshes the data saved with this key extending its valid time for its original TTL.
     * It affects only the local store, i.e., it's not residually executed in the upstream stores.
     *
     * @param key The key associated with the data
     * @return The data that was refreshed or <code>null</code> if there was no data associated with the given key.
     */
    Data refresh(String key);

    /**
     * Clears the data associated with this Store firing the removed event for each key saved in it.
     * Being a request scope store, only the data that was added in the request/response lifecycle is erased.
     * Being a session scope store, any data in the store is erased.
     */
    void clear();

    /**
     * Clears the data associated with this Store optionally firing the removed event for each key saved in it.
     * Being a request scope store, only the data that was added in the request/response lifecycle is erased.
     * Being a session scope store, any data in the store is erased.
     *
     * @param fireRemovedEvent Flag to determine whether the onRemoved handlers should be triggered for each key
     */
    void clear(boolean fireRemovedEvent);

    /**
     * Registers a handler to be executed <i><b>after</b></i> a new data is <b>saved</b> into the store.
     *
     * @param handler The handler to be executed
     * @return This store
     */
    Store onSaved(String key, Handler handler);

    /**
     * Registers a handler to be executed <i><b>after</b></i> this key's data is <b>removed</b> from the store.
     *
     * @param handler The handler to be executed
     * @return This store
     */
    Store onRemoved(String key, Handler handler);

    /**
     * Registers a handler to be executed <i><b>after</b></i> this key's data <b>expires</b> (ttl times out).
     *
     * @param handler The handler to be executed
     * @return This store
     */
    Store onExpired(String key, Handler handler);

}
