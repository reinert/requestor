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

public class PersistentStorage implements Storage {

    private final Map<String, Object> dataMap;

    PersistentStorage() {
        this.dataMap = new HashMap<String, Object>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) dataMap.get(key);
    }

    @Override
    public void set(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override
    public void set(String key, Object value, boolean sessionPersistent) {
        set(key, value);
    }

    @Override
    public boolean has(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T pop(String key) {
        return (T) dataMap.remove(key);
    }

    public void clear() {
        dataMap.clear();
    }
}
