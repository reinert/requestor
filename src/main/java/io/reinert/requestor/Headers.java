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
package io.reinert.requestor;

import java.util.Iterator;
import java.util.Map;

import io.reinert.requestor.header.Header;

import org.turbogwt.core.collections.LightMap;

/**
 * Stores the headers from a HTTP request/response.
 *
 * @author Danilo Reinert
 */
public class Headers implements Iterable<Header> {

    private final Map<String, Header> headers = new LightMap<Header>();

    protected Headers(Header... headers) {
        for (final Header header : headers) {
            this.headers.put(formatKey(header.getName()), header);
        }
    }

    Headers(com.google.gwt.http.client.Header... headers) {
        for (final com.google.gwt.http.client.Header header : headers) {
            this.headers.put(formatKey(header.getName()), new Header() {
                @Override
                public String getName() {
                    return header.getName();
                }

                @Override
                public String getValue() {
                    return header.getValue();
                }
            });
        }
    }

    private static String formatKey(String headerName) {
        return headerName.toLowerCase();
    }

    public boolean contains(String header) {
        return headers.containsKey(formatKey(header));
    }

    public Header get(String header) {
        return headers.get(formatKey(header));
    }

    public String getValue(String header, String defaultValue) {
        final Header h = headers.get(formatKey(header));
        return h != null ? h.getValue() : defaultValue;
    }

    public String getValue(String header) {
        return getValue(formatKey(header), null);
    }

    @Override
    public Iterator<Header> iterator() {
        return headers.values().iterator();
    }

    public int size() {
        return headers.size();
    }

    /**
     * Adds a header to this container.
     *
     * @param header The header to be added
     */
    protected void add(Header header) {
        headers.put(formatKey(header.getName()), header);
    }

    /**
     * If there's a header with the given name, then it is removed and returned.
     *
     * @param name The name of the header to remove
     *
     * @return The removed header or null if there was no header with the given name
     */
    protected Header remove(String name) {
        return headers.remove(name);
    }
}
