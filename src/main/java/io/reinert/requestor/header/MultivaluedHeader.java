/*
 * Copyright 2014 Danilo Reinert
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

import com.google.gwt.http.client.Header;

/**
 * HTTP Header with multiple values.
 *
 * @author Danilo Reinert
 */
public class MultivaluedHeader extends Header {

    private final String name;
    private final String value;
    private final String[] values;

    protected MultivaluedHeader(String name, Object... values) {
        this.name = name;
        this.values = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Object v = values[i];
            this.values[i] = v.toString();
        }
        this.value = mountValue(this.values);
    }

    public MultivaluedHeader(String name, String... values) {
        this.name = name;
        this.values = values;
        this.value = mountValue(values);
    }

    /**
     * Returns the name of the HTTP header.
     *
     * @return name of the HTTP header
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the HTTP header.
     *
     * @return value of the HTTP header
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Returns the values of the HTTP header.
     *
     * @return values of the HTTP header
     */
    public String[] getValues() {
        return values;
    }

    protected String mountValue(String[] values) {
        String mountedValue = "";
        String separator = "";
        for (String v : values) {
            mountedValue += separator + v;
            separator = ", ";
        }
        return mountedValue;
    }
}
