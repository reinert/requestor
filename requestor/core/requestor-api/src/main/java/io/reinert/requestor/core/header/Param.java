/*
 * Copyright 2015-2021 Danilo Reinert
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
package io.reinert.requestor.core.header;

/**
 * Parameter of a header value (element).
 *
 * @author Danilo Reinert
 */
public abstract class Param {

    public static Param of(String key, String value, boolean quoted) {
        return new KeyValueParam(key, value, quoted);
    }

    public static Param of(String key, String value) {
        return new KeyValueParam(key, value);
    }

    public static Param of(String param) {
        return new SimpleParam(param);
    }

    private static void checkNotNull(String param, String arg) {
        if (param == null)
            throw new IllegalArgumentException("Unable to construct header value Param: " + arg + " cannot be null.");
    }

    /**
     * Returns the params separated by semicolon.
     *
     * @param params The params
     *
     * @return A string with the params separated by semicolon
     */
    public static String toString(Iterable<Param> params) {
        String result = "";
        for (Param param : params) {
            result += "; " + param.toString();
        }
        return result;
    }

    /**
     * Returns the param as string.
     *
     * @return The param as string
     */
    public abstract String getParam();

    /**
     * Returns the param as encoded string.
     *
     * @return The param as encoded string
     */
    public abstract String toString();

    public static class SimpleParam extends Param {

        final String param;

        private SimpleParam(String param) {
            checkNotNull(param, "'param'");
            this.param = param;
        }

        @Override
        public String getParam() {
            return param;
        }

        @Override
        public String toString() {
            return param;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final SimpleParam that = (SimpleParam) o;

            if (!param.equals(that.param))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return param.hashCode();
        }
    }

    public static class KeyValueParam extends Param {

        final String key;
        final String value;
        final boolean quoted;

        private KeyValueParam(String key, String value) {
            this(key, value, false);
        }

        private KeyValueParam(String key, String value, boolean quoted) {
            checkNotNull(key, "'key'");
            checkNotNull(value, "'value'");
            this.key = key;
            this.value = value;
            this.quoted = quoted;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String getParam() {
            return key + '=' + value;
        }

        @Override
        public String toString() {
            return key + '=' + (quoted ? '"' + value + '"' : value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final KeyValueParam that = (KeyValueParam) o;

            if (!key.equals(that.key))
                return false;
            if (!value.equals(that.value))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }
    }
}
