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
 * A basic Store implementation using HashMap.
 *
 * @author Danilo Reinert
 */
class RootStore implements Store {

    private final Map<String, Object> dataMap;

    RootStore() {
        this.dataMap = new HashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(String key) {
        return (T) dataMap.get(key);
    }

    @Override
    public void save(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override
    public void save(String key, Object value, Level level) {
        save(key, value);
    }

    @Override
    public boolean exists(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    public boolean delete(String key) {
        return dataMap.remove(key) != null;
    }

    @Override
    public void clear() {
        dataMap.clear();
    }
}
