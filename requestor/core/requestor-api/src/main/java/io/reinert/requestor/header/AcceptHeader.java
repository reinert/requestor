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
 * The HTTP Accept Header.
 *
 * @author Danilo Reinert
 */
public class AcceptHeader extends QualityFactorHeader {

    public AcceptHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                        String v4, double f4, String v5, double f5) {
        super("Accept", v1, f1, v2, f2, v3, f3, v4, f4, v5, f5);
    }

    public AcceptHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3,
                        String v4, double f4) {
        super("Accept", v1, f1, v2, f2, v3, f3, v4, f4);
    }

    public AcceptHeader(String name, String v1, double f1, String v2, double f2, String v3, double f3) {
        super("Accept", v1, f1, v2, f2, v3, f3);
    }

    public AcceptHeader(String name, String v1, double f1, String v2, double f2) {
        super("Accept", v1, f1, v2, f2);
    }

    public AcceptHeader(String name, String v1, double f1) {
        super("Accept", v1, f1);
    }

    public AcceptHeader(String name, String value) {
        super("Accept", value);
    }

    public AcceptHeader(String name, String... values) {
        super("Accept", values);
    }
}
