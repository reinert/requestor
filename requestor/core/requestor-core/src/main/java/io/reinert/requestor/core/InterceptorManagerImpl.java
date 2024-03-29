/*
 * Copyright 2015-2022 Danilo Reinert
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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * A manager for {@link io.reinert.requestor.core.RequestInterceptor} and
 * {@link io.reinert.requestor.core.ResponseInterceptor}.
 *
 * @author Danilo Reinert
 */
class InterceptorManagerImpl implements InterceptorManager {

    private final List<RequestInterceptor.Provider> requestInterceptors =
            new ArrayList<RequestInterceptor.Provider>();
    private final List<ResponseInterceptor.Provider> responseInterceptors =
            new ArrayList<ResponseInterceptor.Provider>();

    @SuppressWarnings("unchecked")
    private List<RequestInterceptor.Provider> requestInterceptorsCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseInterceptor.Provider> responseInterceptorsCopy = Collections.EMPTY_LIST;

    public InterceptorManagerImpl() {
    }

    /**
     * Wraps a {@link InterceptorManagerImpl} absorbing its interceptors.
     * It doesn't affect neither is affected by the wrapping manager.
     *
     * @param manager  a manager to wrap
     */
    public InterceptorManagerImpl(InterceptorManagerImpl manager) {
        final List<RequestInterceptor.Provider> managerRequestInterceptors = manager.getRequestInterceptors();
        this.requestInterceptors.addAll(managerRequestInterceptors);
        this.requestInterceptorsCopy = managerRequestInterceptors; // It's an immutable list
        final List<ResponseInterceptor.Provider> managerResponseInterceptors = manager.getResponseInterceptors();
        this.responseInterceptors.addAll(managerResponseInterceptors);
        this.responseInterceptorsCopy = managerResponseInterceptors; // It's an immutable list
    }

    @Override
    public Registration register(final RequestInterceptor requestInterceptor) {
        return register(new RequestInterceptor.Provider() {
            public RequestInterceptor getInstance() {
                return requestInterceptor;
            }
        });
    }

    @Override
    public Registration register(final RequestInterceptor.Provider provider) {
        addRequestInterceptor(provider);

        return new Registration() {
            public void cancel() {
                removeRequestInterceptor(provider);
            }
        };
    }

    @Override
    public Registration register(final ResponseInterceptor responseInterceptor) {
        return register(new ResponseInterceptor.Provider() {
            public ResponseInterceptor getInstance() {
                return responseInterceptor;
            }
        });
    }

    @Override
    public Registration register(final ResponseInterceptor.Provider provider) {
        addResponseInterceptor(provider);

        return new Registration() {
            public void cancel() {
                removeResponseInterceptor(provider);
            }
        };
    }

    /**
     * Returns an immutable copy of the interceptors.
     *
     * @return the request interceptors
     */
    public List<RequestInterceptor.Provider> getRequestInterceptors() {
        return requestInterceptorsCopy;
    }

    /**
     * Returns an immutable copy of the interceptors.
     *
     * @return the response interceptors
     */
    public List<ResponseInterceptor.Provider> getResponseInterceptors() {
        return responseInterceptorsCopy;
    }

    public ListIterator<RequestInterceptor.Provider> reverseRequestInterceptorsIterator() {
        return requestInterceptorsCopy.listIterator(requestInterceptorsCopy.size());
    }

    public ListIterator<ResponseInterceptor.Provider> reverseResponseInterceptorsIterator() {
        return responseInterceptorsCopy.listIterator(responseInterceptorsCopy.size());
    }

    private synchronized void addRequestInterceptor(RequestInterceptor.Provider requestInterceptor) {
        requestInterceptors.add(requestInterceptor);
        updateRequestInterceptorsCopy();
    }

    private synchronized void addResponseInterceptor(ResponseInterceptor.Provider responseInterceptor) {
        responseInterceptors.add(responseInterceptor);
        updateResponseInterceptorsCopy();
    }

    private synchronized void removeRequestInterceptor(RequestInterceptor.Provider requestInterceptor) {
        requestInterceptors.remove(requestInterceptor);
        updateRequestInterceptorsCopy();
    }

    private synchronized void removeResponseInterceptor(ResponseInterceptor.Provider responseInterceptor) {
        responseInterceptors.remove(responseInterceptor);
        updateResponseInterceptorsCopy();
    }

    private synchronized void updateRequestInterceptorsCopy() {
        requestInterceptorsCopy = Collections.unmodifiableList(requestInterceptors);
    }

    private synchronized void updateResponseInterceptorsCopy() {
        responseInterceptorsCopy = Collections.unmodifiableList(responseInterceptors);
    }
}
