/*
 * Copyright 2014-2022 Danilo Reinert
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
package io.reinert.requestor.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reinert.requestor.core.header.Header;
import io.reinert.requestor.core.header.SimpleHeader;

/**
 * Stores the headers from an HTTP request/response.
 *
 * @author Danilo Reinert
 */
public class Headers implements Iterable<Header>, Map<String, Header> {

    private final boolean concurrent;
    private Map<String, Header> headers;

    public Headers() {
        this(false);
    }

    public Headers(boolean concurrent) {
        headers = null;
        this.concurrent = concurrent;
    }

    public Headers(Iterable<Header> headers) {
        concurrent = false;
        if (headers != null) {
            final Iterator<Header> iterator = headers.iterator();

            if (iterator.hasNext()) ensureHeaders();

            while (iterator.hasNext()) {
                Header header = iterator.next();
                this.headers.put(formatKey(header.getName()), header);
            }
        }
    }

    public Headers(Header... headers) {
        concurrent = false;
        if (headers.length > 0) {
            ensureHeaders();
            for (final Header header : headers) {
                this.headers.put(formatKey(header.getName()), header);
            }
        }
    }

    public static Headers copy(Headers headers) {
        if (headers.isEmpty()) {
            return new Headers();
        }

        return new Headers(headers);
    }

    protected static String formatKey(String headerName) {
        return headerName.toLowerCase();
    }

    public String getValue(String header, String defaultValue) {
        final Header h = isEmpty() ? null : headers.get(formatKey(header));
        return h != null ? h.getValue() : defaultValue;
    }

    public String getValue(String header) {
        return getValue(header, null);
    }

    @Override
    public Iterator<Header> iterator() {
        return isEmpty() ? Collections.<Header>emptySet().iterator() : headers.values().iterator();
    }

    @Override
    public int size() {
        return isEmpty() ? 0 : headers.size();
    }

    @Override
    public String toString() {
        return headers == null ? "" : headers.toString();
    }

    @Override
    public boolean isEmpty() {
        return headers == null || headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return !isEmpty() && key != null && headers.containsKey(formatKey(key.toString()));
    }

    @Override
    public boolean containsValue(Object value) {
        return headers != null && headers.containsValue(value);
    }

    @Override
    public Header get(Object key) {
        return isEmpty() || key == null ? null : headers.get(formatKey(key.toString()));
    }

    @Override
    public Header put(String key, Header value) {
        throw new UnsupportedOperationException("The Headers class is a read-only map." +
                " You cannot update or remove values.");
    }

    @Override
    public Header remove(Object key) {
        throw new UnsupportedOperationException("The Headers class is a read-only map." +
                " You cannot update or remove values.");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Header> m) {
        throw new UnsupportedOperationException("The Headers class is a read-only map." +
                " You cannot update or remove values.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("The Headers class is a read-only map." +
                " You cannot update or remove values.");
    }

    @Override
    public Set<String> keySet() {
        return isEmpty() ? Collections.<String>emptySet() : headers.keySet();
    }

    @Override
    public Collection<Header> values() {
        return isEmpty() ? Collections.<Header>emptySet() : headers.values();
    }

    @Override
    public Set<Entry<String, Header>> entrySet() {
        return isEmpty() ? Collections.<Entry<String, Header>>emptySet() : headers.entrySet();
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    /**
     * Adds a header to this container.
     *
     * @param header The header to be added
     */
    protected void add(Header header) {
        if (header == null) {
            throw new IllegalArgumentException("Header argument cannot be null.");
        }

        if (header.getValue() == null) {
            if (headers == null) return;
            ensureHeaders().remove(header.getName());
        } else {
            ensureHeaders().put(formatKey(header.getName()), header);
        }
    }

    /**
     * Adds a new header with the given name-value pair, or removes the header with the given name if the value is null.
     *
     * @param name  Name of the header
     * @param value Value of the header
     */
    protected void set(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name cannot be null.");
        }

        if (value == null) {
            if (headers == null) return;
            ensureHeaders().remove(name);
        } else {
            ensureHeaders().put(formatKey(name), new SimpleHeader(name, value));
        }
    }

    /**
     * If there's a header with the given name, then it is removed and returned.
     *
     * @param name The name of the header to remove
     *
     * @return The removed header or null if there was no header with the given name
     */
    protected Header pop(String name) {
        return isEmpty() || name == null ? null : ensureHeaders().remove(name);
    }

    private Map<String, Header> ensureHeaders() {
        if (headers == null) {
            headers = concurrent ? new ConcurrentHashMap<String, Header>() : new HashMap<String, Header>();
        }
        return headers;
    }
}
