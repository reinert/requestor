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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
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
    public void testFormDataUrlEncoded() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new JavaNetSession();

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
    public void testFormDataMultiPartPlainContent() throws Throwable {
        final TestResult result = new TestResult();

        final Session session = new JavaNetSession();

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
    public void testFormDataMultiPartBinaryContent() throws Throwable {
        final TestResult result = new TestResult();

        final JavaNetSession session = new JavaNetSession();

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
    public void testFile() throws Throwable {
        final TestResult result = new TestResult();

        final JavaNetSession session = new JavaNetSession();

        final File tempFile = Files.createTempFile("requestor-javanet-SerializationTest-testFile-", null).toFile();
        tempFile.deleteOnExit();

        final byte[] bytes = new byte[(session.getOutputBufferSize() * 2) + 1];
        Arrays.fill(bytes, (byte) 1);

        try (FileOutputStream stream = new FileOutputStream(tempFile)) {
            stream.write(bytes);
        }

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicLong bytesWritten = new AtomicLong(0);

        session.post("https://httpbin.org/post", tempFile)
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
    public void testInputStream() throws Throwable {
        final TestResult result = new TestResult();

        final JavaNetSession session = new JavaNetSession();

        final byte[] bytes = new byte[(session.getOutputBufferSize() * 2) + 1];
        Arrays.fill(bytes, (byte) 1);

        final InputStream inputStream = new ByteArrayInputStream(bytes);

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicLong bytesWritten = new AtomicLong(0);

        session.req("https://httpbin.org/post")
                .payload(inputStream)
                .save(RequestorJavaNet.WRITE_CHUNKING_ENABLED, true)
                .post()
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
