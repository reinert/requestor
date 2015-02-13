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

/**
 * HTTP Header with multiple values.
 *
 * @author Danilo Reinert
 */
public class MultivaluedHeader extends Header {

    private final String name;
    private final Element[] elements;
    private String valueString;

    public MultivaluedHeader(String name, Element... elements) {
        this.name = name;
        this.elements = elements;
    }

    public MultivaluedHeader(String name, String... values) {
        this.name = name;
        this.elements = new Element[values.length];
        for (int i = 0; i < values.length; i++) {
            this.elements[i] = Element.of(values[i]);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        if (valueString == null) valueString = Element.toString(elements);
        return valueString;
    }

    public Element[] getElements() {
        return elements;
    }
}
