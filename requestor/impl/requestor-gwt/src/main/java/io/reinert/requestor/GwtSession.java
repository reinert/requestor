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

import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.json.JsonBooleanSerializer;
import io.reinert.requestor.serialization.json.JsonNumberSerializer;
import io.reinert.requestor.serialization.json.JsonStringSerializer;
import io.reinert.requestor.serialization.json.OverlaySerializer;
import io.reinert.requestor.serialization.misc.TextSerializer;
import io.reinert.requestor.serialization.misc.VoidSerializer;

/**
 * A session that handles json data.
 *
 * @author Danilo Reinert
 */
public class GwtSession extends Session {

    public GwtSession() {
        super();
    }

    public GwtSession(Deferred.Factory deferredFactory) {
        super(deferredFactory);
    }

    public GwtSession(Deferred.Factory deferredFactory, RequestDispatcher.Factory requestDispatcherFactory) {
        super(deferredFactory, requestDispatcherFactory);
    }

    @Override
    protected void configure() {
        setRequestSerializer(new GwtRequestSerializer());
        setResponseDeserializer(new GwtResponseDeserializer());

        register(VoidSerializer.getInstance());
        register(TextSerializer.getInstance());

        register(new SerializerProvider() {
            @Override
            public Serializer<?> getInstance() {
                return new GwtFormDataSerializerUrlEncoded();
            }
        });
        register(new SerializerProvider() {
            @Override
            public Serializer<?> getInstance() {
                return new JsonStringSerializer();
            }
        });
        register(new SerializerProvider() {
            @Override
            public Serializer<?> getInstance() {
                return new JsonNumberSerializer();
            }
        });
        register(new SerializerProvider() {
            @Override
            public Serializer<?> getInstance() {
                return new JsonBooleanSerializer();
            }
        });
        register(new SerializerProvider() {
            @Override
            public Serializer<?> getInstance() {
                return new OverlaySerializer();
            }
        });

        setMediaType("application/json");
    }
}
