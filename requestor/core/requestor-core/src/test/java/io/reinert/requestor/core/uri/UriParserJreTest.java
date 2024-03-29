/*
 * Copyright 2015-2022 Danilo Reinert
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

import java.util.Arrays;

import io.reinert.requestor.core.Base64Codec;
import io.reinert.requestor.core.RequestorCore;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Danilo Reinert
 */
public class UriParserJreTest {

    @BeforeClass
    public static void setUpRequestor() {
        if (!RequestorCore.isInitialized()) {
            RequestorCore.init(new Base64Codec() {
                public String decode(String encoded, String charset) {
                    return encoded;
                }

                public String decode(byte[] encoded, String charset) {
                    throw new UnsupportedOperationException();
                }

                public String encode(String text, String charset) {
                    return text;
                }
            }, new NetUriCodec());
        }
    }

    @Test
    public void testRootPath() {
        final String expected = "/";

        final Uri uri = UriParser.parse(expected);

        assertNull(uri.getScheme());
        assertNull(uri.getUser());
        assertNull(uri.getPassword());
        assertNull(uri.getHost());
        assertTrue(uri.getPort() < 0);
        assertEquals("/", uri.getPath());
        assertNull(uri.getQuery());
        assertNull(uri.getFragment());
        assertEquals(expected, uri.toString());
    }

    @Test
    public void testPathOnly() {
        final String expected = "/server/resource";

        final Uri uri = UriParser.parse(expected);

        assertNull(uri.getScheme());
        assertNull(uri.getUser());
        assertNull(uri.getPassword());
        assertNull(uri.getHost());
        assertTrue(uri.getPort() < 0);
        assertEquals("/server/resource", uri.getPath());
        assertNull(uri.getQuery());
        assertNull(uri.getFragment());
        assertEquals(expected, uri.toString());
    }

    @Test
    public void testPathAndQuery() {
        final String expected = "/server/resource?age=12&name=Aa&name=Zz";

        final Uri uri = UriParser.parse(expected);

        assertNull(uri.getScheme());
        assertNull(uri.getUser());
        assertNull(uri.getPassword());
        assertNull(uri.getHost());
        assertTrue(uri.getPort() < 0);
        assertEquals("/server/resource", uri.getPath());
        assertEquals("12", uri.getQueryParam("age").getValue());
        assertTrue(Arrays.equals(new String[]{"Aa", "Zz"}, uri.getQueryParam("name").getValues().toArray()));
        assertNull(uri.getFragment());
        assertEquals(expected, uri.toString());
    }

    @Test
    public void testSimple() {
        final String expected = "http://user:pwd@localhost:8888/server/resource#first";

        final Uri uri = UriParser.parse(expected);

        assertEquals("http", uri.getScheme());
        assertEquals("user", uri.getUser());
        assertEquals("pwd", uri.getPassword());
        assertEquals("localhost", uri.getHost());
        assertEquals(8888, uri.getPort());
        assertEquals("/server/resource", uri.getPath());
        assertEquals("first", uri.getFragment());
        assertEquals(expected, uri.toString());
    }

    @Test
    public void testComplete() {
        final String expected = "http://user:pwd@localhost:8888/server/root/resource;class=2;class=5;class=6" +
                "/child;group=A;subGroup=A.1;subGroup=A.2?age=12&name=Aa&name=Zz#first";

        final Uri uri = UriParser.parse(expected);

        assertEquals("http", uri.getScheme());
        assertEquals("user", uri.getUser());
        assertEquals("pwd", uri.getPassword());
        assertEquals("localhost", uri.getHost());
        assertEquals(8888, uri.getPort());
        assertEquals("/server/root/resource;class=2;class=5;class=6/child;group=A;subGroup=A.1;subGroup=A.2",
                uri.getPath());
        assertEquals(1, uri.getMatrixParams("resource").size());
        assertTrue(Arrays.equals(new String[]{"2", "5", "6"},
                uri.getMatrixParam("resource", "class").getValues().toArray()));
        assertEquals(2, uri.getMatrixParams("child").size());
        assertEquals("A", uri.getMatrixParam("child", "group").getValue());
        assertTrue(Arrays.equals(new String[]{"A.1", "A.2"},
                uri.getMatrixParam("child", "subGroup").getValues().toArray()));
        assertEquals(2, uri.getQueryParams().size());
        assertEquals("12", uri.getQueryParam("age").getValue());
        assertTrue(Arrays.equals(new String[]{"Aa", "Zz"}, uri.getQueryParam("name").getValues().toArray()));
        assertEquals("first", uri.getFragment());
        assertEquals(expected, uri.toString());
    }

    @Test
    public void testIpHost() {
        final String expected = "http://127.0.0.1:8888/requestor";

        final Uri uri = UriParser.parse(expected);

        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(8888, uri.getPort());
        assertEquals("/requestor", uri.getPath());
        assertEquals(expected, uri.toString());
    }
}
