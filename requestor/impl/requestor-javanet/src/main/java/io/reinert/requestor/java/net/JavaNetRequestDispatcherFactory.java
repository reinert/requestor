/*
 * Copyright 2021-2022 Danilo Reinert
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
package io.reinert.requestor.java.net;

import io.reinert.requestor.core.AsyncRunner;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestLogger;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;

/**
 * Java implementation for {@link io.reinert.requestor.core.RequestDispatcher.Factory} powered by HttpURLConnection.
 *
 * @author Danilo Reinert
 */
public class JavaNetRequestDispatcherFactory implements RequestDispatcher.Factory {

    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    private final int inputBufferSize;
    private final int outputBufferSize;

    // Fields for caching
    private AsyncRunner asyncRunner;
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;
    private DeferredPool.Factory deferredPoolFactory;
    private RequestLogger logger;
    private JavaNetRequestDispatcher dispatcher;

    public JavaNetRequestDispatcherFactory() {
        this(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
    }

    public JavaNetRequestDispatcherFactory(int inputBufferSize, int outputBufferSize) {
        this.inputBufferSize = inputBufferSize;
        this.outputBufferSize = outputBufferSize;
    }

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
            return dispatcher;
        }

        if (dispatcher == null) {
            this.asyncRunner = asyncRunner;
            this.requestProcessor = requestProcessor;
            this.responseProcessor = responseProcessor;
            this.deferredPoolFactory = deferredPoolFactory;
            this.logger = logger;
            dispatcher = new JavaNetRequestDispatcher(asyncRunner, requestProcessor, responseProcessor,
                    deferredPoolFactory, logger, inputBufferSize, outputBufferSize);
            return dispatcher;
        }

        return new JavaNetRequestDispatcher(asyncRunner, requestProcessor, responseProcessor,
                deferredPoolFactory, logger, inputBufferSize, outputBufferSize);
    }

    public int getInputBufferSize() {
        return inputBufferSize;
    }

    public int getOutputBufferSize() {
        return outputBufferSize;
    }
}
