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
 * A Promise for requests.
 *
 * @param <T> The type of successful result
 *
 * @author Danilo Reinert
 */
public interface Promise<T> {

    Promise<T> onAbort(ExceptionCallback callback);

    Promise<T> onLoad(ResponseCallback callback);

    Promise<T> onFail(ResponseCallback callback);

    Promise<T> onProgress(ProgressCallback callback);

    Promise<T> onStatus(int statusCode, ResponseCallback callback);

    Promise<T> onStatus(Status status, ResponseCallback callback);

    Promise<T> onStatus(StatusFamily family, ResponseCallback callback);

    <E extends T> Promise<T> onSuccess(PayloadCallback<E> callback);

    <E extends T> Promise<T> onSuccess(PayloadResponseCallback<E> callback);

    Promise<T> onTimeout(TimeoutCallback callback);

    Promise<T> onUpProgress(ProgressCallback callback);

}
