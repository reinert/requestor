/*
 * Copyright 2014 Danilo Reinert
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

import com.google.web.bindery.event.shared.HandlerRegistration;

import org.turbogwt.core.collections.JsArrayList;

/**
 * Manager of instance providers.
 *
 * @author Danilo Reinert
 */
public final class ProviderManager {

    private final Map<String, Provider<?>> providers;

    public ProviderManager() {
        providers = new HashMap<String, Provider<?>>();
        final Provider<ArrayList> arrayListProvider = new Provider<ArrayList>() {
            @Override
            public ArrayList get() {
                return new ArrayList();
            }
        };
        providers.put(Collection.class.getName(), arrayListProvider);
        providers.put(List.class.getName(), arrayListProvider);
        providers.put(ArrayList.class.getName(), arrayListProvider);
        providers.put(LinkedList.class.getName(), new Provider<LinkedList>() {
            @Override
            public LinkedList get() {
                return new LinkedList();
            }
        });
        providers.put(JsArrayList.class.getName(), new Provider<JsArrayList>() {
            @Override
            public JsArrayList get() {
                return new JsArrayList();
            }
        });
        final Provider<HashSet> hashSetProvider = new Provider<HashSet>() {
            @Override
            public HashSet get() {
                return new HashSet();
            }
        };
        providers.put(Set.class.getName(), hashSetProvider);
        providers.put(HashSet.class.getName(), hashSetProvider);
        providers.put(TreeSet.class.getName(), new Provider<TreeSet>() {
            @Override
            public TreeSet get() {
                return new TreeSet();
            }
        });
    }

    /**
     * Map a {@link Provider} to its class.
     *
     * @param type      The class instance of T
     * @param provider  The Provider of T
     * @param <T>       The type of T
     *
     * @return  The {@link com.google.web.bindery.event.shared.HandlerRegistration} object,
     *          capable of cancelling this registration to the {@link ProviderManager}
     */
    public <T> HandlerRegistration bind(Class<T> type, Provider<T> provider) {
        final String typeName = type.getName();
        providers.put(typeName, provider);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                providers.remove(typeName);
            }
        };
    }

    /**
     * Given collection some class, return its {@link Provider}.
     *
     * @param <T> The class of T
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> Provider<T> get(Class<T> type) {
        return (Provider<T>) providers.get(type.getName());
    }
}
