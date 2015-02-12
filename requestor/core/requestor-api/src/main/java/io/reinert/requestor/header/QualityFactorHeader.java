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
 * HTTP Header with relative quality factors.
 *
 * @author Danilo Reinert
 */
public class QualityFactorHeader extends MultivaluedHeader {

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                               String v4, double f4, String v5, double f5) {
        super(name, Value.of(v1, Param.of(String.valueOf(f1))), Value.of(v2, Param.of(String.valueOf(f2))),
                Value.of(v3, Param.of(String.valueOf(f3))), Value.of(v4, Param.of(String.valueOf(f4))),
                Value.of(v5, Param.of(String.valueOf(f5))));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                               String v4, double f4) {
        super(name, Value.of(v1, Param.of(String.valueOf(f1))), Value.of(v2, Param.of(String.valueOf(f2))),
                Value.of(v3, Param.of(String.valueOf(f3))), Value.of(v4, Param.of(String.valueOf(f4))));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3) {
        super(name, Value.of(v1, Param.of(String.valueOf(f1))), Value.of(v2, Param.of(String.valueOf(f2))),
                Value.of(v3, Param.of(String.valueOf(f3))));
    }

    public QualityFactorHeader(String name, String v1, double f1, String v2, double f2) {
        super(name, Value.of(v1, Param.of(String.valueOf(f1))), Value.of(v2, Param.of(String.valueOf(f2))));
    }

    public QualityFactorHeader(String name, String v1, double f1) {
        super(name, Value.of(v1, Param.of(String.valueOf(f1))));
    }

    public QualityFactorHeader(String name, String value) {
        super(name, Value.of(value));
    }

    public QualityFactorHeader(String name, String... values) {
        super(name, toValues(values));
    }

    private static Value[] toValues(String[] values) {
        Value[] array = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Value.of(values[i]);
        }
        return array;
    }
}
