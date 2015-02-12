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
package io.reinert.requestor.header;

import java.util.Arrays;

import com.google.gwt.http.client.URL;

/**
 * The value of a header.
 *
 * @author Danilo Reinert
 */
public abstract class Value {

    public static Value of(String value, Param... params) {
        if (params.length == 0) {
            return new SimpleValue(value);
        } else {
            return new ParametrizedValue(value, params);
        }
    }

    public static String toString(Value... values) {
        String result = "";
        String separator = "";
        for (Value value : values) {
            result += separator + value.toString();
            separator = ", ";
        }
        return result;
    }

    private static void checkNotNull(String value) {
        if (value == null)
            throw new IllegalArgumentException("Unable to construct header Value: value cannot be null.");
    }

    /**
     * Returns the value as string.
     *
     * @return The value as string
     */
    public abstract String getValue();

    /**
     * Returns the value as encoded string.
     *
     * @return The value as encoded string
     */
    public abstract String toString();

    public static class SimpleValue extends Value {

        private final String value;

        private SimpleValue(String value) {
            checkNotNull(value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final SimpleValue that = (SimpleValue) o;

            if (!value.equals(that.value))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return URL.encodeQueryString(value);
        }
    }

    public static class ParametrizedValue extends Value {

        private final String value;
        private final Param[] params;
        private String paramsString;

        private ParametrizedValue(String value, Param[] params) {
            checkNotNull(value);
            this.value = value;
            this.params = params;
        }

        @Override
        public String getValue() {
            return value;
        }

        public Param[] getParams() {
            return params;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final ParametrizedValue that = (ParametrizedValue) o;

            if (!Arrays.equals(params, that.params))
                return false;
            if (!value.equals(that.value))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value.hashCode();
            result = 31 * result + (params != null ? Arrays.hashCode(params) : 0);
            return result;
        }

        @Override
        public String toString() {
            if (paramsString == null) paramsString = Param.toString(params);
            return URL.encodeQueryString(value) + paramsString;
        }
    }
}
