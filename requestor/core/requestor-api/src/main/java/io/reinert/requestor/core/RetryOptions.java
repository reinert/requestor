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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RetryOptions {

    private List<Integer> delays;
    private List<RequestEvent> events;

    RetryOptions(int[] delaysMillis, RequestEvent[] eventsArray) {
        delays = new ArrayList<Integer>(delaysMillis.length);
        for (int i = 0; i < delaysMillis.length; i++) delays.add(delaysMillis[i]);

        events = new ArrayList<RequestEvent>(eventsArray.length);
        for (int i = 0; i < eventsArray.length; i++) if (eventsArray[i] != null) events.add(eventsArray[i]);
    }

    RetryOptions(List<Integer> delays, List<RequestEvent> events) {
        this.delays = delays;
        this.events = events;
    }

    static RetryOptions copy(RetryOptions options) {
        return new RetryOptions(new ArrayList<Integer>(options.delays), new ArrayList<RequestEvent>(options.events));
    }

    public List<Integer> getDelays() {
        return delays != null ? Collections.<Integer>unmodifiableList(delays) : Collections.<Integer>emptyList();
    }

    public List<RequestEvent> getEvents() {
        return events != null ? Collections.<RequestEvent>unmodifiableList(events) :
                Collections.<RequestEvent>emptyList();
    }

    public boolean isEnabled() {
        return delays.size() > 0 && events.size() > 0;
    }
}
