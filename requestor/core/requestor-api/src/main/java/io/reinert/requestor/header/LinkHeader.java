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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The HTTP Link header.
 * <p/>
 * It's a multivalued header that provides access to the attributes related by official spec.
 *
 * @author Danilo Reinert
 */
public class LinkHeader extends MultivaluedHeader implements Iterable<Link> {

    private final Map<String, ? super Link> linksMap;
    private final Iterable<Element> elements;
    private final Iterable<Link> links;

    @SuppressWarnings("unchecked")
    public LinkHeader(Collection<Element> elements) {
        super("Link", Collections.EMPTY_LIST);
        linksMap = new HashMap<String, Link>(elements.size());
        for (Element e : elements) {
            final Link l = new Link(e);
            linksMap.put(l.getRel(), l);
        }
        links = (Iterable<Link>) linksMap.values();
        this.elements = (Iterable<Element>) linksMap.values();
    }

    public Iterable<Link> getLinks() {
        return links;
    }

    public boolean hasLink(String relation) {
        return linksMap.containsKey(relation);
    }

    public Link getLink(String relation) {
        return (Link) linksMap.get(relation);
    }

    @Override
    public Iterable<Element> getElements() {
        return elements;
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
    public Iterator<Link> iterator() {
        return links.iterator();
    }
}
