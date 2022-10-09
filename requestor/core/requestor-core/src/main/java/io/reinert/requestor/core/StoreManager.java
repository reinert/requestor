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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class StoreManager implements Store {

    private final Store owner;
    private final boolean concurrent;
    private final AsyncRunner asyncRunner;
    private Map<String, Data> dataMap;
    private Map<String, Set<Handler>> savedHandlers;
    private Map<String, Set<Handler>> removedHandlers;
    private Map<String, Set<Handler>> expiredHandlers;

    public StoreManager(Store owner, boolean concurrent, AsyncRunner asyncRunner) {
        this.owner = owner;
        this.concurrent = concurrent;
        this.asyncRunner = asyncRunner;
    }

    StoreManager copy(Store owner) {
        final StoreManager copy = new StoreManager(owner, concurrent, asyncRunner);

        if (dataMap != null) {
            copy.dataMap = concurrent ?
                    new ConcurrentHashMap<String, Data>(dataMap) :
                    new HashMap<String, Data>(dataMap);
        }

        if (savedHandlers != null) {
            copy.savedHandlers = concurrent ?
                    new ConcurrentHashMap<String, Set<Handler>>(savedHandlers) :
                    new HashMap<String, Set<Handler>>(savedHandlers);
        }

        if (removedHandlers != null) {
            copy.removedHandlers = concurrent ?
                    new ConcurrentHashMap<String, Set<Handler>>(removedHandlers) :
                    new HashMap<String, Set<Handler>>(removedHandlers);
        }

        if (expiredHandlers != null) {
            copy.expiredHandlers = concurrent ?
                    new ConcurrentHashMap<String, Set<Handler>>(expiredHandlers) :
                    new HashMap<String, Set<Handler>>(expiredHandlers);
        }

        return copy;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(String key) {
        Data data = getData(key);
        return data == null ? null : (T) data.getValue();
    }

    @Override
    public Data getData(String key) {
        checkNotNull(key, "The key argument cannot be null");
        return dataMap == null ? null : dataMap.get(key);
    }

    @Override
    public Store save(String key, Object value) {
        return save(key, value, 0L);
    }

    @Override
    public Store save(String key, Object value, Level level) {
        throw new UnsupportedOperationException("Save with Level argument is not supported by a Local Store.");
    }

    @Override
    public Store save(String key, Object value, long ttl, Level level) {
        throw new UnsupportedOperationException("Save with Level argument is not supported by a Local Store.");
    }

    @Override
    public Store save(final String key, Object value, long ttl) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null");

        Data removedData = ensureDataMap().remove(key);

        Data savedData = new Data(key, value, ttl);
        final long refreshedAt = savedData.getRefreshedAt();
        dataMap.put(key, savedData);

        if (ttl > 0L) {
            asyncRunner.run(new Runnable() {
                public void run() {
                    Data data = dataMap.get(key);
                    if (data != null && data.getRefreshedAt() == refreshedAt) {
                        triggerExpiredHandlers(key, data);
                        if (!exists(Store.REMOVE_ON_EXPIRED_DISABLED, Boolean.TRUE)) {
                            if (data.isExpired()) {
                                dataMap.remove(key);
                                triggerRemovedHandlers(key, data);
                            }
                        }
                    }
                }
            }, ttl + 1L);
        }

        triggerSavedHandlers(key, removedData, savedData);

        return null;
    }

    @Override
    public boolean exists(String key) {
        checkNotNull(key, "The key argument cannot be null");
        return dataMap != null && dataMap.containsKey(key);
    }

    @Override
    public boolean exists(String key, Object value) {
        checkNotNull(key, "The key argument cannot be null");
        checkNotNull(value, "The value argument cannot be null. Try the exists method instead.");

        Data retrieved = dataMap == null ? null : dataMap.get(key);
        return retrieved != null && (retrieved.getValue() == value || retrieved.getValue().equals(value));
    }

    @Override
    public Data remove(String key) {
        checkNotNull(key, "The key argument cannot be null");

        if (dataMap != null) {
            Data removedData = dataMap.remove(key);

            if (removedData != null) {
                triggerRemovedHandlers(key, removedData);
                return removedData;
            }
        }

        return null;
    }

    @Override
    public Data refresh(String key, long ttlMillis) {
        Data data = getData(key);

        if (data == null) return null;

        if (ttlMillis > -1L) {
            data.refresh(ttlMillis);
        } else {
            data.refresh();
        }

        return data;
    }

    @Override
    public Data refresh(String key) {
        return refresh(key, -1L);
    }

    @Override
    public void clear() {
        clear(true);
    }

    @Override
    public void clear(boolean fireRemovedEvent) {
        if (dataMap != null) {
            if (fireRemovedEvent) {
                List<Data> values = new ArrayList<Data>(dataMap.values());
                dataMap.clear();
                for (Data data : values) {
                    triggerRemovedHandlers(data.getKey(), data);
                }
                return;
            }

            dataMap.clear();
        }
    }

    @Override
    public Store onSaved(String key, Handler handler) {
        addHandler(key, handler, ensureSavedHandlers());
        return null;
    }

    @Override
    public Store onRemoved(String key, Handler handler) {
        addHandler(key, handler, ensureRemovedHandlers());
        return null;
    }

    @Override
    public Store onExpired(String key, Handler handler) {
        addHandler(key, handler, ensureExpiredHandlers());
        return null;
    }

    private synchronized void addHandler(String key, Handler handler, Map<String, Set<Handler>> handlersMap) {
        Set<Handler> handlers = handlersMap.get(key);

        if (handlers == null) {
            handlers = new HashSet<Handler>();
            handlersMap.put(key, handlers);
        }

        handlers.add(handler);
    }

    private void triggerSavedHandlers(String key, Data removedData, Data savedData) {
        triggerHandlers(savedHandlers, key, removedData, savedData);
    }

    private void triggerRemovedHandlers(String key, Data removedData) {
        triggerHandlers(removedHandlers, key, removedData, null);
    }

    private void triggerExpiredHandlers(String key, Data expiredData) {
        triggerHandlers(expiredHandlers, key, expiredData, null);
    }

    private void triggerHandlers(Map<String, Set<Handler>> handlersMap, String key, Data oldData, Data newData) {
        if (handlersMap == null) return;

        final Set<Handler> handlers = handlersMap.get(key);

        if (handlers == null) return;

        final Event.Impl event = new Event.Impl(owner, key, oldData, newData);
        final Iterator<Handler> it = handlers.iterator();
        // Should we run the handlers asynchronously?
        // If so, then make Handler.canceled volatile
        while (it.hasNext()) {
            Handler handler = it.next();
            if (handler.isCanceled()) {
                it.remove();
                continue;
            }

            handler.execute(event);
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

    private Map<String, Set<Handler>> ensureSavedHandlers() {
        if (savedHandlers == null) {
            savedHandlers = concurrent
                    ? new ConcurrentHashMap<String, Set<Handler>>()
                    : new HashMap<String, Set<Handler>>();
        }
        return savedHandlers;
    }

    private Map<String, Set<Handler>> ensureRemovedHandlers() {
        if (removedHandlers == null) {
            removedHandlers = concurrent
                    ? new ConcurrentHashMap<String, Set<Handler>>()
                    : new HashMap<String, Set<Handler>>();
        }
        return removedHandlers;
    }

    private Map<String, Set<Handler>> ensureExpiredHandlers() {
        if (expiredHandlers == null) {
            expiredHandlers = concurrent
                    ? new ConcurrentHashMap<String, Set<Handler>>()
                    : new HashMap<String, Set<Handler>>();
        }
        return expiredHandlers;
    }
}
