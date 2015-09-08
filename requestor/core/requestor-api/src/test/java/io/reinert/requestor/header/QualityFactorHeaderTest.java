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
public class QualityFactorHeaderTest {

    @Test
    public void testGetValue() {
        final String expected = "a/b, x/y+z; 0.2, k/l, n/m; 0.6";
        final QualityFactorHeader header = new QualityFactorHeader("ign", "a/b", 1, "x/y+z", 0.2, "k/l", 1, "n/m", 0.6);
        assertEquals(expected, header.getValue());

        // TODO: split
        // Builds Content-Type: application/json; charset=utf-8
        ContentTypeHeader contentType = new ContentTypeHeader("application/json", Param.of("charset", "utf-8"));
        assertEquals(contentType.toString(), "Content-Type: application/json; charset=utf-8");

        // Builds Accept-Language: fr, en-gb;q=0.8, en;q=0.7
        QualityFactorHeader acceptLang = new QualityFactorHeader("Accept-Language", "fr", 1, "en-gb", 0.8, "en", 0.7);
        assertEquals(acceptLang.toString(), "Accept-Language: fr, en-gb; 0.8, en; 0.7");

        // Builds Accept: application/json, */javascript;0.9, application/xml;0.8
        AcceptHeader accept = new AcceptHeader("application/json", 1, "*/javascript", 0.9, "application/xml", 0.8);
        assertEquals(accept.toString(), "Accept: application/json, */javascript; 0.9, application/xml; 0.8");
    }
}
