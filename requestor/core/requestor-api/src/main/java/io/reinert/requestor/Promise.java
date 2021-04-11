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
package io.reinert.requestor;

import io.reinert.requestor.callbacks.ExceptionCallback;
import io.reinert.requestor.callbacks.PayloadCallback;
import io.reinert.requestor.callbacks.PayloadResponseCallback;
import io.reinert.requestor.callbacks.ProgressCallback;
import io.reinert.requestor.callbacks.ResponseCallback;
import io.reinert.requestor.callbacks.TimeoutCallback;

/**
 * A Promise for requests.
 *
 * @param <T> The type of successful result
 *
 * @author Danilo Reinert
 */
public interface Promise<T> {

    Promise<T> abort(ExceptionCallback callback);

    Promise<T> load(ResponseCallback<Object> callback);

    Promise<T> fail(ResponseCallback<Object> callback);

    Promise<T> progress(ProgressCallback callback);

    Promise<T> status(int statusCode, ResponseCallback<Object> callback);

    Promise<T> status(Status status, ResponseCallback<Object> callback);

    Promise<T> status(StatusFamily family, ResponseCallback<Object> callback);

    <E extends T> Promise<T> success(PayloadCallback<E> callback);

    <E extends T> Promise<T> success(PayloadResponseCallback<E> callback);

    Promise<T> timeout(TimeoutCallback callback);

    Promise<T> upProgress(ProgressCallback callback);

}
