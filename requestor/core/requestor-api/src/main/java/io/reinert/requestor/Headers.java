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
package io.reinert.requestor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reinert.requestor.header.Header;

/**
 * Stores the headers from a HTTP request/response.
 *
 * @author Danilo Reinert
 */
public class Headers implements Iterable<Header> {

    private Map<String, Header> headers;

    public Headers(Iterable<Header> headers) {
        if (headers != null) {
            final Iterator<Header> iterator = headers.iterator();
            if (iterator.hasNext())
                ensureHeaders();
            while (iterator.hasNext()) {
                Header header = iterator.next();
                this.headers.put(formatKey(header.getName()), header);
            }
        }
    }

    public Headers(Header... headers) {
        if (headers.length > 0) {
            ensureHeaders();
            for (final Header header : headers) {
                this.headers.put(formatKey(header.getName()), header);
            }
        }
    }

    Headers(com.google.gwt.http.client.Header... headers) {
        if (headers.length > 0) {
            ensureHeaders();
            for (final com.google.gwt.http.client.Header header : headers) {
                this.headers.put(formatKey(header.getName()), Header.fromRawHeader(header));
            }
        }
    }

    private static String formatKey(String headerName) {
        return headerName.toLowerCase();
    }

    public boolean contains(String header) {
        return !isEmpty() && headers.containsKey(formatKey(header));
    }

    public Collection<Header> getAll() {
        return ensureHeaders().values();
    }

    public Header get(String header) {
        return isEmpty() ? null : headers.get(formatKey(header));
    }

    public String getValue(String header, String defaultValue) {
        final Header h = isEmpty() ? null : headers.get(formatKey(header));
        return h != null ? h.getValue() : defaultValue;
    }

    public String getValue(String header) {
        return getValue(formatKey(header), null);
    }

    @Override
    public Iterator<Header> iterator() {
        return isEmpty() ? Collections.<Header>emptyIterator() : headers.values().iterator();
    }

    public int size() {
        return isEmpty() ? 0 : headers.size();
    }

    /**
     * Adds a header to this container.
     *
     * @param header The header to be added
     */
    protected void add(Header header) {
        ensureHeaders().put(formatKey(header.getName()), header);
    }

    /**
     * If there's a header with the given name, then it is removed and returned.
     *
     * @param name The name of the header to remove
     *
     * @return The removed header or null if there was no header with the given name
     */
    protected Header remove(String name) {
        return isEmpty() ? null : ensureHeaders().remove(name);
    }

    private Map<String, Header> ensureHeaders() {
        if (headers == null)
            headers = new HashMap<String, Header>();
        return headers;
    }

    private boolean isEmpty() {
        return headers == null;
    }
}
