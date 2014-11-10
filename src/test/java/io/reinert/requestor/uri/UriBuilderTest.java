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
package io.reinert.requestor.uri;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author Danilo Reinert
 */
public class UriBuilderTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorTest";
    }

    public void testBasicFlow() {
        String expected = "http://user:pwd@localhost:8888/server/root/resource;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#first";

        String uri = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("server")
                .segment("root", "resource")
                .matrixParam("class", 2, 5, 6)
                .segment("child")
                .matrixParam("group", "A")
                .matrixParam("subGroup", "A.1", "A.2")
                .queryParam("age", 12)
                .queryParam("name", "Aa", "Zz")
                .fragment("first")
                .build().toString();

        assertEquals(expected, uri);
    }

    public void testCommaSeparatedStrategy() {
        String expected = "/server/root;class=2,5,6" +
                "/child;group=A;subGroup=A.1,A.2?age=12&name=Aa,Zz#first";

        String uri = UriBuilder.fromPath("server")
                .multivaluedParamComposition(MultivaluedParamComposition.COMMA_SEPARATED_VALUE)
                .segment("root")
                .matrixParam("class", 2, 5, 6)
                .segment("child")
                .matrixParam("group", "A")
                .matrixParam("subGroup", "A.1", "A.2")
                .queryParam("age", 12)
                .queryParam("name", "Aa", "Zz")
                .fragment("first")
                .build().toString();

        assertEquals(expected, uri);
    }

    public void testTemplateParams() {
        String expected = "http://user:pwd@localhost:8888/server/root/any;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#firstserver";

        String uri = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("{a}/{b}")
                .segment("{c}")
                .matrixParam("class", 2, 5, 6)
                .segment("child")
                .matrixParam("group", "A")
                .matrixParam("subGroup", "A.1", "A.2")
                .queryParam("age", 12)
                .queryParam("name", "Aa", "Zz")
                .fragment("{d}{a}")
                .build("server", "root", "any", "first").toString();

        assertEquals(expected, uri);
    }

    public void testInsufficientTemplateParams() {
        try {
            assertNull(UriBuilder.newInstance()
                    .path("{a}/{b}")
                    .segment("{c}")
                    .fragment("{d}{a}")
                    .build("server", "root", "any").toString());
        } catch (UriBuilderException e) {
            assertNotNull(e);
        }
    }
}
