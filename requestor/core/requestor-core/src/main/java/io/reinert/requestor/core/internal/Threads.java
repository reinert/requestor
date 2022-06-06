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
package io.reinert.requestor.core.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * A utility class that provides thread methods not compatible with GWT.
 *
 * @author Danilo Reinert
 */
public class Threads {

    public static void notifyAll(Object monitor) {
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    public static void waitSafely(Object monitor, long timeout, Callable<Boolean> condition)
            throws InterruptedException, TimeoutException {
        final long startTime = System.currentTimeMillis();
        synchronized (monitor) {
            while (isTrue(condition)) {
                try {
                    if (timeout <= 0) {
                        monitor.wait();
                    } else {
                        final long elapsed = (System.currentTimeMillis() - startTime);
                        final long waitTime = timeout - elapsed;
                        monitor.wait(waitTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                }

                if (timeout > 0 && (System.currentTimeMillis() - startTime) >= timeout) {
                    Thread.currentThread().interrupt();
                    throw new TimeoutException("The timeout of " + timeout + "ms has expired.");
                }
            }
        }
    }

    private static boolean isTrue(Callable<Boolean> condition) {
        try {
            return condition.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}