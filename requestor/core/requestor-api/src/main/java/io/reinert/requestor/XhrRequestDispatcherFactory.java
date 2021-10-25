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

/**
 * Default implementation for {@link RequestDispatcherFactory}.
 *
 * @author Danilo Reinert
 */
public class XhrRequestDispatcherFactory implements RequestDispatcherFactory {

    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;
    private DeferredFactory deferredFactory;
    private RequestDispatcher requestDispatcher;

    @Override
    public RequestDispatcher getRequestDispatcher(RequestProcessor requestProcessor,
                                                  ResponseProcessor responseProcessor,
                                                  DeferredFactory deferredFactory) {
        if (this.requestProcessor == requestProcessor &&
                this.responseProcessor == responseProcessor &&
                this.deferredFactory == deferredFactory) {
            return requestDispatcher;
        }

        if (requestDispatcher == null) {
            this.requestProcessor = requestProcessor;
            this.responseProcessor = responseProcessor;
            this.deferredFactory = deferredFactory;
            requestDispatcher = new XhrRequestDispatcher(requestProcessor, responseProcessor, deferredFactory);
            return requestDispatcher;
        }

        return new XhrRequestDispatcher(requestProcessor, responseProcessor, deferredFactory);
    }
}
