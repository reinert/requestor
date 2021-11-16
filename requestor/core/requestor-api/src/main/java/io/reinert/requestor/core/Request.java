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
package io.reinert.requestor.core;

import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ExceptionRequestCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.PayloadResponseRequestCallback;
import io.reinert.requestor.core.callback.ProgressCallback;
import io.reinert.requestor.core.callback.ProgressRequestCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.ResponseRequestCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;
import io.reinert.requestor.core.callback.TimeoutRequestCallback;

/**
 * An observable Request.
 *
 * @param <T> The type of successful result
 *
 * @author Danilo Reinert
 */
public interface Request<T> extends SerializedRequest {

    HttpConnection getHttpConnection();

    Request<T> onAbort(ExceptionCallback callback);

    Request<T> onAbort(ExceptionRequestCallback<T> callback);

    Request<T> onLoad(ResponseCallback callback);

    Request<T> onLoad(ResponseRequestCallback<T> callback);

    Request<T> onFail(ResponseCallback callback);

    Request<T> onFail(ResponseRequestCallback<T> callback);

    Request<T> onProgress(ProgressCallback callback);

    Request<T> onProgress(ProgressRequestCallback<T> callback);

    Request<T> onStatus(int statusCode, ResponseCallback callback);

    Request<T> onStatus(int statusCode, ResponseRequestCallback<T> callback);

    Request<T> onStatus(Status status, ResponseCallback callback);

    Request<T> onStatus(Status status, ResponseRequestCallback<T> callback);

    Request<T> onStatus(StatusFamily family, ResponseCallback callback);

    Request<T> onStatus(StatusFamily family, ResponseRequestCallback<T> callback);

    <E extends T> Request<T> onSuccess(PayloadCallback<E> callback);

    <E extends T> Request<T> onSuccess(PayloadResponseCallback<E> callback);

    <E extends T> Request<T> onSuccess(PayloadResponseRequestCallback<E> callback);

    Request<T> onTimeout(TimeoutCallback callback);

    Request<T> onTimeout(TimeoutRequestCallback<T> callback);

    Request<T> onUpProgress(ProgressCallback callback);

    Request<T> onUpProgress(ProgressRequestCallback<T> callback);

}
