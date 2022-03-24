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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A child store that delegates execution to a parent store when cannot handle operations.
 *
 * @author Danilo Reinert
 */
class LeafStore implements Store {

    private final Store parentStore;
    private Map<String, Object> localDataMap;

    static LeafStore copy(LeafStore leafStore) {
        LeafStore store = new LeafStore(leafStore.parentStore);

        if (leafStore.localDataMap != null) {
            store.localDataMap = new ConcurrentHashMap<String, Object>(leafStore.localDataMap);
        }

        return store;
    }

    LeafStore(Store store) {
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.parentStore = store;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(String key) {
        checkNotNull(key, "The key argument cannot be null");

        T data = null;

        if (localDataMap != null) {
            data = (T) localDataMap.get(key);
        }

        if (data == null) {
            data = parentStore.retrieve(key);
        }

        return data;
    }

    @Override
    public Store save(String key, Object value, Level level) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");
        checkNotNull(level, "The value argument cannot be null");

        if (level == Level.PARENT) {
            parentStore.save(key, value);
            return this;
        }

        parentStore.save(key, value, level);
        return this;
    }

    @Override
    public Store save(String key, Object value) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");

        ensureDataMap().put(key, value);
        return this;
    }

    @Override
    public boolean exists(String key) {
        checkNotNull(key, "The key argument cannot be null");

        boolean has = parentStore.exists(key);

        if (has) return true;

        if (localDataMap == null) return false;

        return localDataMap.containsKey(key);
    }

    @Override
    public boolean isEquals(String key, Object value) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");

        Object retrieved = retrieve(key);
        return retrieved != null && (retrieved == value || retrieved.equals(value));
    }

    @Override
    public boolean remove(String key) {
        checkNotNull(key, "The key argument cannot be null");

        if (localDataMap != null) {
            return localDataMap.remove(key) != null;
        }

        return false;
    }

    @Override
    public void clear() {
        if (localDataMap != null) localDataMap.clear();
    }

    private Map<String, Object> ensureDataMap() {
        if (localDataMap == null) {
            localDataMap = new ConcurrentHashMap<String, Object>();
        }

        return localDataMap;
    }

    private void checkNotNull(Object arg, String msg) {
        if (arg == null) throw new IllegalArgumentException(msg);
    }
}
