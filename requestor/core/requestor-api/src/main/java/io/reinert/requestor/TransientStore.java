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

import java.util.HashMap;
import java.util.Map;

class TransientStore implements Store {

    private final Store underStore;
    private Map<String, Object> localDataMap;

    public static TransientStore copy(TransientStore transientStore) {
        TransientStore store = new TransientStore(transientStore.underStore);

        if (transientStore.localDataMap != null) {
            store.localDataMap = new HashMap<String, Object>(transientStore.localDataMap);
        }

        return store;
    }

    TransientStore(Store store) {
        if (store == null) throw new IllegalArgumentException("Store cannot be null");
        this.underStore = store;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        T data = null;

        if (localDataMap != null) {
            data = (T) localDataMap.get(key);
        }

        if (data == null) {
            data = underStore.get(key);
        }

        return data;
    }

    @Override
    public void save(String key, Object value, boolean persist) {
        if (persist) {
            // If we'd want to go further in the chain, we'd call save(key, value, true)
            underStore.save(key, value);
        } else {
            ensureDataMap().put(key, value);
        }
    }

    @Override
    public void save(String key, Object value) {
        this.save(key, value, false);
    }

    @Override
    public boolean has(String key) {
        boolean has = underStore.has(key);

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
