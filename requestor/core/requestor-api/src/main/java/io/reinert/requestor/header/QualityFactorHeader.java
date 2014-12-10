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
package io.reinert.requestor.header;

import java.util.Arrays;
import java.util.Iterator;

/**
 * HTTP Header with relative quality factors.
 *
 * @author Danilo Reinert
 */
public class QualityFactorHeader extends MultivaluedHeader implements Iterable<QualityFactorHeader.Value> {

    private final Value[] values;

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                               String v4, double f4, String v5, double f5) {
        this(name, Value.of(v1, f1), Value.of(v2, f2), Value.of(v3, f3), Value.of(v4, f4), Value.of(v5, f5));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                               String v4, double f4) {
        this(name, Value.of(v1, f1), Value.of(v2, f2), Value.of(v3, f3), Value.of(v4, f4));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3) {
        this(name, Value.of(v1, f1), Value.of(v2, f2), Value.of(v3, f3));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2) {
        this(name, Value.of(v1, f1), Value.of(v2, f2));
    }

    public QualityFactorHeader(String name, double f1, String v1) {
        this(name, Value.of(v1, f1));
    }

    public QualityFactorHeader(String name, Value... values) {
        super(name, (Object[]) values);
        this.values = values;
    }

    public QualityFactorHeader(String name, String... values) {
        super(name, (String[]) values);
        this.values = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            this.values[i] = Value.of(value);
        }
        Arrays.sort(values);
    }

    public Value[] getQualityFactorValues() {
        return values;
    }

    @Override
    public Iterator<Value> iterator() {
        return Arrays.asList(values).iterator();
    }

    /**
     * Represents a HTTP Header value with relative quality factor associated.
     */
    public static class Value implements Comparable<Value> {

        private final double factor;
        private final String value;

        private Value(double factor, String value) throws IllegalArgumentException {
            if (factor > 1.0 || factor < 0.0)
                throw new IllegalArgumentException("Factor must be between 0 and 1.");
            if (value == null || value.isEmpty())
                throw new IllegalArgumentException("Value cannot be empty or null.");
            this.factor = factor;
            this.value = value;
        }

        public static Value of(String value, double factor) {
            return new Value(factor, value);
        }

        public static Value of(String value) {
            return new Value(1, value);
        }

        public double getFactor() {
            return factor;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            if (factor == 1) {
                return value;
            }
            return value + "; " + factor;
        }

        @Override
        public int compareTo(Value value) {
            return Double.compare(value.factor, factor);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Value)) {
                return false;
            }

            final Value value1 = (Value) o;

            if (Double.compare(value1.factor, factor) != 0) {
                return false;
            }
            if (!value.equals(value1.value)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(factor);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + value.hashCode();
            return result;
        }
    }
}
