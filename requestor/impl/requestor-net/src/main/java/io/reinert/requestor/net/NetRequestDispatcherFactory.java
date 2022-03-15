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
package io.reinert.requestor.net;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;

/**
 * Java implementation for {@link io.reinert.requestor.core.RequestDispatcher.Factory} powered by HttpURLConnection.
 *
 * @author Danilo Reinert
 */
class NetRequestDispatcherFactory implements RequestDispatcher.Factory {

    private int threadPoolSize = 10;
    private int inputBufferSize = 8 * 1024;
    private int outputBufferSize = 8 * 1024;

    public RequestDispatcher create(RequestProcessor requestProcessor,
                                    ResponseProcessor responseProcessor,
                                    DeferredPool.Factory deferredPoolFactory) {
        return new NetRequestDispatcher(requestProcessor, responseProcessor, deferredPoolFactory,
                new ScheduledThreadPoolExecutor(threadPoolSize), inputBufferSize, outputBufferSize);
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public synchronized void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getInputBufferSize() {
        return inputBufferSize;
    }

    public synchronized void setInputBufferSize(int inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }

    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    public synchronized void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }
}
