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
package io.reinert.requestor.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds retry options.
 *
 * @author Danilo Reinert
 */
class RetryPolicyImpl implements RetryPolicy {

    private int[] delays;
    private List<RequestEvent> events;
    int delayIndex;

    RetryPolicyImpl(int[] delaysMillis, RequestEvent[] eventsArray) {
        delays = delaysMillis;
        events = Arrays.asList(eventsArray);
    }

    @Override
    public int retryIn(RequestOptions request, RequestEvent event, int counter) {
        if (delayIndex < delays.length) {
            final List<RequestEvent> occurredEvents = getEventTree(event);
            occurredEvents.retainAll(events);
            if (!occurredEvents.isEmpty()) {
                return delays[delayIndex++];
            }
        }

        return -1;
    }

    private List<RequestEvent> getEventTree(RequestEvent event) {
        final List<RequestEvent> eventsTree = new ArrayList<RequestEvent>();

        do {
            eventsTree.add(event);
            event = event.getParent();
        } while (event != null);

        return eventsTree;
    }
}
