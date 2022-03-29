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
package io.reinert.requestor.javanet;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.deferred.DeferredPoolFactoryImpl;
import io.reinert.requestor.java.serialization.BinarySerializer;
import io.reinert.requestor.java.serialization.ByteSerializer;
import io.reinert.requestor.java.serialization.FileSerializer;
import io.reinert.requestor.java.serialization.FormDataMultiPartSerializer;
import io.reinert.requestor.java.serialization.InputStreamSerializer;

/**
 * A session implementation for requestor-net.
 *
 * @author Danilo Reinert
 */
public class NetSession extends Session {

    public static final int DEFAULT_CORE_POOL_SIZE = 10;

    static {
        RequestorNet.init();
    }

    public NetSession() {
        this(new DeferredPoolFactoryImpl());
    }

    public NetSession(DeferredPool.Factory deferredPoolFactory) {
        this(deferredPoolFactory, new ScheduledThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE));
    }

    public NetSession(ScheduledExecutorService executorService) {
        this(new DeferredPoolFactoryImpl(), executorService);
    }

    public NetSession(DeferredPool.Factory deferredPoolFactory, ScheduledExecutorService executorService) {
        super(new NetRequestDispatcherFactory(executorService), deferredPoolFactory);
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return ((NetRequestDispatcherFactory) getRequestDispatcherFactory()).getScheduledExecutorService();
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutor) {
        ((NetRequestDispatcherFactory) getRequestDispatcherFactory()).setScheduledExecutorService(scheduledExecutor);
    }

    public int getInputBufferSize() {
        return ((NetRequestDispatcherFactory) getRequestDispatcherFactory()).getInputBufferSize();
    }

    public void setInputBufferSize(int inputBufferSize) {
        ((NetRequestDispatcherFactory) getRequestDispatcherFactory()).setInputBufferSize(inputBufferSize);
    }

    public int getOutputBufferSize() {
        return ((NetRequestDispatcherFactory) getRequestDispatcherFactory()).getOutputBufferSize();
    }

    public void setOutputBufferSize(int outputBufferSize) {
        ((NetRequestDispatcherFactory) getRequestDispatcherFactory()).setOutputBufferSize(outputBufferSize);
    }

    @Override
    protected void configure() {
        super.configure();

        save(RequestorNet.DEFAULT_CONTENT_TYPE, "text/plain");

        register(BinarySerializer.getInstance());
        register(ByteSerializer.getInstance());
        register(FileSerializer.getInstance());
        register(FormDataMultiPartSerializer.getInstance());
        register(InputStreamSerializer.getInstance());
    }
}