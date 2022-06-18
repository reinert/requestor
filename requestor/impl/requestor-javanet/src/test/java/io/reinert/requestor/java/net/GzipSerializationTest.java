/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.java.net;

import java.util.concurrent.Executors;

import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for basic request events.
 */
public class GzipSerializationTest extends JavaNetTest {

    private static final int TIMEOUT = 10_000;

    @Test(timeout = TIMEOUT)
    public void testGetGzippedWithLargeBuffer() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = Requestor.newSession(Executors.newSingleThreadScheduledExecutor());
        session.save(Requestor.GZIP_ENCODING_ENABLED, Boolean.TRUE);
        session.save(Requestor.INPUT_BUFFER_SIZE, 1024 * 8);

        session.req("https://httpbin.org/gzip")
                .get(String.class)
                .onSuccess(test(result, (String body, Response res) -> {
                    Assert.assertTrue(body.contains("\"gzipped\": true"));
                    Assert.assertTrue(body.contains("\"Accept-Encoding\": \"gzip\""));
                    Assert.assertTrue(body.contains("\"Content-Encoding\": \"gzip\""));
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testGetGzippedWithSmallBuffer() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = Requestor.newSession(Executors.newSingleThreadScheduledExecutor());
        session.save(Requestor.GZIP_ENCODING_ENABLED, Boolean.TRUE);
        session.save(Requestor.INPUT_BUFFER_SIZE, 128);

        session.req("https://httpbin.org/gzip")
                .get(String.class)
                .onSuccess(test(result, (String body, Response res) -> {
                    Assert.assertTrue(body.contains("\"gzipped\": true"));
                    Assert.assertTrue(body.contains("\"Accept-Encoding\": \"gzip\""));
                    Assert.assertTrue(body.contains("\"Content-Encoding\": \"gzip\""));
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }
}
