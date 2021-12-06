/*
 * Copyright 2014-2021 Danilo Reinert
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

import io.reinert.requestor.core.payload.SerializedPayload;
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
    public String deserialize(SerializedPayload payload, DeserializationContext context) {
        final String text = payload.asText();
        if (text.startsWith("\"") && text.endsWith("\""))
            return text.substring(1, text.length() - 1);
        return text;
    }

    @Override
    public SerializedPayload serialize(String s, SerializationContext context) {
        if (s.length() == 0) return SerializedPayload.EMPTY_PAYLOAD;
        return new SerializedPayload("\"" + s + "\"");
    }
}
