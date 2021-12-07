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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * HTTP Header with multiple values (elements).
 *
 * @author Danilo Reinert
 */
public class MultivaluedHeader extends Header {

    private final String name;
    private final Iterable<Element> elements;
    private String valueString;

    @SuppressWarnings("unchecked")
    public MultivaluedHeader(String name, Collection<Element> elements) {
        checkNotNull(name, "Header name cannot be null.");
        checkNotNull(elements, "Header value elements cannot be null.");
        this.name = name;
        if (!elements.isEmpty()) {
            this.elements = Collections.unmodifiableCollection(elements);
        } else {
            this.elements = Collections.EMPTY_LIST;
        }
    }

    @SuppressWarnings("unchecked")
    public MultivaluedHeader(String name, Element... elements) {
        checkNotNull(name, "Header name cannot be null.");
        this.name = name;
        if (elements.length > 0) {
            this.elements = Collections.unmodifiableCollection(Arrays.asList(elements));
        } else {
            this.elements = Collections.EMPTY_LIST;
        }
    }

    @SuppressWarnings("unchecked")
    public MultivaluedHeader(String name, String... values) {
        checkNotNull(name, "Header name cannot be null.");
        this.name = name;
        if (values.length > 0) {
            final ArrayList<Element> elBuilder = new ArrayList<Element>(values.length);
            for (final String value : values) {
                elBuilder.add(Element.of(value));
            }
            this.elements = Collections.unmodifiableCollection(elBuilder);
        } else {
            this.elements = Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        ensureValueString();
        return valueString;
    }

    public Iterable<Element> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final MultivaluedHeader that = (MultivaluedHeader) o;

        if (!name.equals(that.name))
            return false;

        ensureValueString();
        if (!valueString.equals(that.valueString))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        ensureValueString();
        result = 31 * result + valueString.hashCode();

        return result;
    }

    private void ensureValueString() {
        if (valueString == null) valueString = Element.toString(getElements());
    }

    private void checkNotNull(Object param, String msg) {
        if (param == null) throw new IllegalArgumentException(msg);
    }
}
