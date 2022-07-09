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
package io.reinert.requestor.java;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reinert.requestor.core.AsyncRunner;

/**
 * AsyncRunner powered by a {@link ScheduledExecutorService}.
 *
 * @author Danilo Reinert
 */
public class ScheduledExecutorAsyncRunner implements AsyncRunner {

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

    private static final int DEFAULT_CORE_POOL_SIZE = 10;

    private final ScheduledExecutorService scheduledExecutorService;

    public ScheduledExecutorAsyncRunner() {
        scheduledExecutorService = Executors.newScheduledThreadPool(DEFAULT_CORE_POOL_SIZE);
    }

    public ScheduledExecutorAsyncRunner(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    @Override
    public void run(final Runnable runnable, int delayMillis) {
        scheduledExecutorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    @Override
    public Lock getLock() {
        return new Lock();
    }
}
