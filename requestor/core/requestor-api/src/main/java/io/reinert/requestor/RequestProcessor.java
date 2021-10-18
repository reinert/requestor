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

import java.util.ListIterator;

/**
 * This class performs all necessary processing steps to ongoing requests.
 *
 * @author Danilo Reinert
 */
class RequestProcessor {

    private final SerializationEngine serializationEngine;
    private RequestSerializer requestSerializer;
    private final FilterManagerImpl filterManager;
    private final InterceptorManagerImpl interceptorManager;

    public RequestProcessor(SerializationEngine serializationEngine, RequestSerializer requestSerializer,
                            FilterManagerImpl filterManager, InterceptorManagerImpl interceptorManager) {
        this.serializationEngine = serializationEngine;
        this.requestSerializer = requestSerializer;
        this.filterManager = filterManager;
        this.interceptorManager = interceptorManager;
    }

    public void process(RequestInAuthProcess<?, ?> request) {
        // 1: FILTER
        // 2: SERIALIZE
        // 3: INTERCEPT
        // 4: AUTH (outside)
        applyFilters(applySerializer(applyInterceptors(request))).process();
    }

    public RequestSerializer getRequestSerializer() {
        return requestSerializer;
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        this.requestSerializer = requestSerializer;
    }

    private ProcessableRequest applyFilters(ProcessableRequest request) {
        // Apply filters in reverse order so they are executed in the order they were registered
        final ListIterator<RequestFilter> it = filterManager.reverseRequestFiltersIterator();
        while (it.hasPrevious()) {
            request = new RequestInFilterProcess(request, it.previous());
        }

        return request;
    }

    private ProcessableRequest applyInterceptors(ProcessableRequest request) {
        // Apply interceptors in reverse order so they are executed in the order they were registered
        final ListIterator<RequestInterceptor> it = interceptorManager.reverseRequestInterceptorsIterator();
        while (it.hasPrevious()) {
            request = new RequestInInterceptProcess(request, it.previous());
        }

        return request;
    }

    private ProcessableRequest applySerializer(ProcessableRequest request) {
        return new RequestInSerializeProcess(request, serializationEngine, requestSerializer);
    }
}
