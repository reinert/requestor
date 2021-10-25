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

/**
 * A manager for {@link RequestFilter} and {@link ResponseFilter}.
 *
 * @author Danilo Reinert
 */
class FilterManagerImpl implements FilterManager {

    private final List<RequestFilter.Provider> requestFilters = new ArrayList<RequestFilter.Provider>();
    private final List<ResponseFilter.Provider> responseFilters = new ArrayList<ResponseFilter.Provider>();

    @SuppressWarnings("unchecked")
    private List<RequestFilter.Provider> requestFiltersCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseFilter.Provider> responseFiltersCopy = Collections.EMPTY_LIST;

    public FilterManagerImpl() {
    }

    /**
     * Wraps a {@link FilterManagerImpl} absorbing its filters.
     * It doesn't affect neither is affected by the wrapping manager.
     *
     * @param manager  a manager to wrap
     */
    public FilterManagerImpl(FilterManagerImpl manager) {
        final List<RequestFilter.Provider> managerRequestFilters = manager.getRequestFilters();
        this.requestFilters.addAll(managerRequestFilters);
        this.requestFiltersCopy = managerRequestFilters; // It's an immutable list
        final List<ResponseFilter.Provider> managerResponseFilters = manager.getResponseFilters();
        this.responseFilters.addAll(managerResponseFilters);
        this.responseFiltersCopy = managerResponseFilters; // It's an immutable list
    }

    @Override
    public Registration register(final RequestFilter requestFilter) {
        return register(new RequestFilter.Provider() {
            @Override
            public RequestFilter getInstance() {
                return requestFilter;
            }
        });
    }

    @Override
    public Registration register(final RequestFilter.Provider provider) {
        requestFilters.add(provider);
        updateRequestFiltersCopy(); // getRequestFilters returns this copy

        return new Registration() {
            @Override
            public void cancel() {
                removeRequestFilter(provider);
            }
        };
    }

    @Override
    public Registration register(final ResponseFilter responseFilter) {
        return register(new ResponseFilter.Provider() {
            @Override
            public ResponseFilter getInstance() {
                return responseFilter;
            }
        });
    }

    @Override
    public Registration register(final ResponseFilter.Provider provider) {
        responseFilters.add(provider);
        updateResponseFiltersCopy(); // getResponseFilters returns this copy

        return new Registration() {
            @Override
            public void cancel() {
                removeResponseFilter(provider);
            }
        };
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The request filters.
     */
    public List<RequestFilter.Provider> getRequestFilters() {
        return requestFiltersCopy;
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The response filters.
     */
    public List<ResponseFilter.Provider> getResponseFilters() {
        return responseFiltersCopy;
    }

    public ListIterator<RequestFilter.Provider> reverseRequestFiltersIterator() {
        return requestFiltersCopy.listIterator(requestFiltersCopy.size());
    }

    public ListIterator<ResponseFilter.Provider> reverseResponseFiltersIterator() {
        return responseFiltersCopy.listIterator(responseFiltersCopy.size());
    }

    private void removeRequestFilter(RequestFilter.Provider requestFilter) {
        requestFilters.remove(requestFilter);
        updateRequestFiltersCopy();
    }

    private void removeResponseFilter(ResponseFilter.Provider responseFilter) {
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
