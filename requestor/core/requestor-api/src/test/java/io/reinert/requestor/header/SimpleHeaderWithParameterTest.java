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
package io.reinert.requestor.header;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author Danilo Reinert
 */
public class SimpleHeaderWithParameterTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorApiTest";
    }

    public void testGetValue() {
        final String expected = "text/html; charset=ISO-8859-4";
        final SimpleHeaderWithParameter header = new SimpleHeaderWithParameter("Content-Type", "text/html",
                SimpleHeaderWithParameter.Param.of("charset", "ISO-8859-4"));
        assertEquals(expected, header.getValue());
    }
}
