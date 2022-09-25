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

public class StoreManagerImpl implements StoreManager {

    private final boolean concurrent;
    private final Map<String, List<Store.SaveCallback>> savedCallbacks;
    private final Map<String, List<Store.RemoveCallback>> removedCallbacks;

    public StoreManagerImpl(boolean concurrent) {
        this.concurrent = concurrent;

        if (concurrent) {
            savedCallbacks = new ConcurrentHashMap<String, List<Store.SaveCallback>>();
            removedCallbacks = new ConcurrentHashMap<String, List<Store.RemoveCallback>>();
            return;
        }

        savedCallbacks = new HashMap<String, List<Store.SaveCallback>>();
        removedCallbacks = new HashMap<String, List<Store.RemoveCallback>>();
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void triggerRemovedCallbacks(String key, Store.RemoveEvent event) {
        final List<Store.RemoveCallback> callbacks = removedCallbacks.get(key);

        if (callbacks == null) return;

        for (Store.RemoveCallback callback : callbacks) {
            callback.execute(event);
        }
    }

    public void triggerSavedCallbacks(String key, Store.SaveEvent event) {
        final List<Store.SaveCallback> callbacks = savedCallbacks.get(key);

        if (callbacks == null) return;

        for (Store.SaveCallback callback : callbacks) {
            callback.execute(event);
        }
    }

    @Override
    public StoreManager onSaved(String key, Store.SaveCallback callback) {
        return addCallback(key, callback, savedCallbacks);
    }

    @Override
    public StoreManager onRemoved(String key, Store.RemoveCallback callback) {
        return addCallback(key, callback, removedCallbacks);
    }

    private synchronized <C> StoreManager addCallback(String key, C callback, Map<String, List<C>> callbacksMap) {
        List<C> callbacks = callbacksMap.get(key);

        if (callbacks == null) {
            callbacks = new ArrayList<C>();
            callbacksMap.put(key, callbacks);
        }

        callbacks.add(callback);

        return this;
    }
}
