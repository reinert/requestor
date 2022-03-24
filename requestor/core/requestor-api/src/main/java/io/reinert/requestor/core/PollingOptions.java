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
 * It manages polling options.
 *
 * @author Danilo Reinert
 */
class PollingOptions implements HasPollingOptions {

    private int pollingInterval;
    private int pollingLimit;
    private PollingStrategy pollingStrategy;

    private volatile int pollingCount;
    private volatile boolean pollingActive;

    public boolean isPolling() {
        return pollingActive;
    }

    public static PollingOptions copy(PollingOptions options) {
        PollingOptions copy = new PollingOptions();
        copy.pollingInterval = options.pollingInterval;
        copy.pollingLimit = options.pollingLimit;
        copy.pollingStrategy = options.pollingStrategy;
        copy.pollingCount = options.pollingCount;
        copy.pollingActive = options.pollingActive;
        return copy;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public int getPollingLimit() {
        return pollingLimit;
    }

    public int getPollingCount() {
        return pollingCount;
    }

    public PollingStrategy getPollingStrategy() {
        return pollingStrategy;
    }

    public void setPollingActive(boolean pollingActive) {
        this.pollingActive = pollingActive;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public void setPollingLimit(int pollingLimit) {
        this.pollingLimit = pollingLimit;
    }

    public void setPollingStrategy(PollingStrategy pollingStrategy) {
        this.pollingStrategy = pollingStrategy;
    }

    public int incrementPollingCount() {
        synchronized (this) {
            pollingCount++;
        }

        if (pollingLimit > 0) {
            pollingActive = pollingCount < pollingLimit;
        }

        return pollingCount;
    }

    public void reset() {
        pollingInterval = 0;
        pollingLimit = 0;
        pollingStrategy = PollingStrategy.SHORT;
        pollingCount = 0;
        pollingActive = false;
    }

    public void startPolling(PollingStrategy strategy, int pollingInterval, int pollingLimit) {
        if (strategy == null) throw new IllegalArgumentException("PollingStrategy cannot be null");
        this.pollingStrategy = strategy;
        this.pollingInterval = pollingInterval;
        this.pollingLimit = pollingLimit;
        this.pollingCount = 0;
        this.pollingActive = true;
    }

    public void stopPolling() {
        this.pollingActive = false;
    }
}
