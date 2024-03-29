/*
 * Copyright 2014-2022 Danilo Reinert
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

import java.util.ListIterator;

/**
 * This class performs all necessary processing steps to ongoing requests.
 *
 * @author Danilo Reinert
 */
public class RequestProcessor {

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

    public void process(RequestInAuthProcess<?> requestInAuthProcess) {
        ProcessableRequest request = requestInAuthProcess;

        // 4: AUTH
        if (!shouldApply(request, Process.AUTH_REQUEST)) {
            request.setAuth((Auth.Provider) null);
        }

        // 3: INTERCEPT
        if (shouldApply(request, Process.INTERCEPT_REQUEST)) {
            request = applyInterceptors(request);
        }

        // 2: SERIALIZE
        if (shouldApply(request, Process.SERIALIZE_REQUEST)) {
            request = applySerializer(request);
        }

        // 1: FILTER
        if (shouldApply(request, Process.FILTER_REQUEST)) {
            request = applyFilters(request);
        }

        request.process();
    }

    public RequestSerializer getRequestSerializer() {
        return requestSerializer;
    }

    public void setRequestSerializer(RequestSerializer requestSerializer) {
        this.requestSerializer = requestSerializer;
    }

    private ProcessableRequest applyFilters(ProcessableRequest request) {
        // Apply filters in reverse order, so they are executed in the order they were registered
        final ListIterator<RequestFilter.Provider> it = filterManager.reverseRequestFiltersIterator();
        while (it.hasPrevious()) {
            request = new RequestInFilterProcess(request, it.previous().getInstance());
        }

        return request;
    }

    private ProcessableRequest applyInterceptors(ProcessableRequest request) {
        // Apply interceptors in reverse order, so they are executed in the order they were registered
        final ListIterator<RequestInterceptor.Provider> it = interceptorManager.reverseRequestInterceptorsIterator();
        while (it.hasPrevious()) {
            request = new RequestInInterceptProcess(request, it.previous().getInstance());
        }

        return request;
    }

    private ProcessableRequest applySerializer(ProcessableRequest request) {
        return new RequestInSerializeProcess(request, serializationEngine, requestSerializer);
    }

    private boolean shouldApply(ProcessableRequest request, Process process) {
        return !request.getSkippedProcesses().contains(process);
    }
}
