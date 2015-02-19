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
 * Simple HTTP header with a single value (element).
 *
 * @author Danilo Reinert
 */
public class SimpleHeader extends Header {

    private final String name;
    private final Element element;

    public SimpleHeader(String name, Element element) {
        this.name = name;
        this.element = element;
    }

    public SimpleHeader(String name, String value) {
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
}
