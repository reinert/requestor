/*
 * Copyright 2022 Danilo Reinert
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class StoreManager implements Store {

    private final boolean concurrent;
    private Map<String, Data> dataMap;
    private Map<String, List<Store.SaveCallback>> savedCallbacks;
    private Map<String, List<Store.RemoveCallback>> removedCallbacks;

    public StoreManager(boolean concurrent) {
        this.concurrent = concurrent;
    }

    StoreManager copy() {
        final StoreManager copy = new StoreManager(concurrent);

        if (dataMap != null) {
            copy.dataMap = concurrent ?
                    new ConcurrentHashMap<String, Data>(dataMap) :
                    new HashMap<String, Data>(dataMap);
        }

        if (savedCallbacks != null) {
            copy.savedCallbacks = concurrent ?
                    new ConcurrentHashMap<String, List<Store.SaveCallback>>(savedCallbacks) :
                    new HashMap<String, List<Store.SaveCallback>>(savedCallbacks);
        }

        if (removedCallbacks != null) {
            copy.removedCallbacks = concurrent ?
                    new ConcurrentHashMap<String, List<Store.RemoveCallback>>(removedCallbacks) :
                    new HashMap<String, List<Store.RemoveCallback>>(removedCallbacks);
        }

        return copy;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(String key) {
        checkNotNull(key, "The key argument cannot be null");

        if (dataMap == null) return null;

        final Data data = dataMap.get(key);

        if (data == null || data.isExpired()) return null;

        return (T) data.getValue();
    }

    @Override
    public Store save(String key, Object value) {
        return save(key, value, 0L);
    }

    @Override
    public Store save(String key, Object value, Level level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Store save(String key, Object value, long ttl, Level level) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Store save(String key, Object value, long ttl) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");

        Object old = ensureDataMap().remove(key);

        dataMap.put(key, new Data(value, ttl));

        triggerSavedCallbacks(key, new Store.SaveEvent.Impl(key, old, value));

        return null;
    }

    @Override
    public boolean exists(String key) {
        checkNotNull(key, "The key argument cannot be null");
        return dataMap != null && dataMap.containsKey(key) && !dataMap.get(key).isExpired();
    }

    @Override
    public boolean exists(String key, Object value) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null. Try the exists method instead.");

        Data retrieved = dataMap == null ? null : dataMap.get(key);
        return retrieved != null && !retrieved.isExpired() &&
                (retrieved.getValue() == value || retrieved.getValue().equals(value));
    }

    @Override
    public boolean remove(String key) {
        checkNotNull(key, "The key argument cannot be null");

        if (dataMap != null) {
            Data old = dataMap.remove(key);

            if (old != null && !old.isExpired()) {
                triggerRemovedCallbacks(key, new Store.RemoveEvent.Impl(key, old.getValue()));
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        if (dataMap != null) dataMap.clear();
    }

    @Override
    public Store onSaved(String key, Store.SaveCallback callback) {
        addCallback(key, callback, ensureSavedCallbacks());
        return null;
    }

    @Override
    public Store onRemoved(String key, Store.RemoveCallback callback) {
        addCallback(key, callback, ensureRemovedCallbacks());
        return null;
    }

    private synchronized <C> void addCallback(String key, C callback, Map<String, List<C>> callbacksMap) {
        List<C> callbacks = callbacksMap.get(key);

        if (callbacks == null) {
            callbacks = new ArrayList<C>();
            callbacksMap.put(key, callbacks);
        }

        callbacks.add(callback);
    }

    private void triggerRemovedCallbacks(String key, Store.RemoveEvent event) {
        if (removedCallbacks == null) return;

        final List<Store.RemoveCallback> callbacks = removedCallbacks.get(key);

        if (callbacks == null) return;

        for (Store.RemoveCallback callback : callbacks) {
            callback.execute(event);
        }
    }

    private void triggerSavedCallbacks(String key, Store.SaveEvent event) {
        if (savedCallbacks == null) return;

        final List<Store.SaveCallback> callbacks = savedCallbacks.get(key);

        if (callbacks == null) return;

        for (Store.SaveCallback callback : callbacks) {
            callback.execute(event);
        }
    }

    private void checkNotNull(Object arg, String msg) {
        if (arg == null) throw new IllegalArgumentException(msg);
    }

    private Map<String, Data> ensureDataMap() {
        if (dataMap == null) {
            dataMap = concurrent
                    ? new ConcurrentHashMap<String, Data>()
                    : new HashMap<String, Data>();
        }
        return dataMap;
    }

    private Map<String, List<Store.RemoveCallback>> ensureRemovedCallbacks() {
        if (dataMap == null) {
            removedCallbacks = concurrent
                    ? new ConcurrentHashMap<String, List<Store.RemoveCallback>>()
                    : new HashMap<String, List<Store.RemoveCallback>>();
        }
        return removedCallbacks;
    }

    private Map<String, List<Store.SaveCallback>> ensureSavedCallbacks() {
        if (savedCallbacks == null) {
            savedCallbacks = concurrent
                    ? new ConcurrentHashMap<String, List<Store.SaveCallback>>()
                    : new HashMap<String, List<Store.SaveCallback>>();
        }
        return savedCallbacks;
    }
}
