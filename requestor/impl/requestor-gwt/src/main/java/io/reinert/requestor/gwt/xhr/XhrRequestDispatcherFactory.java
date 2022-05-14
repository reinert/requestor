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

import java.util.Collections;
import java.util.List;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;

/**
 * GWT implementation for {@link io.reinert.requestor.core.RequestDispatcher.Factory} powered by XMLHttpRequest.
 *
 * @author Danilo Reinert
 */
public class XhrRequestDispatcherFactory implements RequestDispatcher.Factory {

    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;
    private DeferredPool.Factory deferredPoolFactory;
    private RequestDispatcher requestDispatcher;

    @Override
    public RequestDispatcher create(RequestProcessor requestProcessor,
                                    ResponseProcessor responseProcessor,
                                    DeferredPool.Factory deferredPoolFactory) {
        if (this.requestProcessor == requestProcessor &&
                this.responseProcessor == responseProcessor &&
                this.deferredPoolFactory == deferredPoolFactory) {
            return requestDispatcher;
        }

        if (requestDispatcher == null) {
            this.requestProcessor = requestProcessor;
            this.responseProcessor = responseProcessor;
            this.deferredPoolFactory = deferredPoolFactory;
            requestDispatcher = new XhrRequestDispatcher(requestProcessor, responseProcessor, deferredPoolFactory);
            return requestDispatcher;
        }

        return new XhrRequestDispatcher(requestProcessor, responseProcessor, deferredPoolFactory);
    }

    @Override
    public void shutdown() {
        // No-op since the environment is single threaded
    }

    @Override
    public List<Runnable> shutdownNow() {
        // No-op since the environment is single threaded
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeoutInMillis) throws InterruptedException {
        return false;
    }
}
