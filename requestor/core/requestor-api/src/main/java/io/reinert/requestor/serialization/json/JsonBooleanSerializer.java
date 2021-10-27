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
package io.reinert.requestor.serialization.json;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;

/**
 * Serializer of JSON booleans.
 *
 * @author Danilo Reinert
 */
public class JsonBooleanSerializer extends JsonValueSerializer<Boolean> {

    public JsonBooleanSerializer() {
        super(Boolean.class);
    }

    @Override
    public Boolean deserialize(String response, DeserializationContext context) {
        return Boolean.valueOf(response);
    }

    @Override
    public String serialize(Boolean b, SerializationContext context) {
        return String.valueOf(b);
    }
}
