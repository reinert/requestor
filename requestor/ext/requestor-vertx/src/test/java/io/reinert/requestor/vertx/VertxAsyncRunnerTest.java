/*
 * Copyright 2023 Danilo Reinert
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
package io.reinert.requestor.vertx;

import java.util.concurrent.atomic.AtomicInteger;

import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.java.net.Requestor;
import io.vertx.core.Vertx;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for VertxAsyncRunner
 */
public class VertxAsyncRunnerTest {

    @Test
    public void testRunMethod() {
        AtomicInteger counter = new AtomicInteger();

        // Create a Vertx instance
        Vertx vertx = Vertx.vertx();

        // Create an instance of VertxAsyncRunner
        VertxAsyncRunner asyncRunner = new VertxAsyncRunner(vertx);

        // Define the delay in milliseconds
        long delayMillis = 2000;

        // Define the Runnable to be executed
        // Perform the desired actions
        Runnable runnable = counter::incrementAndGet;

        // Execute the run method
        asyncRunner.run(runnable, delayMillis);

        // Sleep for a sufficient amount of time to allow the execution to complete
        try {
            Thread.sleep(delayMillis + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        vertx.close();

        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testSessionRequest() throws RequestException {
        AtomicInteger counter = new AtomicInteger();

        Vertx vertx = Vertx.vertx();

        // Integrates requestor to vertx engine
        VertxAsyncRunner asyncRunner = new VertxAsyncRunner(vertx);

        // Start a new session with vertx async runner
        Session session = Requestor.newSession(asyncRunner);

        session.get("https://httpbin.org/ip", String.class)
                .onSuccess(counter::incrementAndGet)
                .await(); // hold main thread until request is done

        vertx.close();

        Assert.assertEquals(1, counter.get());
    }
}
