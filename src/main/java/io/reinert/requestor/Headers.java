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

import com.google.gwt.http.client.Header;

import org.turbogwt.core.collections.JsArrayList;
import org.turbogwt.core.collections.JsMapInteger;

/**
 * Stores the headers from a HTTP request/response.
 *
 * @author Danilo Reinert
 */
public class Headers implements Iterable<Header> {

    private final JsArrayList<Header> headers;
    private final JsMapInteger indexes = JsMapInteger.create();

    protected Headers() {
        this.headers = new JsArrayList<Header>();
    }

    Headers(Header... headers) {
        this.headers = new JsArrayList<Header>(headers);
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            indexes.set(header.getName(), i);
        }
    }

    public boolean contains(String header) {
        return indexes.contains(header);
    }

    public String getValue(String name) {
        final int i = indexes.get(name, -1);
        if (i == -1) return null;
        final Header header = headers.get(i);
        return header != null ? header.getValue() : null;
    }

    public Header get(String name) {
        return headers.get(indexes.get(name));
    }

    @Override
    public Iterator<Header> iterator() {
        return headers.iterator();
    }

    /**
     * Adds a header to this container and returns if the array has increased.
     *
     * @param header    The header to be added
     *
     * @return  {@code true} if there was not header set with the same header name, {@code false} otherwise
     */
    protected boolean add(Header header) {
        int i = indexes.get(header.getName(), -1);

        if (i > -1) {
            headers.set(i, header);
            return false;
        }

        indexes.set(header.getName(), headers.size());
        headers.add(header);
        return true;
    }

    /**
     * If there's a header with the given name, then it is removed and {@code true} is returned.
     *
     * @param name  The name of the header to remove
     *
     * @return  If a header with the given name was removed
     */
    protected boolean remove(String name) {
        int i = indexes.get(name, -1);

        if (i > -1) {
            indexes.remove(name);
            headers.remove(i);
            return true;
        }

        return false;
    }
}
