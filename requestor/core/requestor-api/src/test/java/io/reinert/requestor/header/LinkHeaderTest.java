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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Danilo Reinert
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkHeaderTest {

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

        LinkHeader output = (LinkHeader) Header.from(input);

        final LinkHeader.Link[] links = output.getLinks();

        assertEquals("/TheBook/chapter2", links[0].getUri());
        assertEquals("previous", links[0].getRel());
        assertEquals("UTF-8'de'letztes%20Kapitel", links[0].getTitle());

        assertEquals("/TheBook/chapter4", links[1].getUri());
        assertEquals("next", links[1].getRel());
        assertEquals("UTF-8'de'n%c3%a4chstes%20Kapitel", links[1].getTitle());
    }
}
