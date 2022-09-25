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

/**
 * A child store that delegates execution to a parent store when cannot handle operations.
 *
 * @author Danilo Reinert
 */
class LeafStore implements Store {

    private final StoreManager storeManager;
    private final Store parentStore;

    static LeafStore copy(LeafStore leafStore) {
        return new LeafStore(leafStore.parentStore, leafStore.storeManager.copy());
    }

    LeafStore(Store store, boolean concurrent, AsyncRunner asyncRunner) {
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.storeManager = new StoreManager(concurrent, asyncRunner);
        this.parentStore = store;
    }

    private LeafStore(Store store, StoreManager storeManager) {
        this.storeManager = storeManager;
        this.parentStore = store;
    }

    @Override
    public <T> T retrieve(String key) {
        T data = storeManager.retrieve(key);

        if (data == null) {
            data = parentStore.retrieve(key);
        }

        return data;
    }

    @Override
    public Store save(String key, Object value, Level level) {
        return save(key, value, 0L, level);
    }

    @Override
    public Store save(String key, Object value, long ttl, Level level) {
        if (level == Level.PARENT) {
            parentStore.save(key, value, ttl);
            return this;
        }

        if (level == Level.ROOT) {
            parentStore.save(key, value, ttl, level);
            return this;
        }

        save(key, value, ttl);
        return this;
    }

    @Override
    public Store save(String key, Object value, long ttl) {
        storeManager.save(key, value, ttl);
        return this;
    }

    @Override
    public Store save(String key, Object value) {
        storeManager.save(key, value);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return storeManager.exists(key) || parentStore.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return storeManager.exists(key) ? storeManager.exists(key, value) : parentStore.exists(key, value);
    }

    @Override
    public boolean remove(String key) {
        return storeManager.remove(key) || parentStore.remove(key);
    }

    @Override
    public void clear() {
        storeManager.clear();
    }

    @Override
    public Store onSaved(String key, Callback callback) {
        storeManager.onSaved(key, callback);
        return this;
    }

    @Override
    public Store onRemoved(String key, Callback callback) {
        storeManager.onRemoved(key, callback);
        return this;
    }

    @Override
    public Store onExpired(String key, Callback callback) {
        storeManager.onExpired(key, callback);
        return this;
    }

    public boolean isConcurrent() {
        return storeManager.isConcurrent();
    }
}
