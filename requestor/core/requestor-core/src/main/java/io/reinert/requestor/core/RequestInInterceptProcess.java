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

/**
 * A request that process a {@link RequestInterceptor}.
 *
 * @author Danilo Reinert
 */
class RequestInInterceptProcess extends AbstractProcessableRequest {

    private final RequestInterceptor interceptor;

    public RequestInInterceptProcess(ProcessableRequest request, RequestInterceptor interceptor) {
        super(request);
        this.interceptor = interceptor;
    }

    @Override
    public void process() {
        interceptor.intercept(this);
    }
}
