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
package io.reinert.requestor.json;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.Deferred;
import io.reinert.requestor.RequestDispatcher;
import io.reinert.requestor.SerializationModule;
import io.reinert.requestor.TypeProvider;
import io.reinert.requestor.gwt.GwtSession;
import io.reinert.requestor.serialization.Serializer;

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

    public JsonSession(Deferred.Factory deferredFactory) {
        super(deferredFactory);
    }

    public JsonSession(Deferred.Factory deferredFactory, RequestDispatcher.Factory requestDispatcherFactory) {
        super(deferredFactory, requestDispatcherFactory);
    }

    @Override
    protected void configure() {
        super.configure();

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
