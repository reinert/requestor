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
package io.reinert.requestor.gwt.xhr;

import io.reinert.requestor.core.AsyncRunner;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestLogger;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;

/**
 * GWT implementation for {@link io.reinert.requestor.core.RequestDispatcher.Factory} powered by XMLHttpRequest.
 *
 * @author Danilo Reinert
 */
public class XhrRequestDispatcherFactory implements RequestDispatcher.Factory {

    private AsyncRunner asyncRunner;
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;
    private DeferredPool.Factory deferredPoolFactory;
    private RequestLogger logger;
    private RequestDispatcher requestDispatcher;

    @Override
    public RequestDispatcher create(AsyncRunner asyncRunner,
                                    RequestProcessor requestProcessor,
                                    ResponseProcessor responseProcessor,
                                    DeferredPool.Factory deferredPoolFactory,
                                    RequestLogger logger) {
        if (this.asyncRunner == asyncRunner &&
                this.requestProcessor == requestProcessor &&
                this.responseProcessor == responseProcessor &&
                this.deferredPoolFactory == deferredPoolFactory &&
                this.logger == logger) {
            return requestDispatcher;
        }

        if (requestDispatcher == null) {
            this.asyncRunner = asyncRunner;
            this.requestProcessor = requestProcessor;
            this.responseProcessor = responseProcessor;
            this.deferredPoolFactory = deferredPoolFactory;
            this.logger = logger;
            requestDispatcher = new XhrRequestDispatcher(asyncRunner, requestProcessor, responseProcessor,
                    deferredPoolFactory, logger);
            return requestDispatcher;
        }

        return new XhrRequestDispatcher(asyncRunner, requestProcessor, responseProcessor, deferredPoolFactory, logger);
    }
}
