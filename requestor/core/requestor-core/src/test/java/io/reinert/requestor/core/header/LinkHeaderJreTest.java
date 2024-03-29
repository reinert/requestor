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
package io.reinert.requestor.core.header;

import java.util.Iterator;

import io.reinert.requestor.core.Link;
import io.reinert.requestor.core.uri.Uri;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Danilo Reinert
 */
public class LinkHeaderJreTest {

    @Test
    public void testMultipleValue() {
        final LinkHeader output = (LinkHeader) Header.fromRawHeader("Link",
                "</TheBook/chapter2>; rel=\"previous\"; title*=UTF-8'de'letztes%20Kapitel, "
                + "</TheBook/chapter4>; rel=\"next\"; title*=UTF-8'de'n%c3%a4chstes%20Kapitel");
        assertEquals("</TheBook/chapter2>; rel=\"previous\"; title*=UTF-8'de'letztes%20Kapitel, "
                + "</TheBook/chapter4>; rel=\"next\"; title*=UTF-8'de'n%c3%a4chstes%20Kapitel", output.getValue());

        final Iterable<Link> links = output.getLinks();
        final Iterator<Link> itl = links.iterator();
        final Link l0 = itl.next();
        final Link l1 = itl.next();
        assertEquals(Uri.create("/TheBook/chapter2"), l0.getUri());
        assertEquals("/TheBook/chapter2", l0.getUri().toString());
        assertEquals("previous", l0.getRel());
        assertEquals("UTF-8'de'letztes%20Kapitel", l0.getTitle());
        assertEquals(Uri.create("/TheBook/chapter4"), l1.getUri());
        assertEquals("/TheBook/chapter4", l1.getUri().toString());
        assertEquals("next", l1.getRel());
        assertEquals("UTF-8'de'n%c3%a4chstes%20Kapitel", l1.getTitle());

        final Iterable<Element> rawValues = output.getElements();
        final Iterator<Element> it = rawValues.iterator();
        assertEquals("</TheBook/chapter2>", it.next().getElement());
        assertEquals("</TheBook/chapter4>", it.next().getElement());
    }
}
