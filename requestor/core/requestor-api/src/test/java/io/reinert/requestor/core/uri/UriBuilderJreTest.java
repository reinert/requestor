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
package io.reinert.requestor.core.uri;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Danilo Reinert
 */
public class UriBuilderJreTest {

    @Test
    public void build_PathOnly_ShouldBuildSuccessfully() {
        String expected = "/server";

        String uri = UriBuilder.newInstance()
                .path("server")
                .build().toString();

        assertEquals(expected, uri);
    }

    @Test
    public void build_AllMethodsExcludingWithParamsProperlyUsed_ShouldBuildSuccessfully() {
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
    public void build_AllMethodsProperlyUsed_ShouldBuildSuccessfully() {
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
    public void build_ProperTemplateValues_ShouldBuildSuccessfully() {
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

    @Test(expected = IllegalArgumentException.class)
    public void build_InsufficientTemplateValues_ShouldThrowIllegalArgumentException() {
        UriBuilder.newInstance()
                .path("{a}/{b}")
                .segment("{c}")
                .fragment("{d}{a}")
                .build("server", "root", "any");
    }

    @Test
    public void build_ProperTemplateValuesMap_ShouldBuildSuccessfully() {
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

    @Test(expected = IllegalArgumentException.class)
    public void build_InsufficientTemplateValuesMap_ShouldThrowIllegalArgumentException() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("a", "server");
        params.put("b", 1);
        params.put("c", "any");

        UriBuilder.newInstance()
                .path("{a}/{b}")
                .segment("{c}")
                .fragment("{d}{a}")
                .build(params);
    }

    @Test
    public void clone_ShouldReturnDetachedInstance() {
        String expectedOriginal = "http://user:pwd@localhost:8888/server/root/resource;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#first";
        String expectedClone = "http://user:pwd@localhost:8888/server?foo=bar";

        // Given
        UriBuilder original = UriBuilder.newInstance()
                .scheme("http")
                .user("user")
                .password("pwd")
                .host("localhost")
                .port(8888)
                .path("/server/");

        UriBuilder clone = original.clone();

        // When
        original.segment("root", "resource")
                .matrixParam("class", 2, 5, 6)
                .segment("child")
                .matrixParam("group", "A")
                .matrixParam("subGroup", "A.1", "A.2")
                .queryParam("age", 12)
                .queryParam("name", "Aa", "Zz")
                .fragment("first");

        clone.queryParam("foo", "bar");

        // Then
        assertEquals(expectedOriginal, original.build().toString());
        assertEquals(expectedClone, clone.build().toString());
    }

    @Test
    public void fromPath_WithTemplates_ShouldNotEscapeTemplates() {
        final String expected = "/server/resource/1";

        final Uri uri = UriBuilder.fromPath("server/{res}/{id}").build("resource", 1);

        assertEquals(expected, uri.toString());
    }

    @Test
    public void fromUri_WithoutMatrixAndQueryParams_ShouldBuildSuccessfully() {
        final String expected = "http://user:pwd@localhost:8888/server/resource#first";

        final Uri uri = UriBuilder.fromUri(Uri.create(expected)).build();

        assertEquals(expected, uri.toString());
    }

    @Test
    public void fromUri_WithIpHost_ShouldBuildSuccessfully() {
        final String expected = "http://127.0.0.1:8888/requestor";

        final Uri uri = UriBuilder.fromUri(Uri.create(expected)).build();

        assertEquals(expected, uri.toString());
    }
}
