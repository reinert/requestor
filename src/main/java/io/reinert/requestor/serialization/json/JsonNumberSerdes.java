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
import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * De/Serializer of JSON numbers.
 *
 * @author Danilo Reinert
 */
public class JsonNumberSerdes extends JsonValueSerdes<Number> {

    private static JsonNumberSerdes INSTANCE = new JsonNumberSerdes();

    public JsonNumberSerdes() {
        super(Number.class);
    }

    public static JsonNumberSerdes getInstance() {
        return INSTANCE;
    }

    @Override
    public Number deserialize(String response, DeserializationContext context) {
        try {
            if (response.contains(".")) {
                return Double.valueOf(response);
            }
            try {
                return Integer.valueOf(response);
            } catch (NumberFormatException e) {
                return Long.valueOf(response);
            }
        } catch (NumberFormatException e) {
            throw new UnableToDeserializeException("Could not deserialize response as number.", e);
        }
    }

    @Override
    public String serialize(Number n, SerializationContext context) {
        return String.valueOf(n);
    }
}
