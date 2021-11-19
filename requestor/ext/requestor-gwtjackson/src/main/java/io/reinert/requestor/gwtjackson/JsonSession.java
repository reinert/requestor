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
package io.reinert.requestor.gwtjackson;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.SerializationModule;
import io.reinert.requestor.core.TypeProvider;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.gwt.GwtSession;

/**
 * A session that handles AutoBeans.
 *
 * @author Danilo Reinert
 */
public class JsonSession extends GwtSession {

    private static SerializationModule[] generatedModules;

    public JsonSession() {
        super();
    }

    public JsonSession(DeferredPool.Factory deferredPoolFactory) {
        super(deferredPoolFactory);
    }

    @Override
    protected void configure() {
        super.configure();

        setMediaType("application/json");

        if (generatedModules == null) {
            JsonGeneratedModules generatedModulesProvider = GWT.create(JsonGeneratedModules.class);
            generatedModules = generatedModulesProvider.getSerializationModules();
        }

        for (SerializationModule serializationModule : generatedModules) {
            for (Serializer<?> serializer : serializationModule.getSerializers()) {
                register(serializer);
            }

            for (TypeProvider<?> provider : serializationModule.getTypeProviders()) {
                register(provider);
            }
        }
    }
}
