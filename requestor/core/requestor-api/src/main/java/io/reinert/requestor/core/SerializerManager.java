/*
 * Copyright 2015 Danilo Reinert
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

import io.reinert.requestor.core.serialization.Deserializer;
import io.reinert.requestor.core.serialization.Serializer;

/**
 * A container of {@link Serializer} and {@link Deserializer}.
 *
 * @author Danilo Reinert
 */
public interface SerializerManager {

    /**
     * Register a {@link Deserializer}.
     *
     * @param deserializer the deserializer to register
     *
     * @return the {@link Registration} object, capable of cancelling this registration
     */
    Registration register(Deserializer<?> deserializer);

    Registration register(DeserializerProvider deserializerProvider);

    /**
     * Register a {@link Serializer}.
     *
     * @param serializer the serializer to register
     *
     * @return the {@link Registration} object, capable of cancelling this registration
     */
    Registration register(Serializer<?> serializer);

    Registration register(SerializerProvider serializerProvider);
}
