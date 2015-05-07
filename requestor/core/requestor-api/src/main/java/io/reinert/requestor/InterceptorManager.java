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
 * A manager for {@link io.reinert.requestor.RequestInterceptor} and {@link io.reinert.requestor.ResponseInterceptor}.
 *
 * @author Danilo Reinert
 */
class InterceptorManager implements HasInterceptors {

    private final List<RequestInterceptor> requestInterceptors = new ArrayList<RequestInterceptor>();
    private final List<ResponseInterceptor> responseInterceptors = new ArrayList<ResponseInterceptor>();

    @SuppressWarnings("unchecked")
    private List<RequestInterceptor> requestInterceptorsCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseInterceptor> responseInterceptorsCopy = Collections.EMPTY_LIST;

    public InterceptorManager() {
    }

    /**
     * Wraps a {@link InterceptorManager} absorbing its interceptors.
     * It doesn't affect neither is affected by the wrapping manager.
     *
     * @param manager  a manager to wrap
     */
    public InterceptorManager(InterceptorManager manager) {
        final List<RequestInterceptor> managerRequestInterceptors = manager.getRequestInterceptors();
        this.requestInterceptors.addAll(managerRequestInterceptors);
        this.requestInterceptorsCopy = managerRequestInterceptors; // It's an immutable list
        final List<ResponseInterceptor> managerResponseInterceptors = manager.getResponseInterceptors();
        this.responseInterceptors.addAll(managerResponseInterceptors);
        this.responseInterceptorsCopy = managerResponseInterceptors; // It's an immutable list
    }

    @Override
    public HandlerRegistration addRequestInterceptor(final RequestInterceptor requestInterceptor) {
        requestInterceptors.add(requestInterceptor);
        updateRequestInterceptorsCopy(); // getRequestInterceptors returns this immutable copy

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeRequestInterceptor(requestInterceptor);
            }
        };
    }

    @Override
    public HandlerRegistration addResponseInterceptor(final ResponseInterceptor responseInterceptor) {
        responseInterceptors.add(responseInterceptor);
        updateResponseInterceptorsCopy(); // getResponseInterceptors returns this immutable copy

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                removeResponseInterceptor(responseInterceptor);
            }
        };
    }

    /**
     * Returns an immutable copy of the interceptors.
     *
     * @return the request interceptors
     */
    public List<RequestInterceptor> getRequestInterceptors() {
        return requestInterceptorsCopy;
    }

    /**
     * Returns an immutable copy of the interceptors.
     *
     * @return the response interceptors
     */
    public List<ResponseInterceptor> getResponseInterceptors() {
        return responseInterceptorsCopy;
    }

    private void removeRequestInterceptor(RequestInterceptor requestInterceptor) {
        requestInterceptors.remove(requestInterceptor);
        updateRequestInterceptorsCopy();
    }

    private void removeResponseInterceptor(ResponseInterceptor responseInterceptor) {
        responseInterceptors.remove(responseInterceptor);
        updateResponseInterceptorsCopy();
    }

    private void updateRequestInterceptorsCopy() {
        requestInterceptorsCopy = Collections.unmodifiableList(requestInterceptors);
    }

    private void updateResponseInterceptorsCopy() {
        responseInterceptorsCopy = Collections.unmodifiableList(responseInterceptors);
    }
}
