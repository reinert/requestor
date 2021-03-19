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

import java.util.Iterator;

import com.google.gwtmockito.GwtMockitoTestRunner;

import io.reinert.requestor.Link;
import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriJreTestBase;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * @author Danilo Reinert
 */
@RunWith(GwtMockitoTestRunner.class)
public class LinkHeaderJreTest extends UriJreTestBase {

    @Test
    public void testMultipleValue() {
        final com.google.gwt.http.client.Header input = new com.google.gwt.http.client.Header() {
            @Override
            public String getName() {
                return "Link";
            }

            @Override
            public String getValue() {
                return "</TheBook/chapter2>; rel=\"previous\"; title*=UTF-8'de'letztes%20Kapitel, "
                        + "</TheBook/chapter4>; rel=\"next\"; title*=UTF-8'de'n%c3%a4chstes%20Kapitel";
            }
        };

        final LinkHeader output = (LinkHeader) Header.fromRawHeader(input);
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
