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
import java.util.ListIterator;

import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A manager for {@link RequestFilter} and {@link ResponseFilter}.
 *
 * @author Danilo Reinert
 */
class FilterManagerImpl implements FilterManager {

    private final List<RequestFilter.Factory> requestFilters = new ArrayList<RequestFilter.Factory>();
    private final List<ResponseFilter.Factory> responseFilters = new ArrayList<ResponseFilter.Factory>();

    @SuppressWarnings("unchecked")
    private List<RequestFilter.Factory> requestFiltersCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseFilter.Factory> responseFiltersCopy = Collections.EMPTY_LIST;

    public FilterManagerImpl() {
    }

    /**
     * Wraps a {@link FilterManagerImpl} absorbing its filters.
     * It doesn't affect neither is affected by the wrapping manager.
     *
     * @param manager  a manager to wrap
     */
    public FilterManagerImpl(FilterManagerImpl manager) {
        final List<RequestFilter.Factory> managerRequestFilters = manager.getRequestFilters();
        this.requestFilters.addAll(managerRequestFilters);
        this.requestFiltersCopy = managerRequestFilters; // It's an immutable list
        final List<ResponseFilter.Factory> managerResponseFilters = manager.getResponseFilters();
        this.responseFilters.addAll(managerResponseFilters);
        this.responseFiltersCopy = managerResponseFilters; // It's an immutable list
    }

    @Override
    public HandlerRegistration register(final RequestFilter requestFilter) {
        return register(new RequestFilter.Factory() {
            @Override
            public RequestFilter getInstance() {
                return requestFilter;
            }
        });
    }

    @Override
    public HandlerRegistration register(final RequestFilter.Factory factory) {
        requestFilters.add(factory);
        updateRequestFiltersCopy(); // getRequestFilters returns this copy

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeRequestFilter(factory);
            }
        };
    }

    @Override
    public HandlerRegistration register(final ResponseFilter responseFilter) {
        return register(new ResponseFilter.Factory() {
            @Override
            public ResponseFilter getInstance() {
                return responseFilter;
            }
        });
    }

    @Override
    public HandlerRegistration register(final ResponseFilter.Factory factory) {
        responseFilters.add(factory);
        updateResponseFiltersCopy(); // getResponseFilters returns this copy

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeResponseFilter(factory);
            }
        };
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The request filters.
     */
    public List<RequestFilter.Factory> getRequestFilters() {
        return requestFiltersCopy;
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The response filters.
     */
    public List<ResponseFilter.Factory> getResponseFilters() {
        return responseFiltersCopy;
    }

    public ListIterator<RequestFilter.Factory> reverseRequestFiltersIterator() {
        return requestFiltersCopy.listIterator(requestFiltersCopy.size());
    }

    public ListIterator<ResponseFilter.Factory> reverseResponseFiltersIterator() {
        return responseFiltersCopy.listIterator(responseFiltersCopy.size());
    }

    private void removeRequestFilter(RequestFilter.Factory requestFilter) {
        requestFilters.remove(requestFilter);
        updateRequestFiltersCopy();
    }

    private void removeResponseFilter(ResponseFilter.Factory responseFilter) {
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
