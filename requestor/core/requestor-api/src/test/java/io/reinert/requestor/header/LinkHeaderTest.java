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
    public void testUriConstructor() {
        final String expected = "<http://example.com/TheBook/chapter2>; rel=\"previous\"";
        final LinkHeader header = new LinkHeader("http://example.com/TheBook/chapter2", "previous");
        assertEquals(expected, header.getValue());
    }

    @Test
    public void testUriRelConstructor() {
        final String expected = "<http://example.com/TheBook/chapter2>; rel=\"previous\"";
        final LinkHeader header = new LinkHeader("http://example.com/TheBook/chapter2", "previous");
        assertEquals(expected, header.getValue());
    }
}
