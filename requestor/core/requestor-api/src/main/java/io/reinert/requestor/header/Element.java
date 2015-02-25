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

import java.util.ArrayList;
import java.util.List;

/**
 * A single header value.
 *
 * @author Danilo Reinert
 */
public abstract class Element {

    protected final Iterable<Param> params;
    private String paramsString;

    protected Element(Iterable<Param> params) {
        this.params = params;
    }

    public static Element of(String key, String value, boolean quoted, Iterable<Param> params) {
        return new KeyValueElement(key, value, quoted, params);
    }

    public static Element of(String key, String value, boolean quoted) {
        return new KeyValueElement(key, value, quoted);
    }

    public static Element of(String key, String value, Iterable<Param> params) {
        return new KeyValueElement(key, value, false, params);
    }

    public static Element of(String key, String value) {
        return new KeyValueElement(key, value, false);
    }

    public static Element of(String element, Iterable<Param> params) {
        return new SimpleElement(element, params);
    }

    public static Element of(String element) {
        return new SimpleElement(element);
    }

    private static void checkNotNull(String element, String arg) {
        if (element == null)
            throw new IllegalArgumentException("Unable to construct header value Element: " + arg + " cannot be null.");
    }

    /**
     * Returns the elements separated by comma.
     *
     * @param elements The elements
     *
     * @return A string with the elements separated by semicolon
     */
    public static String toString(Element... elements) {
        String result = "";
        String separator = "";
        for (Element element : elements) {
            result += separator + element.toString();
            separator = ", ";
        }
        return result;
    }

    /**
     * Returns the elements separated by comma.
     *
     * @param elements The elements
     *
     * @return A string with the elements separated by semicolon
     */
    public static String toString(Iterable<Element> elements) {
        String result = "";
        String separator = "";
        for (Element element : elements) {
            result += separator + element.toString();
            separator = ", ";
        }
        return result;
    }

    /**
     * Returns the element as string.
     *
     * @return The element as string
     */
    public abstract String getElement();

    public Iterable<Param> getParams() {
        return params;
    }

    @Override
    public String toString() {
        if (params == null)
            return getElement();

        if (paramsString == null) paramsString = Param.toString(params);
        return getElement() + paramsString;
    }

    public static class SimpleElement extends Element {

        private final String element;

        SimpleElement(String element, Iterable<Param> params) {
            super(params);
            checkNotNull(element, "'element'");
            this.element = element;
        }

        SimpleElement(String element) {
            this(element, null);
        }

        @Override
        public String getElement() {
            return element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final SimpleElement that = (SimpleElement) o;

            if (!element.equals(that.element))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return element.hashCode();
        }
    }

    public static class KeyValueElement extends Element {

        final String key;
        final String value;
        final boolean quoted;

        KeyValueElement(String key, String value, boolean quoted) {
            super(null);
            checkNotNull(key, "'key'");
            checkNotNull(value, "'value'");
            this.key = key;
            this.value = value;
            this.quoted = quoted;
        }

        KeyValueElement(String key, String value, boolean quoted, Iterable<Param> params) {
            super(params);
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
        public String getElement() {
            return key + '=' + (quoted ? '"' + value + '"' : value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final KeyValueElement that = (KeyValueElement) o;

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

    static class Builder {

        private String key;
        private String value;
        private boolean quoted;
        private List<Param> params;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder quoted(boolean quoted) {
            this.quoted = quoted;
            return this;
        }

        public Builder addParam(Param param) {
            if (params == null) params = new ArrayList<Param>();
            params.add(param);
            return this;
        }

        public boolean hasKey() {
            return key != null;
        }

        public Element build() {
            if (key != null && value != null)
                return of(key, value, quoted, params);
            else if (key != null)
                return of (key, params);
            else if (value != null)
                return of (value, params);
            else
                throw new IllegalStateException("Cannot build the header value Element because there's no key neither"
                        + " value set.");
        }
    }
}
