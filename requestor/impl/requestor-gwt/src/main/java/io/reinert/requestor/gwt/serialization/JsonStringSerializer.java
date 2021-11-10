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
package io.reinert.requestor.gwt.serialization;

import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;

/**
 * Serializer of JSON strings.
 *
 * @author Danilo Reinert
 */
public class JsonStringSerializer extends JsonValueSerializer<String> {

    public JsonStringSerializer() {
        super(String.class);
    }

    @Override
    public String deserialize(String response, DeserializationContext context) {
        if (response.startsWith("\"") && response.endsWith("\""))
            return response.substring(1, response.length() - 1);
        return response;
    }

    @Override
    public String serialize(String s, SerializationContext context) {
        return "\"" + s + "\"";
    }
}
