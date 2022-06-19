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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPOutputStream;

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

    @Test(timeout = TIMEOUT)
    public void testGzipInputStream() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = Requestor.newSession(Executors.newSingleThreadScheduledExecutor());
        session.save(Requestor.GZIP_ENCODING_ENABLED, Boolean.TRUE);

        JavaNetRequestDispatcherFactory dispatcherFactory =
                (JavaNetRequestDispatcherFactory) session.getRequestDispatcherFactory();

        final byte[] bytes = new byte[(dispatcherFactory.getOutputBufferSize() * 2) + 1];
        new Random().nextBytes(bytes);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        final GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(bytes);
        gzip.close();
        final String expected = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();

        final InputStream inputStream = new ByteArrayInputStream(bytes);

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicLong bytesWritten = new AtomicLong(0);

        session.req("https://httpbin.org/post")
                .payload(inputStream)
                .save(Requestor.WRITE_CHUNKING_ENABLED, true)
                .post(String.class)
                .onWrite(p -> buffers[progressCalls.get()] = p.getChunk().asBytes())
                .onWrite(p -> bytesWritten.set(p.getLoaded()))
                .onWrite(p -> progressCalls.addAndGet(1))
                .onSuccess(test(result, (String body) -> {
                    String content = body.substring(body.indexOf("\"data\"") + 46, body.indexOf("\"files\"") - 6);
                    Assert.assertEquals(expected, content);

                    Assert.assertEquals(expectedProgressCalls, progressCalls.get());
                    Assert.assertEquals(bytes.length, bytesWritten.get());

                    byte[] joinedBuffers = new byte[bytes.length];
                    for (int i = 0, k = 0; i < expectedProgressCalls; i++) {
                        System.arraycopy(buffers[i], 0, joinedBuffers, k, buffers[i].length);
                        k += buffers[i].length;
                    }
                    Assert.assertArrayEquals(bytes, joinedBuffers);
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }
}
