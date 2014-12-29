/*
 * Copyright 2014 Danilo Reinert
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
package io.reinert.requestor.gdeferred;

import io.reinert.gdeferred.Promise;
import io.reinert.requestor.RequestProgress;

/**
 * A Promise for requests.
 *
 * @param <T> The type of successful result
 *
 * @author Danilo Reinert
 */
public interface RequestPromise<T> extends Promise<T, Throwable, RequestProgress> {

    RequestPromise<T> upProgress(io.reinert.gdeferred.ProgressCallback<RequestProgress> callback);

    @Override
    RequestPromise<T> always(io.reinert.gdeferred.AlwaysCallback<T, Throwable> callback);

    @Override
    RequestPromise<T> done(io.reinert.gdeferred.DoneCallback<T> callback);

    @Override
    RequestPromise<T> fail(io.reinert.gdeferred.FailCallback<Throwable> callback);

    @Override
    RequestPromise<T> progress(io.reinert.gdeferred.ProgressCallback<RequestProgress> callback);

    @Override
    RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback);

    @Override
    RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                           io.reinert.gdeferred.FailCallback<Throwable> failCallback);

    @Override
    RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                           io.reinert.gdeferred.FailCallback<Throwable> failCallback,
                           io.reinert.gdeferred.ProgressCallback<RequestProgress> progressCallback);

    RequestPromise<T> then(io.reinert.gdeferred.DoneCallback<T> doneCallback,
                           io.reinert.gdeferred.FailCallback<Throwable> failCallback,
                           io.reinert.gdeferred.ProgressCallback<RequestProgress> progressCallback,
                           io.reinert.gdeferred.ProgressCallback<RequestProgress> upProgressCallback);

}
