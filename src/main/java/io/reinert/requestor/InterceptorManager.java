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
class InterceptorManager {

    private final List<RequestInterceptor> requestInterceptors = new ArrayList<RequestInterceptor>();
    private final List<ResponseInterceptor> responseInterceptors = new ArrayList<ResponseInterceptor>();

    @SuppressWarnings("unchecked")
    private List<RequestInterceptor> requestInterceptorsCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseInterceptor> responseInterceptorsCopy = Collections.EMPTY_LIST;

    /**
     * Register a request interceptor.
     *
     * @param requestInterceptor The request interceptor to be registered.
     *
     * @return  The {@link com.google.web.bindery.event.shared.HandlerRegistration} object, capable of cancelling this
     *          registration to the {@link io.reinert.requestor.InterceptorManager}.
     */
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

    /**
     * Register a response interceptor.
     *
     * @param responseInterceptor The response interceptor to be registered.
     *
     * @return  The {@link com.google.web.bindery.event.shared.HandlerRegistration} object, capable of cancelling this
     *          registration to the {@link io.reinert.requestor.InterceptorManager}.
     */
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
     * @return The request interceptors.
     */
    public List<RequestInterceptor> getRequestInterceptors() {
        return requestInterceptorsCopy;
    }

    /**
     * Returns an immutable copy of the interceptors.
     *
     * @return The response interceptors.
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
