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
package io.reinert.requestor.uri;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.junit.client.GWTTestCase;

import org.junit.Test;

/**
 * @author Danilo Reinert
 */
public class UriBuilderTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "io.reinert.requestor.RequestorApiTest";
    }

    @Test
    public void testSimple() {
        String expected = "http://user:pwd@localhost:8888/server/resource#first";

        String uri = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("server")
                .segment("resource")
                .fragment("first")
                .build().toString();

        assertEquals(expected, uri);
    }

    @Test
    public void testComplete() {
        String expected = "http://user:pwd@localhost:8888/server/root/resource;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#first";

        String uri = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("/server/")
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

    @Test
    public void testTemplateParams() {
        String expected = "http://user:pwd@localhost:8888/server/root/any;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#firstserver";

        String uri = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("/{a}/{b}")
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

    @Test
    public void testInsufficientTemplateParams() {
        try {
            assertNull(UriBuilder.newInstance()
                    .path("{a}/{b}")
                    .segment("{c}")
                    .fragment("{d}{a}")
                    .build("server", "root", "any").toString());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testTemplateParamsByMap() {
        String expected = "http://user:pwd@localhost:8888/server/1/any;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#firstserver";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("a", "server");
        params.put("b", 1);
        params.put("c", "any");
        params.put("d", "first");

        String uri = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("/{a}/{b}")
                .segment("{c}")
                .matrixParam("class", 2, 5, 6)
                .segment("child")
                .matrixParam("group", "A")
                .matrixParam("subGroup", "A.1", "A.2")
                .queryParam("age", 12)
                .queryParam("name", "Aa", "Zz")
                .fragment("{d}{a}")
                .build(params).toString();

        assertEquals(expected, uri);
    }

    @Test
    public void testInsufficientTemplateParamsByMap() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("a", "server");
            params.put("b", 1);
            params.put("c", "any");

            assertNull(UriBuilder.newInstance()
                    .path("{a}/{b}")
                    .segment("{c}")
                    .fragment("{d}{a}")
                    .build(params).toString());
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}
