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

import com.google.gwt.junit.client.GWTTestCase;

import io.reinert.requestor.header.QualityFactorHeader;

/**
 * @author Danilo Reinert
 */
public class QualityFactorHeaderTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    public void testGetValue() {
        final String expected = "a/b, x/y+z; 0.2, k/l, n/m; 0.6";

        final QualityFactorHeader header = new QualityFactorHeader("name",
                new QualityFactorHeader.Value("a/b"),
                new QualityFactorHeader.Value(0.2, "x/y+z"),
                new QualityFactorHeader.Value("k/l"),
                new QualityFactorHeader.Value(0.6, "n/m"));

        assertEquals(expected, header.getValue());
    }
}
