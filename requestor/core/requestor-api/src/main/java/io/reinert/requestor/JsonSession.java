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

import io.reinert.requestor.serialization.json.JsonBooleanSerializer;
import io.reinert.requestor.serialization.json.JsonNumberSerializer;
import io.reinert.requestor.serialization.json.JsonStringSerializer;
import io.reinert.requestor.serialization.json.OverlaySerializer;

/**
 * A session that handles json data.
 *
 * @author Danilo Reinert
 */
public class JsonSession extends CleanSession {

    public JsonSession() {
        super();
    }

    public JsonSession(RequestDispatcherFactory requestDispatcherFactory, DeferredFactory deferredFactory) {
        super(requestDispatcherFactory, deferredFactory);
    }

    protected void configure() {
        super.configure();

        register(JsonStringSerializer.getInstance());
        register(JsonNumberSerializer.getInstance());
        register(JsonBooleanSerializer.getInstance());
        register(OverlaySerializer.getInstance());

        setMediaType("application/json");
    }
}