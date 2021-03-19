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
package io.reinert.requestor.serialization;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reinert.requestor.serialization.json.JsonNumberSerializer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests of {@link JsonNumberSerializer}.
 */
public class JsonNumberSerializerJreTest {

    private final JsonNumberSerializer serializer = JsonNumberSerializer.getInstance();

    @Test
    public void serialize_Number_ShouldReturnNumberAsString() throws Exception {
        Integer i = Integer.MAX_VALUE;
        Double d = Double.MAX_VALUE;
        BigInteger bi = BigInteger.ONE;
        BigDecimal bd = BigDecimal.ONE;

        assertEquals(i.toString(), serializer.serialize(i, null));
        assertEquals(d.toString(), serializer.serialize(d, null));
        assertEquals(bi.toString(), serializer.serialize(bi, null));
        assertEquals(bd.toString(), serializer.serialize(bd, null));
    }

    @Test(expected = UnableToDeserializeException.class)
    @SuppressWarnings("unchecked")
    public void deserialize_InvalidNumber_ShouldThrowUnableToDeserializeException() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Number.class);

        serializer.deserialize("invalid number", context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize_ValidDoubleValue_ShouldReturnDouble() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Number.class);

        Double upperBound = Double.MAX_VALUE;
        Double lowerBound = Double.MIN_VALUE;

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize_ValidBigDecimalValue_ShouldReturnBigDecimal() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Number.class);

        BigDecimal upperBound = new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(BigDecimal.TEN);
        BigDecimal lowerBound = upperBound.negate();

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize_ValidIntegerValue_ShouldReturnInteger() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Number.class);

        Integer upperBound = Integer.MAX_VALUE;
        Integer lowerBound = Integer.MIN_VALUE;

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize_ValidLongValue_ShouldReturnLong() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Number.class);

        Long upperBound = Long.MAX_VALUE;
        Long lowerBound = Long.MIN_VALUE;

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserialize_ValidBigIntegerValue_ShouldReturnBigInteger() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Number.class);

        BigInteger upperBound = new BigInteger(String.valueOf(Long.MAX_VALUE)).multiply(BigInteger.TEN);
        BigInteger lowerBound = upperBound.negate();

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeBigDecimalCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) BigDecimal.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        BigDecimal upperBound = new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(BigDecimal.TEN);
        BigDecimal lowerBound = upperBound.negate();

        String input = "[" + lowerBound.toString() + "," + BigDecimal.ZERO.toString() + ","
                + BigDecimal.ONE.toString() + "," + upperBound.toString() + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, lowerBound, BigDecimal.ZERO, BigDecimal.ONE, upperBound);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeBigDecimalValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) BigDecimal.class);

        BigDecimal upperBound = new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(BigDecimal.TEN);
        BigDecimal lowerBound = upperBound.negate();

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeBigIntegerCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) BigInteger.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        BigInteger upperBound = new BigInteger(String.valueOf(Long.MAX_VALUE)).multiply(BigInteger.TEN);
        BigInteger lowerBound = upperBound.negate();

        String input = "[" + lowerBound.toString(10) + "," + BigInteger.ZERO.toString(10) + ","
                + BigInteger.ONE.toString(10) + "," + upperBound.toString(10) + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, lowerBound, BigInteger.ZERO, BigInteger.ONE, upperBound);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeBigIntegerValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) BigInteger.class);

        BigInteger upperBound = new BigInteger(String.valueOf(Long.MAX_VALUE)).multiply(BigInteger.TEN);
        BigInteger lowerBound = upperBound.negate();

        assertEquals(lowerBound, serializer.deserialize(lowerBound.toString(10), context));
        assertEquals(upperBound, serializer.deserialize(upperBound.toString(10), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeByteCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Byte.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[" + String.valueOf(Byte.MIN_VALUE) + "," + String.valueOf(Byte.MAX_VALUE) + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, Byte.MIN_VALUE, Byte.MAX_VALUE);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeByteValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Byte.class);

        assertEquals(Byte.MIN_VALUE, serializer.deserialize(String.valueOf(Byte.MIN_VALUE), context));
        assertEquals(Byte.MAX_VALUE, serializer.deserialize(String.valueOf(Byte.MAX_VALUE), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeDoubleCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Double.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[" + String.valueOf(Double.MIN_VALUE) + "," + String.valueOf(Double.MAX_VALUE) + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, Double.MIN_VALUE, Double.MAX_VALUE);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeDoubleValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Double.class);

        assertEquals(Double.MIN_VALUE, serializer.deserialize(String.valueOf(Double.MIN_VALUE), context));
        assertEquals(Double.MAX_VALUE, serializer.deserialize(String.valueOf(Double.MAX_VALUE), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeIntegerCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Integer.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[" + String.valueOf(Integer.MIN_VALUE) + "," + String.valueOf(Integer.MAX_VALUE) + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, Integer.MIN_VALUE, Integer.MAX_VALUE);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeIntegerValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Integer.class);

        assertEquals(Integer.MIN_VALUE, serializer.deserialize(String.valueOf(Integer.MIN_VALUE), context));
        assertEquals(Integer.MAX_VALUE, serializer.deserialize(String.valueOf(Integer.MAX_VALUE), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeLongCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Long.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[" + String.valueOf(Long.MIN_VALUE) + "," + String.valueOf(Long.MAX_VALUE) + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, Long.MIN_VALUE, Long.MAX_VALUE);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeLongValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Long.class);

        assertEquals(Long.MIN_VALUE, serializer.deserialize(String.valueOf(Long.MIN_VALUE), context));
        assertEquals(Long.MAX_VALUE, serializer.deserialize(String.valueOf(Long.MAX_VALUE), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeShortCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Short.class);
        when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[" + String.valueOf(Short.MIN_VALUE) + "," + String.valueOf(Short.MAX_VALUE) + "]";
        Collection<Number> expected = new ArrayList<Number>();
        Collections.addAll(expected, Short.MIN_VALUE, Short.MAX_VALUE);

        Collection<Number> output = serializer.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deserializeShortValue() throws Exception {
        // Set-up mock
        DeserializationContext context = mock(DeserializationContext.class);
        when(context.getRequestedType()).thenReturn((Class) Short.class);

        assertEquals(Short.MIN_VALUE, serializer.deserialize(String.valueOf(Short.MIN_VALUE), context));
        assertEquals(Short.MAX_VALUE, serializer.deserialize(String.valueOf(Short.MAX_VALUE), context));
    }

    @Test
    public void serializeBigDecimalCollection() throws Exception {
        BigDecimal upperBound = new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(BigDecimal.TEN);
        BigDecimal lowerBound = upperBound.negate();

        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, lowerBound, BigDecimal.ZERO, BigDecimal.ONE, upperBound);
        String expected = "[" + lowerBound.toString() + "," + BigDecimal.ZERO.toString() + ","
                + BigDecimal.ONE.toString() + "," + upperBound.toString() + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeBigDecimalValue() throws Exception {
        BigDecimal upperBound = new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(BigDecimal.TEN);
        BigDecimal lowerBound = upperBound.negate();

        assertEquals(lowerBound.toString(), serializer.serialize(lowerBound, null));
        assertEquals(BigDecimal.ZERO.toString(), serializer.serialize(BigDecimal.ZERO, null));
        assertEquals(BigDecimal.ONE.toString(), serializer.serialize(BigDecimal.ONE, null));
        assertEquals(upperBound.toString(), serializer.serialize(upperBound, null));
    }

    @Test
    public void serializeBigIntegerCollection() throws Exception {
        BigInteger upperBound = new BigInteger(String.valueOf(Long.MAX_VALUE)).multiply(BigInteger.TEN);
        BigInteger lowerBound = upperBound.negate();

        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, lowerBound, BigInteger.ZERO, BigInteger.ONE, upperBound);
        String expected = "[" + lowerBound.toString(10) + "," + BigInteger.ZERO.toString(10) + ","
                + BigInteger.ONE.toString(10) + "," + upperBound.toString(10) + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeBigIntegerValue() throws Exception {
        BigInteger upperBound = new BigInteger(String.valueOf(Long.MAX_VALUE)).multiply(BigInteger.TEN);
        BigInteger lowerBound = upperBound.negate();

        assertEquals(lowerBound.toString(10), serializer.serialize(lowerBound, null));
        assertEquals(BigInteger.ZERO.toString(10), serializer.serialize(BigInteger.ZERO, null));
        assertEquals(BigInteger.ONE.toString(10), serializer.serialize(BigInteger.ONE, null));
        assertEquals(upperBound.toString(10), serializer.serialize(upperBound, null));
    }

    @Test
    public void serializeByteCollection() throws Exception {
        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, Byte.MIN_VALUE, Byte.MAX_VALUE);
        String expected = "[" + String.valueOf(Byte.MIN_VALUE) + "," + String.valueOf(Byte.MAX_VALUE) + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeByteValue() throws Exception {
        assertEquals(String.valueOf(Byte.MIN_VALUE), serializer.serialize(Byte.MIN_VALUE, null));
        assertEquals(String.valueOf(Byte.MAX_VALUE), serializer.serialize(Byte.MAX_VALUE, null));
    }

    @Test
    public void serializeDoubleCollection() throws Exception {
        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, Double.MIN_VALUE, Double.MAX_VALUE);
        String expected = "[" + String.valueOf(Double.MIN_VALUE) + "," + String.valueOf(Double.MAX_VALUE) + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeDoubleValue() throws Exception {
        assertEquals(String.valueOf(Double.MIN_VALUE), serializer.serialize(Double.MIN_VALUE, null));
        assertEquals(String.valueOf(Double.MAX_VALUE), serializer.serialize(Double.MAX_VALUE, null));
    }

    @Test
    public void serializeIntegerCollection() throws Exception {
        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, Integer.MIN_VALUE, Integer.MAX_VALUE);
        String expected = "[" + String.valueOf(Integer.MIN_VALUE) + "," + String.valueOf(Integer.MAX_VALUE) + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeIntegerValue() throws Exception {
        assertEquals(String.valueOf(Integer.MIN_VALUE), serializer.serialize(Integer.MIN_VALUE, null));
        assertEquals(String.valueOf(Integer.MAX_VALUE), serializer.serialize(Integer.MAX_VALUE, null));
    }

    @Test
    public void serializeLongCollection() throws Exception {
        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, Long.MIN_VALUE, Long.MAX_VALUE);
        String expected = "[" + String.valueOf(Long.MIN_VALUE) + "," + String.valueOf(Long.MAX_VALUE) + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeLongValue() throws Exception {
        assertEquals(String.valueOf(Long.MIN_VALUE), serializer.serialize(Long.MIN_VALUE, null));
        assertEquals(String.valueOf(Long.MAX_VALUE), serializer.serialize(Long.MAX_VALUE, null));
    }

    @Test
    public void serializeShortCollection() throws Exception {
        Collection<Number> input = new ArrayList<Number>();
        Collections.addAll(input, Short.MIN_VALUE, Short.MAX_VALUE);
        String expected = "[" + String.valueOf(Short.MIN_VALUE) + "," + String.valueOf(Short.MAX_VALUE) + "]";

        String output = serializer.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeShortValue() throws Exception {
        assertEquals(String.valueOf(Short.MIN_VALUE), serializer.serialize(Short.MIN_VALUE, null));
        assertEquals(String.valueOf(Short.MAX_VALUE), serializer.serialize(Short.MAX_VALUE, null));
    }
}
