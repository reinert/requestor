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
package io.reinert.requestor.core.header;

/**
 * Simple HTTP header with a single value (element).
 *
 * @author Danilo Reinert
 */
public class SimpleHeader extends Header {

    private final String name;
    private final Element element;

    public SimpleHeader(String name, Element element) {
        checkNotNull(name, "Header name cannot be null.");
        checkNotNull(element, "Header value element cannot be null.");
        this.name = name;
        this.element = element;
    }

    public SimpleHeader(String name, String value) {
        checkNotNull(name, "Header name cannot be null.");
        checkNotNull(value, "Header value cannot be null.");
        this.name = name;
        this.element = Element.of(value);
    }

    public Element getElement() {
        return element;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return element.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final SimpleHeader that = (SimpleHeader) o;

        if (!element.equals(that.element))
            return false;
        if (!name.equals(that.name))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + element.hashCode();
        return result;
    }

    private void checkNotNull(Object param, String msg) {
        if (param == null) throw new IllegalArgumentException(msg);
    }
}
