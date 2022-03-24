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
 * A basic Store implementation using HashMap.
 *
 * @author Danilo Reinert
 */
class RootStore implements Store {

    private final Map<String, Object> dataMap;

    RootStore() {
        this.dataMap = new ConcurrentHashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(String key) {
        checkNotNull(key, "The key argument cannot be null");

        return (T) dataMap.get(key);
    }

    @Override
    public Store save(String key, Object value) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");

        dataMap.put(key, value);
        return this;
    }

    @Override
    public Store save(String key, Object value, Level level) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");

        save(key, value);
        return this;
    }

    @Override
    public boolean exists(String key) {
        checkNotNull(key, "The key argument cannot be null");
        return dataMap.containsKey(key);
    }

    @Override
    public boolean isEquals(String key, Object value) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null. Try the exists method instead.");

        Object retrieved = dataMap.get(key);
        return retrieved != null && (retrieved == value || retrieved.equals(value));
    }

    @Override
    public boolean remove(String key) {
        checkNotNull(key, "The key argument cannot be null");
        return dataMap.remove(key) != null;
    }

    @Override
    public void clear() {
        dataMap.clear();
    }

    private void checkNotNull(Object arg, String msg) {
        if (arg == null) throw new IllegalArgumentException(msg);
    }
}
