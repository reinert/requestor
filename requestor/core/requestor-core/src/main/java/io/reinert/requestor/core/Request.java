/*
 * Copyright 2014-2022 Danilo Reinert
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
package io.reinert.requestor.core;

import java.util.concurrent.Future;

import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ReadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;
import io.reinert.requestor.core.callback.VoidCallback;
import io.reinert.requestor.core.callback.WriteCallback;

/**
 * An observable Request.
 *
 * @param <T> The type of successful result
 *
 * @author Danilo Reinert
 */
public interface Request<T> extends SerializedRequest {

    Response await() throws RequestException;

    Future<IncomingResponse> getResponse();

    HttpConnection getHttpConnection();

    int getRetryCount();

    Request<T> onAbort(ExceptionCallback callback);

    Request<T> onAbort(ExceptionRequestCallback<T> callback);

    Request<T> onCancel(ExceptionCallback callback);

    Request<T> onCancel(ExceptionRequestCallback<T> callback);

    Request<T> onError(VoidCallback callback);

    Request<T> onError(ExceptionCallback callback);

    Request<T> onError(ExceptionRequestCallback<T> callback);

    Request<T> onLoad(VoidCallback callback);

    Request<T> onLoad(ResponseCallback callback);

    Request<T> onLoad(ResponseRequestCallback<T> callback);

    Request<T> onFail(VoidCallback callback);

    Request<T> onFail(ResponseCallback callback);

    Request<T> onFail(ResponseRequestCallback<T> callback);

    Request<T> onRead(ReadCallback callback);

    Request<T> onStatus(int statusCode, VoidCallback callback);

    Request<T> onStatus(int statusCode, ResponseCallback callback);

    Request<T> onStatus(int statusCode, ResponseRequestCallback<T> callback);

    Request<T> onStatus(Status status, VoidCallback callback);

    Request<T> onStatus(Status status, ResponseCallback callback);

    Request<T> onStatus(Status status, ResponseRequestCallback<T> callback);

    Request<T> onStatus(StatusFamily family, VoidCallback callback);

    Request<T> onStatus(StatusFamily family, ResponseCallback callback);

    Request<T> onStatus(StatusFamily family, ResponseRequestCallback<T> callback);

    Request<T> onSuccess(VoidCallback callback);

    Request<T> onSuccess(PayloadCallback<T> callback);

    Request<T> onSuccess(PayloadResponseCallback<T> callback);

    Request<T> onSuccess(PayloadResponseRequestCallback<T> callback);

    Request<T> onTimeout(TimeoutCallback callback);

    Request<T> onTimeout(TimeoutRequestCallback<T> callback);

    Request<T> onWrite(WriteCallback callback);

    // ========================================================================
    // Store
    // ========================================================================

    @Override
    Request<T> save(String key, Object value);

    @Override
    Request<T> save(String key, Object value, Level level);

    @Override
    Request<T> save(String key, Object value, long ttl, Level level);

    @Override
    Request<T> save(String key, Object value, long ttl);

    @Override
    Request<T> onSaved(String key, Handler handler);

    @Override
    Request<T> onRemoved(String key, Handler handler);

    @Override
    Request<T> onExpired(String key, Handler handler);

}
