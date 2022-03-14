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
package io.reinert.requestor.net;

import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.RequestDispatchException;
import io.reinert.requestor.core.RequestDispatcher;
import io.reinert.requestor.core.RequestProcessor;
import io.reinert.requestor.core.ResponseProcessor;
import io.reinert.requestor.core.payload.type.PayloadType;

/**
 * RequestDispatcher implementation using {@link HttpURLConnection}.
 *
 * @author Onezino Gabriel
 */
class NetRequestDispatcher extends RequestDispatcher {

    private final ScheduledThreadPoolExecutor threadPool;

    public NetRequestDispatcher(RequestProcessor requestProcessor,
                                ResponseProcessor responseProcessor,
                                DeferredPool.Factory deferredPoolFactory,
                                ScheduledThreadPoolExecutor threadPool) {
        super(requestProcessor, responseProcessor, deferredPoolFactory);
        this.threadPool = threadPool;
    }

    public void scheduleRun(final Runnable runnable, int delay) {
        threadPool.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    protected <R> void send(PreparedRequest request, Deferred<R> deferred, PayloadType responsePayloadType) {
        // Return if deferred were rejected or resolved before this method was called
        if (!deferred.isPending()) return;

        deferred.reject(new RequestDispatchException(request, "Dispatcher not implemented yet."));
    }
}
