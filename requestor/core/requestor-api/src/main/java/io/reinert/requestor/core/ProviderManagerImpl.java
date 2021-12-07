/*
 * Copyright 2015-2021 Danilo Reinert
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Manager of instance providers.
 *
 * @author Danilo Reinert
 */
class ProviderManagerImpl implements ProviderManager {

    private final Map<String, Provider<?>> providers = new HashMap<String, Provider<?>>();

    public ProviderManagerImpl() {
        registerCollectionProviders();
    }

    @Override
    public <T> Registration register(Class<T> type, Provider<T> provider) {
        final String typeName = type.getCanonicalName();
        providers.put(typeName, provider);

        return new Registration() {
            @Override
            public void cancel() {
                providers.remove(typeName);
            }
        };
    }

    @Override
    public <T> Registration register(TypeProvider<T> provider) {
        return register(provider.getType(), provider);
    }

    /**
     * Given a class, return its {@link Provider}.
     *
     * @param <T> the class of T
     */
    @SuppressWarnings("unchecked")
    public <T> Provider<T> get(Class<T> type) {
        // TODO: throw exception if not exists?
        return (Provider<T>) providers.get(type.getCanonicalName());
    }

    private void registerCollectionProviders() {
        final Provider<ArrayList> arrayListProvider = new Provider<ArrayList>() {
            @Override
            public ArrayList getInstance() {
                return new ArrayList();
            }
        };
        providers.put(Collection.class.getCanonicalName(), arrayListProvider);
        providers.put(List.class.getCanonicalName(), arrayListProvider);
        providers.put(ArrayList.class.getCanonicalName(), arrayListProvider);
        providers.put(LinkedList.class.getCanonicalName(), new Provider<LinkedList>() {
            @Override
            public LinkedList getInstance() {
                return new LinkedList();
            }
        });
        final Provider<HashSet> hashSetProvider = new Provider<HashSet>() {
            @Override
            public HashSet getInstance() {
                return new HashSet();
            }
        };
        providers.put(Set.class.getCanonicalName(), hashSetProvider);
        providers.put(HashSet.class.getCanonicalName(), hashSetProvider);
        providers.put(TreeSet.class.getCanonicalName(), new Provider<TreeSet>() {
            @Override
            public TreeSet getInstance() {
                return new TreeSet();
            }
        });
    }
}
