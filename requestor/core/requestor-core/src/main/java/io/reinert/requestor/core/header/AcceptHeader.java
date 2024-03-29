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

import java.util.Collection;

/**
 * The HTTP Accept header.
 * <p></p>
 * It's a relative quality factor header.
 *
 * @author Danilo Reinert
 */
public class AcceptHeader extends QualityFactorHeader {

    public AcceptHeader(String v1, double f1, String v2, double f2, String v3, double f3,
                        String v4, double f4, String v5, double f5) {
        super("Accept", v1, f1, v2, f2, v3, f3, v4, f4, v5, f5);
    }

    public AcceptHeader(String v1, double f1, String v2, double f2, String v3, double f3,
                        String v4, double f4) {
        super("Accept", v1, f1, v2, f2, v3, f3, v4, f4);
    }

    public AcceptHeader(String v1, double f1, String v2, double f2, String v3, double f3) {
        super("Accept", v1, f1, v2, f2, v3, f3);
    }

    public AcceptHeader(String v1, double f1, String v2, double f2) {
        super("Accept", v1, f1, v2, f2);
    }

    public AcceptHeader(String v1, double f1) {
        super("Accept", v1, f1);
    }

    public AcceptHeader(String value) {
        super("Accept", value);
    }

    public AcceptHeader(String... values) {
        super("Accept", values);
    }

    public AcceptHeader(Collection<Element> elements) {
        super("Accept", elements);
    }
}
