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
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.ProgressCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.callback.TimeoutCallback;

/**
 * An observable Request.
 *
 * @param <T> The type of successful result
 *
 * @author Danilo Reinert
 */
public interface Request<T> {

    Store getStore();

    Request<T> onAbort(ExceptionCallback callback);

    Request<T> onLoad(ResponseCallback callback);

    Request<T> onFail(ResponseCallback callback);

    Request<T> onProgress(ProgressCallback callback);

    Request<T> onStatus(int statusCode, ResponseCallback callback);

    Request<T> onStatus(Status status, ResponseCallback callback);

    Request<T> onStatus(StatusFamily family, ResponseCallback callback);

    <E extends T> Request<T> onSuccess(PayloadCallback<E> callback);

    <E extends T> Request<T> onSuccess(PayloadResponseCallback<E> callback);

    Request<T> onTimeout(TimeoutCallback callback);

    Request<T> onUpProgress(ProgressCallback callback);

}
