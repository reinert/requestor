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

import com.google.gwt.http.client.Response;

/**
 * An extension interface implemented by response filters.
 * Response filters are intended to manipulate the response before the code that invoked its request receives it.
 *
 * @author Danilo Reinert
 */
public interface ResponseFilter {

    /**
     * Filter method called after a response has been provided for a request.
     *
     * @param request   The sent request.
     * @param response  The received response.
     */
    void filter(Request request, Response response);
}
