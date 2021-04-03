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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reinert.requestor.Link;

/**
 * The HTTP Link header.
 * <p/>
 * It's a multivalued header that provides access to the attributes related by official spec.
 *
 * @author Danilo Reinert
 */
public class LinkHeader extends MultivaluedHeader implements Iterable<Link> {

    private final Map<String, ? super LinkElement> linksMap;

    @SuppressWarnings("unchecked")
    public LinkHeader(Collection<Element> elements) {
        super("Link", Collections.EMPTY_LIST);
        this.linksMap = new HashMap<String, LinkElement>(elements.size());
        for (Element e : elements) {
            final LinkElement l = new LinkElement(e);
            // It stores one link element per rel
            // TODO: support many elements per rel
            linksMap.put(l.getRel(), l);
        }
    }

    @SuppressWarnings("unchecked")
    public Iterable<Link> getLinks() {
        return (Iterable<Link>) linksMap.values();
    }

    public boolean hasLink(String relation) {
        return linksMap.containsKey(relation);
    }

    public Link getLink(String relation) {
        return (Link) linksMap.get(relation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<Element> getElements() {
        return (Iterable<Element>) linksMap.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final LinkHeader that = (LinkHeader) o;

        if (!linksMap.equals(that.linksMap))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return linksMap.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Link> iterator() {
        return (Iterator<Link>) linksMap.values().iterator();
    }
}
