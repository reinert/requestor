/*
 * Copyright 2021 Danilo Reinert
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

class PollingOptions implements HasPollingOptions {

    private boolean pollingActive;
    private int pollingInterval;
    private int pollingLimit;
    private int pollingCount;
    private PollingStrategy pollingStrategy;

    public boolean isPolling() {
        return pollingActive;
    }

    public static PollingOptions copy(PollingOptions options) {
        PollingOptions copy = new PollingOptions();
        copy.pollingActive = options.pollingActive;
        copy.pollingInterval = options.pollingInterval;
        copy.pollingLimit = options.pollingLimit;
        copy.pollingCount = options.pollingCount;
        copy.pollingStrategy = options.pollingStrategy;
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
        pollingCount++;

        if (pollingLimit > 0) {
            pollingActive = pollingCount < pollingLimit;
        }

        return pollingCount;
    }

    public void reset() {
        pollingActive = false;
        pollingInterval = 0;
        pollingLimit = 0;
        pollingCount = 0;
        pollingStrategy = PollingStrategy.SHORT;
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
