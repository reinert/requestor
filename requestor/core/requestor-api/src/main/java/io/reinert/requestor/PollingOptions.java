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
package io.reinert.requestor;

class PollingOptions {

    private boolean pollingActive;
    private int pollingInterval;
    private int pollingLimit;
    private int pollingCounter;
    private PollingStrategy pollingStrategy = PollingStrategy.SHORT;

    public boolean isPolling() {
        return pollingActive;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public int getPollingLimit() {
        return pollingLimit;
    }

    public int getPollingCounter() {
        return pollingCounter;
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

    public int incrementPollingCounter() {
        pollingCounter++;
        pollingActive = pollingCounter >= pollingLimit;
        return pollingCounter;
    }

    public PollingOptions copy() {
        PollingOptions copy = new PollingOptions();
        copy.pollingActive = this.pollingActive;
        copy.pollingInterval = this.pollingInterval;
        copy.pollingCounter = this.pollingCounter;
        copy.pollingStrategy = this.pollingStrategy;
        return copy;
    }

    public void reset() {
        pollingActive = false;
        pollingInterval = 0;
        pollingLimit = 0;
        pollingCounter = 0;
        pollingStrategy = PollingStrategy.SHORT;
    }

    public void startPolling(PollingStrategy strategy, int pollingInterval, int pollingLimit) {
        this.pollingActive = true;
        this.pollingInterval = pollingInterval;
        this.pollingLimit = pollingLimit;
        this.pollingCounter = 0;
        this.pollingStrategy = strategy;
    }

    public void stopPolling() {
        this.pollingActive = false;
    }
}
