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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import io.reinert.requestor.core.RequestFilter;
import io.reinert.requestor.core.Session;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for basic request events.
 */
public class RequestEventTest extends NetTest {

    private static final int TIMEOUT = 5_000;

    //=========================================================================
    // LOAD EVENTS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testLoadEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/200")
                .onStatus(200, succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testSuccessEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/200")
                .onSuccess(succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testFailEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/400")
                .onSuccess(failOnEvent(result))
                .onFail(succeedOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testStatusEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.get("https://httpbin.org/status/200")
                .onStatus(200, succeedOnEvent(result))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    //=========================================================================
    // ERROR EVENTS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testAbortEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.register((RequestFilter) request -> {
            throw new RuntimeException("Request should be aborted.");
        });

        session.get("https://httpbin.org/status/200")
                .onLoad(failOnEvent(result))
                .onCancel(failOnError(result))
                .onTimeout(failOnTimeout(result))
                .onAbort(succeedOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testTimeoutEvent() {
        final TestResult result = new TestResult();

        final Session session = new NetSession();

        session.setTimeout(50);

        session.get("https://httpbin.org/delay/1")
                .onLoad(failOnEvent(result))
                .onAbort(failOnError(result))
                .onCancel(failOnError(result))
                .onTimeout(succeedOnTimeout(result));

        finishTest(result, TIMEOUT);
    }

    //=========================================================================
    // PROGRESS EVENTS
    //=========================================================================

    @Test(timeout = TIMEOUT)
    public void testUploadProgressEvent() {
        final TestResult result = new TestResult();

        final NetSession session = new NetSession();

        final byte[] payload = new byte[(session.getOutputBufferSize() * 2) + 1];
        Arrays.fill(payload, (byte) 1);

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicInteger bytesWritten = new AtomicInteger(0);

        session.post("https://httpbin.org/post", payload)
                .onUpProgress(p -> buffers[progressCalls.get()] = p.getChunk().asBytes())
                .onUpProgress(p -> bytesWritten.set(p.getLoaded()))
                .onUpProgress(p -> progressCalls.addAndGet(1))
                .onSuccess(test(result, () -> {
                    Assert.assertEquals(expectedProgressCalls, progressCalls.get());
                    Assert.assertEquals(payload.length, bytesWritten.get());

                    byte[] joinedBuffers = new byte[payload.length];
                    for (int i = 0, k = 0; i < expectedProgressCalls; i++) {
                        System.arraycopy(buffers[i], 0, joinedBuffers, k, buffers[i].length);
                        k += buffers[i].length;
                    }
                    Assert.assertArrayEquals(payload, joinedBuffers);
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }

    @Test(timeout = TIMEOUT)
    public void testDownloadProgressEvent() {
        final TestResult result = new TestResult();

        final NetSession session = new NetSession();
        session.setMediaType("application/octet-stream");

        final int expectedProgressCalls = 3;
        final AtomicInteger progressCalls = new AtomicInteger(0);

        final String byteSize = String.valueOf((session.getInputBufferSize() * 2) + 1);

        final byte[][] buffers = new byte[expectedProgressCalls][];

        final AtomicInteger bytesRead = new AtomicInteger(0);

        session.get("https://httpbin.org/bytes/" + byteSize, byte[].class)
                .onProgress(p -> buffers[progressCalls.get()] = p.getChunk().asBytes())
                .onProgress(p -> bytesRead.set(p.getLoaded()))
                .onProgress(p -> progressCalls.addAndGet(1))
                .onSuccess(test(result, (byte[] payload) -> {
                    Assert.assertEquals(expectedProgressCalls, progressCalls.get());
                    Assert.assertEquals(payload.length, bytesRead.get());

                    byte[] joinedBuffers = new byte[payload.length];
                    for (int i = 0, k = 0; i < expectedProgressCalls; i++) {
                        System.arraycopy(buffers[i], 0, joinedBuffers, k, buffers[i].length);
                        k += buffers[i].length;
                    }
                    Assert.assertArrayEquals(payload, joinedBuffers);
                }))
                .onFail(failOnEvent(result))
                .onError(failOnError(result));

        finishTest(result, TIMEOUT);
    }
}
