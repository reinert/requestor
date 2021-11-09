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

import java.math.BigDecimal;
import java.math.BigInteger;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.HandlesSubTypes;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * Serializer of JSON numbers.
 *
 * @author Danilo Reinert
 */
public class JsonNumberSerializer extends JsonValueSerializer<Number> implements HandlesSubTypes {

    public static boolean SERIALIZE_BIG_DECIMAL_AS_PLAIN_STRING;

    private static final Class<?>[] IMPL_CLASSES = new Class<?>[]{Byte.class, Short.class, Integer.class,
            Double.class, Long.class, BigInteger.class, BigDecimal.class};

    public JsonNumberSerializer() {
        super(Number.class);
    }

    @Override
    public Number deserialize(String response, DeserializationContext context) {
        final Class<?> clazz = context.getRequestedType();
        try {
            if (clazz == Integer.class)
                return Integer.valueOf(response);

            if (clazz == Double.class)
                return Double.valueOf(response);

            if (clazz == Long.class)
                return Long.valueOf(response);

            if (clazz == BigDecimal.class)
                return new BigDecimal(response);

            if (clazz == Short.class)
                return Short.valueOf(response);

            if (clazz == BigInteger.class)
                return new BigInteger(response);

            if (clazz == Byte.class)
                return Byte.valueOf(response);

            // else Number.class, then we must guess the best suit
            if (response.contains(".")) {
                try {
                    final Double d = Double.valueOf(response);
                    return d.isInfinite() || d.isNaN() ? new BigDecimal(response) : d;
                } catch (Exception e) {
                    return new BigDecimal(response);
                }
            } else {
                try {
                    return Integer.valueOf(response);
                } catch (Exception e) {
                    try {
                        return Long.valueOf(response);
                    } catch (Exception e1) {
                        return new BigInteger(response);
                    }
                }
            }
        } catch (Exception e) {
            throw new UnableToDeserializeException("Could not deserialize response as number.", e);
        }
    }

    @Override
    public Class<?>[] handledSubTypes() {
        return IMPL_CLASSES;
    }

    @Override
    public String serialize(Number n, SerializationContext context) {
        if (n == null)
            return null;

        if (n instanceof BigDecimal)
            return SERIALIZE_BIG_DECIMAL_AS_PLAIN_STRING ? ((BigDecimal) n).toPlainString() : n.toString();

        return n.toString();
    }
}
