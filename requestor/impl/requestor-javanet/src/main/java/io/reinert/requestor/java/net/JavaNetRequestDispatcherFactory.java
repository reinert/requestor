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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final ScheduledExecutorService scheduledExecutorService;

    private int inputBufferSize;
    private int outputBufferSize;

    public JavaNetRequestDispatcherFactory(ScheduledExecutorService scheduledExecutorService) {
        this(scheduledExecutorService, DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_SIZE);
    }

    public JavaNetRequestDispatcherFactory(ScheduledExecutorService scheduledExecutorService,
                                           int inputBufferSize, int outputBufferSize) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.inputBufferSize = inputBufferSize;
        this.outputBufferSize = outputBufferSize;
    }

    public RequestDispatcher create(RequestProcessor requestProcessor,
                                    ResponseProcessor responseProcessor,
                                    DeferredPool.Factory deferredPoolFactory,
                                    RequestLogger logger) {
        return new JavaNetRequestDispatcher(requestProcessor, responseProcessor, deferredPoolFactory, logger,
                scheduledExecutorService, inputBufferSize, outputBufferSize);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return scheduledExecutorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return scheduledExecutorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeoutInMillis) throws InterruptedException {
        return scheduledExecutorService.awaitTermination(timeoutInMillis, TimeUnit.MILLISECONDS);
    }

    public int getInputBufferSize() {
        return inputBufferSize;
    }

    public void setInputBufferSize(int inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }

    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    public void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
