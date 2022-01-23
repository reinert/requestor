package io.reinert.requestor.net.dispatcher;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;

public class DispatcherFactory implements RequestDispatcher.Factory {
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
                requestDispatcher = new JavaDispatcher(requestProcessor, responseProcessor, deferredPoolFactory);
                return requestDispatcher;
            }

            return new JavaDispatcher(requestProcessor, responseProcessor, deferredPoolFactory);
        }
    }

