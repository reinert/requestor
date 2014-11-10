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

import java.util.Collection;

import io.reinert.requestor.Provider;

/**
 * Context of deserialization.
 *
 * @author Danilo Reinert
 */
public abstract class DeserializationContext {

    private final ContainerProviderManager containerProviderManager;

    public DeserializationContext(ContainerProviderManager containerProviderManager) {
        this.containerProviderManager = containerProviderManager;
    }

    public <C extends Collection> C getContainerInstance(Class<C> type) {
        final Provider<C> factory = containerProviderManager.getProvider(type);
        if (factory == null)
            throw new UnableToDeserializeException("Could not get container instance because there's no factory " +
                    "registered in the requestor.");
        return factory.get();
    }

    protected ContainerProviderManager getContainerProviderManager() {
        return containerProviderManager;
    }
}
