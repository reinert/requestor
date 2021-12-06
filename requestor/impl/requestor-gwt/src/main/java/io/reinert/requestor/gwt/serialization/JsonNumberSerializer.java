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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.HandlesSubTypes;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;

/**
 * Serializer of JSON numbers.
 *
 * @author Danilo Reinert
 */
public class JsonNumberSerializer extends JsonValueSerializer<Number> implements HandlesSubTypes<Number> {

    public static boolean SERIALIZE_BIG_DECIMAL_AS_PLAIN_STRING;

    private static final List<Class<? extends Number>> IMPL_CLASSES = Arrays.<Class<? extends Number>>asList(Byte.class,
            Short.class, Integer.class, Double.class, Long.class, BigInteger.class, BigDecimal.class);

    public JsonNumberSerializer() {
        super(Number.class);
    }

    @Override
    public Number deserialize(SerializedPayload payload, DeserializationContext context) {
        final Class<?> clazz = context.getRequestedType();
        final String text = payload.asString();
        try {
            if (clazz == Integer.class)
                return Integer.valueOf(text);

            if (clazz == Double.class)
                return Double.valueOf(text);

            if (clazz == Long.class)
                return Long.valueOf(text);

            if (clazz == BigDecimal.class)
                return new BigDecimal(text);

            if (clazz == Short.class)
                return Short.valueOf(text);

            if (clazz == BigInteger.class)
                return new BigInteger(text);

            if (clazz == Byte.class)
                return Byte.valueOf(text);

            // else Number.class, then we must guess the best suit
            if (text.contains(".")) {
                try {
                    final Double d = Double.valueOf(text);
                    return d.isInfinite() || d.isNaN() ? new BigDecimal(text) : d;
                } catch (Exception e) {
                    return new BigDecimal(text);
                }
            } else {
                try {
                    return Integer.valueOf(text);
                } catch (Exception e) {
                    try {
                        return Long.valueOf(text);
                    } catch (Exception e1) {
                        return new BigInteger(text);
                    }
                }
            }
        } catch (Exception e) {
            throw new UnableToDeserializeException("Could not deserialize response as number.", e);
        }
    }

    @Override
    public List<Class<? extends Number>> handledSubTypes() {
        return IMPL_CLASSES;
    }

    @Override
    public SerializedPayload serialize(Number n, SerializationContext context) {
        if (n instanceof BigDecimal) {
            return new SerializedPayload(SERIALIZE_BIG_DECIMAL_AS_PLAIN_STRING ?
                    ((BigDecimal) n).toPlainString() : n.toString());
        }

        return new SerializedPayload(n.toString());
    }
}
