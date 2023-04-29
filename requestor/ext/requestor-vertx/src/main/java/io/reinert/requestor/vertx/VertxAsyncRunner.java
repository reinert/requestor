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

import io.reinert.requestor.core.AsyncRunner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;

/**
 * AsyncRunner that integrates Requestor with Vertx.
 *
 * @author Danilo Reinert
 */
public class VertxAsyncRunner implements AsyncRunner {

    public VertxAsyncRunner(Vertx vertx) {
        this.vertx = vertx;
    }
    private final Vertx vertx;

    @Override
    public void run(Runnable runnable, long delayMillis) {
        if (delayMillis < 1) {
            executeBlocking(runnable);
        } else {
            vertx.setTimer(delayMillis, ignored -> executeBlocking(runnable));
        }
    }

    @Override
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeBlocking(Runnable runnable) {
        vertx.executeBlocking(promise -> {
            runnable.run();
            promise.complete();
        }, VertxAsyncRunner::ignore);
    }

    private static void ignore(AsyncResult<?> res) {
        // no-op
    }

    @Override
    public void shutdown() {
        // no-op
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public Lock getLock() {
        return new Lock();
    }

    public static class Lock implements AsyncRunner.Lock {

        private volatile boolean awaiting = false;

        @Override
        public synchronized void await(long timeout) throws InterruptedException {
            awaiting = true;
            wait(timeout);
        }

        @Override
        public boolean isAwaiting() {
            return awaiting;
        }

        @Override
        public synchronized void signalAll() {
            notifyAll();
        }
    }
}
