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
package io.reinert.requestor.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.reinert.requestor.core.FormData;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for basic request events.
 */
public class SerializationTest extends NetTest {

    private static final int TIMEOUT = 10_000;

    @Test(timeout = TIMEOUT)
    public void testFormDataUrlEncoded() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        final FormData data = FormData.builder()
                .append("string", "value")
                .append("int", 1)
                .append("long", 10L)
                .append("double", 1.5)
                .append("boolean", true)
                .build();

        session.req("https://httpbin.org/post")
                .contentType("application/x-www-form-urlencoded")
                .payload(data)
                .post(String.class)
                .onSuccess(test(result, (String body, Response res) -> {
                    Assert.assertTrue(body.contains("\"Content-Type\": \"application/x-www-form-urlencoded\""));
                    Assert.assertTrue(body.contains("\"string\": \"value\""));
                    Assert.assertTrue(body.contains("\"int\": \"1\""));
                    Assert.assertTrue(body.contains("\"long\": \"10\""));
                    Assert.assertTrue(body.contains("\"double\": \"1.5\""));
                    Assert.assertTrue(body.contains("\"boolean\": \"true\""));
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testFormDataMultiPartPlainContent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        final FormData data = FormData.builder()
                .append("string", "value")
                .append("int", 1)
                .append("long", 10L)
                .append("double", 1.5)
                .append("boolean", true)
                .build();

        session.req("https://httpbin.org/post")
                .contentType("multipart/form-data")
                .payload(data)
                .post(String.class)
                .onSuccess(test(result, (String body, Response res) -> {
                    Assert.assertTrue(body.contains("\"Content-Type\": \"multipart/form-data"));
                    Assert.assertTrue(body.contains("\"string\": \"value\""));
                    Assert.assertTrue(body.contains("\"int\": \"1\""));
                    Assert.assertTrue(body.contains("\"long\": \"10\""));
                    Assert.assertTrue(body.contains("\"double\": \"1.5\""));
                    Assert.assertTrue(body.contains("\"boolean\": \"true\""));
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testFormDataMultiPartBinaryContent() {
        final TestResult result = new TestResult();

        final NetSession session = new NetSession();

        final byte[] bytes = new byte[(session.getOutputBufferSize() * 2) + 1];
        Arrays.fill(bytes, (byte) 1);

        final InputStream is = new ByteArrayInputStream(bytes);

        final FormData data = FormData.builder()
                .append("string", "value")
                .append("int", 1)
                .append("long", 10L)
                .append("double", 1.5)
                .append("file", is, null)
                .append("boolean", true)
                .build();

        session.req("https://httpbin.org/post")
                .contentType("multipart/form-data")
                .payload(data)
                .post(String.class)
                .onSuccess(test(result, (String body, Response res) -> {
                    Assert.assertTrue(body.contains("\"Content-Type\": \"multipart/form-data"));
                    Assert.assertTrue(body.contains("\"string\": \"value\""));
                    Assert.assertTrue(body.contains("\"int\": \"1\""));
                    Assert.assertTrue(body.contains("\"long\": \"10\""));
                    Assert.assertTrue(body.contains("\"double\": \"1.5\""));
                    Assert.assertTrue(body.contains("\"boolean\": \"true\""));
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testFile() throws IOException {
        final TestResult result = new TestResult();

        final NetSession session = new NetSession();

        final String filePath = "/tmp/requestor-" + generateRandomString(32);

        final byte[] bytes = new byte[(session.getOutputBufferSize() * 2) + 1];
        Arrays.fill(bytes, (byte) 1);

        try (FileOutputStream stream = new FileOutputStream(filePath)) {
            stream.write(bytes);
        }

        final File file = new File(filePath);
        file.deleteOnExit();

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicLong bytesWritten = new AtomicLong(0);

        session.post("https://httpbin.org/post", file)
                .onWrite(p -> buffers[progressCalls.get()] = p.getChunk().asBytes())
                .onWrite(p -> bytesWritten.set(p.getLoaded()))
                .onWrite(p -> progressCalls.addAndGet(1))
                .onSuccess(test(result, () -> {
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
    }

    @Test(timeout = TIMEOUT)
    public void testInputStream() {
        final TestResult result = new TestResult();

        final NetSession session = new NetSession();

        final byte[] bytes = new byte[(session.getOutputBufferSize() * 2) + 1];
        Arrays.fill(bytes, (byte) 1);

        final InputStream inputStream = new ByteArrayInputStream(bytes);

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicLong bytesWritten = new AtomicLong(0);

        session.post("https://httpbin.org/post", inputStream)
                .onWrite(p -> buffers[progressCalls.get()] = p.getChunk().asBytes())
                .onWrite(p -> bytesWritten.set(p.getLoaded()))
                .onWrite(p -> progressCalls.addAndGet(1))
                .onSuccess(test(result, () -> {
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
