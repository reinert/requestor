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

public class PollOptions {

    private int pollInterval;
    private int pollLimit;
    private int pollCounter;

    public int getPollInterval() {
        return pollInterval;
    }

    public int getPollLimit() {
        return pollLimit;
    }

    public int getPollCounter() {
        return pollCounter;
    }

    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    public void setPollLimit(int pollLimit) {
        this.pollLimit = pollLimit;
    }

    public void setPollCounter(int pollCounter) {
        this.pollCounter = pollCounter;
    }

}
