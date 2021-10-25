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
 * A container of {@link RequestFilter} and {@link ResponseFilter}.
 *
 * @author Danilo Reinert
 */
public interface FilterManager {

    /**
     * Register a request filter.
     *
     * @param requestFilter  the request filter to be registered
     *
     * @return  the {@link Registration} object, capable of cancelling this registration
     */
    Registration register(RequestFilter requestFilter);

    Registration register(RequestFilter.Provider provider);

    /**
     * Register a response filter.
     *
     * @param responseFilter  the response filter to be registered
     *
     * @return  the {@link Registration} object, capable of cancelling this registration
     */
    Registration register(ResponseFilter responseFilter);

    Registration register(ResponseFilter.Provider provider);

}
