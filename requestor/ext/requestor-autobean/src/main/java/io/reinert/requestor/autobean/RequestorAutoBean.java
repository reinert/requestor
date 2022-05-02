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
package io.reinert.requestor.autobean;

import com.google.gwt.core.client.GWT;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.SerializationModule;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.gwt.Requestor;

/**
 * A session that handles AutoBeans.
 *
 * @author Danilo Reinert
 */
public class RequestorAutoBean {

    private static SerializationModule[] generatedModules;

    public static Session newSession() {
        return configure(Requestor.newSession());
    }

    public static Session newSession(DeferredPool.Factory deferredPoolFactory) {
        return configure(Requestor.newSession(deferredPoolFactory));
    }

    public static Session configure(Session session) {
        session.setMediaType("application/json");

        if (generatedModules == null) {
            AutoBeanGeneratedModules generatedModulesProvider = GWT.create(AutoBeanGeneratedModules.class);
            generatedModules = generatedModulesProvider.getSerializationModules();
        }

        for (SerializationModule serializationModule : generatedModules) {
            session.register(serializationModule);
        }

        return session;
    }
}
