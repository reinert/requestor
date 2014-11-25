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

/**
 * Responsible for performing request and response filtering.
 *
 * @author Danilo Reinert
 */
final class FilterEngine {

    private final FilterManager filterManager;

    public FilterEngine(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    public RequestBuilder filterRequest(RequestBuilder request) {
        for (RequestFilter filter : filterManager.getRequestFilters()) {
            filter.filter(request);
        }
        return request;
    }

    public Response filterResponse(Request request, Response response) {
        for (ResponseFilter filter : filterManager.getResponseFilters()) {
            filter.filter(request, response);
        }
        return response;
    }
}
