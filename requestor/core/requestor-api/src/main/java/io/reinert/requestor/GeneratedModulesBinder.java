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
package io.reinert.requestor;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.serialization.Serializer;

class GeneratedModulesBinder {

    private static GeneratedModules generatedModules;

    public static void bind(SerializerManager serializerManager, ProviderManager providerManager) {
        if (generatedModules == null) {
            generatedModules = GWT.create(GeneratedModules.class);
        }

        for (SerializationModule serializationModule : generatedModules.getSerializationModules()) {
            for (Serializer<?> serializer : serializationModule.getSerializers()) {
                serializerManager.register(serializer);
            }

            for (Provider<?> provider : serializationModule.getProviders()) {
                providerManager.register(provider);
            }
        }
    }
}
