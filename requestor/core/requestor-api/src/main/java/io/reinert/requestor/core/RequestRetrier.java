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
import java.util.List;

/**
 * Responsible for retrying a request.
 *
 * @author Danilo Reinert
 */
public class RequestRetrier {

    private final PreparedRequest preparedRequest;
    private final RunScheduler scheduler;
    private final RetryOptions retryOptions;
    private final List<String> eventsNames;
    private int delayIndex = 0;

    RequestRetrier(PreparedRequest preparedRequest, RunScheduler scheduler, RetryOptions retryOptions) {
        this.preparedRequest = preparedRequest;
        this.scheduler = scheduler;
        this.retryOptions = retryOptions;
        eventsNames = new ArrayList<String>(retryOptions.getEvents().size());
        for (Event e : retryOptions.getEvents()) eventsNames.add(e.getName());
    }

    public int getRetryCount() {
        return delayIndex;
    }

    public boolean maybeRetry(Response response) {
        return maybeRetry(getEventsFromResponse(response));
    }

    public boolean maybeRetry(RequestException exception) {
        return maybeRetry(getEventsFromException(exception));
    }

    private List<String> getEventsFromResponse(Response response) {
        final List<String> events = new ArrayList<String>();

        events.add("load");
        events.add(String.valueOf(response.getStatusCode()));

        int familyCode = response.getStatusCode() / 100;
        events.add(String.valueOf(familyCode));

        if (familyCode == 2) {
            events.add("success");
        } else {
            events.add("fail");
        }

        return events;
    }

    private List<String> getEventsFromException(RequestException exception) {
        final List<String> events = new ArrayList<String>();

        events.add("error");

        if (exception instanceof RequestTimeoutException) {
            events.add("timeout");
        } else if (exception instanceof RequestCancelException) {
            events.add("cancel");
        } else if (exception instanceof RequestAbortException) {
            events.add("abort");
        }

        return events;
    }

    private boolean maybeRetry(List<String> occurredEvents) {
        if (delayIndex < retryOptions.getDelays().size()) {
            occurredEvents.retainAll(eventsNames);
            if (!occurredEvents.isEmpty()) {
                scheduler.scheduleRun(new Runnable() {
                    @Override
                    public void run() {
                        preparedRequest.send();
                    }
                }, retryOptions.getDelays().get(delayIndex++));
                return true;
            }
        }
        return false;
    }
}
