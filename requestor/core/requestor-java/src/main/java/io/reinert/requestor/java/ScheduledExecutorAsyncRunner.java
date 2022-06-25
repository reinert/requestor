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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reinert.requestor.core.AsyncRunner;

/**
 * AsyncRunner powered by a {@link ScheduledExecutorService}.
 *
 * @author Danilo Reinert
 */
public class ScheduledExecutorAsyncRunner implements AsyncRunner {

    private final ScheduledExecutorService scheduledExecutorService;

    public ScheduledExecutorAsyncRunner(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void run(final Runnable runnable, int delayMillis) {
        scheduledExecutorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
