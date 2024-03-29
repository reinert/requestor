/*
 * Copyright 2021-2022 Danilo Reinert
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
package io.reinert.requestor.core;

/**
 * Functional interface that abstracts running a callback asynchronously.
 *
 * @author Danilo Reinert
 */
public interface AsyncRunner {

    interface Lock {
        void await(long timeout) throws InterruptedException;

        boolean isAwaiting();

        void signalAll();
    }

    void run(Runnable runnable, long delayMillis);

    void sleep(long millis);

    void shutdown();

    boolean isShutdown();

    Lock getLock();
}
