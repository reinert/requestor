/*
 * Copyright 2015 Danilo Reinert
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
 * A container of {@link RequestInterceptor} and {@link ResponseInterceptor}.
 *
 * @author Danilo Reinert
 */
public interface InterceptorManager {

    /**
     * Register a request interceptor.
     *
     * @param requestInterceptor the request interceptor to be registered
     *
     * @return  the {@link Registration} object, capable of cancelling this registration
     */
    Registration register(RequestInterceptor requestInterceptor);

    Registration register(RequestInterceptor.Provider provider);

    /**
     * Register a response interceptor.
     *
     * @param responseInterceptor The response interceptor to be registered
     *
     * @return  the {@link Registration} object, capable of cancelling this registration
     */
    Registration register(ResponseInterceptor responseInterceptor);

    Registration register(ResponseInterceptor.Provider provider);

}
