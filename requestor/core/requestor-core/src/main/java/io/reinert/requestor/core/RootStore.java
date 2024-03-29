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
 * A thread-safe Store implementation using ConcurrentHashMap with no parent Store.
 *
 * @author Danilo Reinert
 */
class RootStore implements Store {

    private final StoreManager storeManager;

    RootStore(AsyncRunner asyncRunner) {
        this.storeManager = new StoreManager(this, true, asyncRunner);
    }

    @Override
    @Deprecated
    public <T> T retrieve(String key) {
        return getValue(key);
    }

    @Override
    public <T> T getValue(String key) {
        return storeManager.getValue(key);
    }

    @Override
    public Data getData(String key) {
        return storeManager.getData(key);
    }

    @Override
    public Store save(String key, Object value) {
        storeManager.save(key, value);
        return this;
    }

    @Override
    public Store save(String key, Object value, Level level) {
        return save(key, value);
    }

    @Override
    public Store save(String key, Object value, long ttl, Level level) {
        return save(key, value, ttl);
    }

    @Override
    public Store save(String key, Object value, long ttl) {
        storeManager.save(key, value, ttl);
        return this;
    }

    @Override
    public boolean exists(String key) {
        return storeManager.exists(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        return storeManager.exists(key, value);
    }

    @Override
    public Data remove(String key) {
        return storeManager.remove(key);
    }

    @Override
    public Data refresh(String key, long ttlMillis) {
        return storeManager.refresh(key, ttlMillis);
    }

    @Override
    public Data refresh(String key) {
        return storeManager.refresh(key);
    }

    @Override
    public void clear() {
        storeManager.clear();
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        storeManager.clear(fireRemovedEvent);
    }

    @Override
    public Store onSaved(String key, Handler handler) {
        storeManager.onSaved(key, handler);
        return this;
    }

    @Override
    public Store onRemoved(String key, Handler handler) {
        storeManager.onRemoved(key, handler);
        return this;
    }

    @Override
    public Store onExpired(String key, Handler handler) {
        storeManager.onExpired(key, handler);
        return this;
    }
}
