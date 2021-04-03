/*
 * Copyright 2021 Danilo Reinert
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
import java.util.Collection;
import java.util.List;

/**
 * Multivalued HTTP Header with relative quality factors.
 *
 * @author Danilo Reinert
 */
public class QualityFactorHeader extends MultivaluedHeader {

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                               String v4, double f4, String v5, double f5) {
        super(name, Element.of(v1, getParams(f1)),
                Element.of(v2, getParams(f2)),
                Element.of(v3, getParams(f3)),
                Element.of(v4, getParams(f4)),
                Element.of(v5, getParams(f5)));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                               String v4, double f4) {
        super(name, Element.of(v1, getParams(f1)),
                Element.of(v2, getParams(f2)),
                Element.of(v3, getParams(f3)),
                Element.of(v4, getParams(f4)));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3) {
        super(name, Element.of(v1, getParams(f1)),
                Element.of(v2, getParams(f2)),
                Element.of(v3, getParams(f3)));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2) {
        super(name, Element.of(v1, getParams(f1)),
                Element.of(v2, getParams(f2)));
    }

    public QualityFactorHeader(String name, String v1, double f1) {
        super(name, Element.of(v1, getParams(f1)));
    }

    public QualityFactorHeader(String name, String value) {
        super(name, Element.of(value));
    }

    public QualityFactorHeader(String name, String... values) {
        super(name, toValues(values));
    }

    public QualityFactorHeader(String name, Collection<Element> elements) {
        super(name, elements);
    }

    private static List<Param> getParams(double factor) {
        if (factor < 0 || factor > 1)
            throw new IllegalArgumentException("Quality Factor must be between 0 and 1.");
        return factor == 1 ? null : Arrays.asList(Param.of(String.valueOf(factor)));
    }

    private static Element[] toValues(String[] values) {
        Element[] array = new Element[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Element.of(values[i]);
        }
        return array;
    }
}
