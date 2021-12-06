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

import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;

/**
 * Base class for all Serializer that manipulates serialized JSON simple values.
 *
 * @param <T>   Type of the object to serialize/deserialize
 *
 * @author Danilo Reinert
 */
public abstract class JsonValueSerializer<T> extends JsonSerializer<T> {

    public JsonValueSerializer(Class<T> handledType) {
        super(handledType);
    }

    @Override
    public <C extends Collection<T>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                   DeserializationContext context) {
        final String trimmedPayload = payload.asString().trim();
        if (!isArray(trimmedPayload))
            throw new UnableToDeserializeException("Response content is not an array.");

        C col = context.getInstance(collectionType);

        int initialIndex = 1;
        final int lastIndex = trimmedPayload.length() - 1;
        while (initialIndex < lastIndex) {
            int finalIndex = trimmedPayload.indexOf(",", initialIndex);
            if (finalIndex == -1) finalIndex = trimmedPayload.indexOf("]", initialIndex);
            final String trimmedValue = trimmedPayload.substring(initialIndex, finalIndex).trim();
            col.add(deserialize(new SerializedPayload(trimmedValue), context));
            initialIndex = finalIndex + 1;
        }

        return col;
    }
}
