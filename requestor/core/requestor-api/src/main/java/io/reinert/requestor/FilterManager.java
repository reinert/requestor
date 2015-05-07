/*
 * Copyright 2014 Danilo Reinert
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A manager for {@link RequestFilter} and {@link ResponseFilter}.
 *
 * @author Danilo Reinert
 */
class FilterManager implements HasFilters {

    private final List<RequestFilter> requestFilters = new ArrayList<RequestFilter>();
    private final List<ResponseFilter> responseFilters = new ArrayList<ResponseFilter>();

    @SuppressWarnings("unchecked")
    private List<RequestFilter> requestFiltersCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseFilter> responseFiltersCopy = Collections.EMPTY_LIST;

    public FilterManager() {
    }

    public FilterManager(FilterManager manager) {
        final List<RequestFilter> managerRequestFilters = manager.getRequestFilters();
        this.requestFilters.addAll(managerRequestFilters);
        this.requestFiltersCopy = managerRequestFilters; // It's an immutable list
        final List<ResponseFilter> managerResponseFilters = manager.getResponseFilters();
        this.responseFilters.addAll(managerResponseFilters);
        this.responseFiltersCopy = managerResponseFilters; // It's an immutable list
    }

    @Override
    public HandlerRegistration addRequestFilter(final RequestFilter requestFilter) {
        requestFilters.add(requestFilter);
        updateRequestFiltersCopy(); // getRequestFilters returns this immutable copy

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeRequestFilter(requestFilter);
            }
        };
    }

    @Override
    public HandlerRegistration addResponseFilter(final ResponseFilter responseFilter) {
        responseFilters.add(responseFilter);
        updateResponseFiltersCopy(); // getResponseFilters returns this immutable copy

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeResponseFilter(responseFilter);
            }
        };
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The request filters.
     */
    public List<RequestFilter> getRequestFilters() {
        return requestFiltersCopy;
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The response filters.
     */
    public List<ResponseFilter> getResponseFilters() {
        return responseFiltersCopy;
    }

    private void removeRequestFilter(RequestFilter requestFilter) {
        requestFilters.remove(requestFilter);
        updateRequestFiltersCopy();
    }

    private void removeResponseFilter(ResponseFilter responseFilter) {
        responseFilters.remove(responseFilter);
        updateResponseFiltersCopy();
    }

    private void updateRequestFiltersCopy() {
        requestFiltersCopy = Collections.unmodifiableList(requestFilters);
    }

    private void updateResponseFiltersCopy() {
        responseFiltersCopy = Collections.unmodifiableList(responseFilters);
    }
}
