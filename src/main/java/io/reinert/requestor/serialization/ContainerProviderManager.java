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
package io.reinert.requestor.serialization;

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

import io.reinert.requestor.Provider;

import org.turbogwt.core.collections.JsArrayList;

/**
 * Manager of container (collection) factories.
 *
 * @author Danilo Reinert
 */
public final class ContainerProviderManager {

    private final Map<String, Provider<? extends Collection>> factories;

    public ContainerProviderManager() {
        factories = new HashMap<String, Provider<? extends Collection>>();
        final Provider<ArrayList> arrayListProvider = new Provider<ArrayList>() {
            @Override
            public ArrayList get() {
                return new ArrayList();
            }
        };
        final Provider<JsArrayList> jsArrayListProvider = new Provider<JsArrayList>() {
            @Override
            public JsArrayList get() {
                return new JsArrayList();
            }
        };
        factories.put(JsArrayList.class.getName(), jsArrayListProvider);
        factories.put(Collection.class.getName(), jsArrayListProvider);
        factories.put(List.class.getName(), jsArrayListProvider);
        factories.put(ArrayList.class.getName(), arrayListProvider);
        factories.put(LinkedList.class.getName(), new Provider<LinkedList>() {
            @Override
            public LinkedList get() {
                return new LinkedList();
            }
        });

        final Provider<HashSet> hashSetProvider = new Provider<HashSet>() {
            @Override
            public HashSet get() {
                return new HashSet();
            }
        };
        factories.put(Set.class.getName(), hashSetProvider);
        factories.put(HashSet.class.getName(), hashSetProvider);
        factories.put(TreeSet.class.getName(), new Provider<TreeSet>() {
            @Override
            public TreeSet get() {
                return new TreeSet();
            }
        });
    }

    /**
     * Map a {@link Provider} to some collection class.
     *
     * @param type      The type of the collection
     * @param provider   The Provider of the collection
     * @param <C>       The type of the collection
     *
     * @return  The {@link com.google.web.bindery.event.shared.HandlerRegistration} object,
     *          capable of cancelling this registration to the {@link ContainerProviderManager}
     */
    public <C extends Collection> HandlerRegistration putProvider(Class<C> type, Provider<C> provider) {
        final String typeName = type.getName();
        factories.put(typeName, provider);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                factories.remove(typeName);
            }
        };
    }

    /**
     * Given collection some class, return its {@link Provider}.
     *
     * @param <C> Collection type
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends Collection> Provider<C> getProvider(Class<C> type) {
        return (Provider<C>) factories.get(type.getName());
    }
}
