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

public class VolatileStorage implements Storage {

    private final Storage persistentStorage;
    private Map<String, Object> localDataMap;

    public static VolatileStorage copy(VolatileStorage volatileStorage) {
        VolatileStorage storage = new VolatileStorage(volatileStorage.persistentStorage);

        if (volatileStorage.localDataMap != null) {
            storage.localDataMap = new HashMap<String, Object>(volatileStorage.localDataMap);
        }

        return storage;
    }

    VolatileStorage(Storage storage) {
        this.persistentStorage = storage;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        T data = null;

        if (localDataMap != null) {
            data = (T) localDataMap.get(key);
        }

        if (data == null) {
            data = persistentStorage.get(key);
        }

        return data;
    }

    public void set(String key, Object value, boolean sessionPersistent) {
        if (sessionPersistent) persistentStorage.set(key, value, true);
        ensureDataMap().put(key, value);
    }

    public void set(String key, Object value) {
        this.set(key, value, false);
    }

    public boolean has(String key) {
        boolean has = persistentStorage.has(key);

        if (has) return true;

        if (localDataMap == null) return false;

        return localDataMap.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T pop(String key) {
        T data = null;

        if (localDataMap != null) {
            data = (T) localDataMap.remove(key);
        }

        if (data == null) {
            data = persistentStorage.get(key);
        }

        return data;
    }

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
