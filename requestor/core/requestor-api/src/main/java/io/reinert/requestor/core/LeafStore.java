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

import java.util.HashMap;
import java.util.Map;

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
            store.localDataMap = new HashMap<String, Object>(leafStore.localDataMap);
        }

        return store;
    }

    LeafStore(Store store) {
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.parentStore = store;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        T data = null;

        if (localDataMap != null) {
            data = (T) localDataMap.get(key);
        }

        if (data == null) {
            data = parentStore.get(key);
        }

        return data;
    }

    @Override
    public void save(String key, Object value, Level level) {
        if (level == null) {
            save(key, value);
            return;
        }

        if (level == Level.PARENT) {
            parentStore.save(key, value);
            return;
        }

        parentStore.save(key, value, level);
    }

    @Override
    public void save(String key, Object value) {
        ensureDataMap().put(key, value);
    }

    @Override
    public boolean has(String key) {
        boolean has = parentStore.has(key);

        if (has) return true;

        if (localDataMap == null) return false;

        return localDataMap.containsKey(key);
    }

    @Override
    public boolean delete(String key) {
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
            localDataMap = new HashMap<String, Object>();
        }

        return localDataMap;
    }
}
