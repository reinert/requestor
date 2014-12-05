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
package io.reinert.requestor.deferred;

/**
 * Promise interface based on Promises/A+ spec.
 *
 * @param <F> The type of the fulfilled result
 *
 * @author Danilo Reinert
 */
public interface Promise<F> {

    /**
     * Informs if the promise is still pending.
     *
     * @return {@code true} if is pending, {@code false} otherwise
     */
    boolean isPending();

    /**
     * Informs if the promise has been rejected.
     *
     * @return {@code true} if is rejected, {@code false} otherwise
     */
    boolean isRejected();

    /**
     * Informs if the promise has finished successfully.
     *
     * @return {@code true} if is fulfilled, {@code false} otherwise
     */
    boolean isFulfilled();

    /**
     * Register a FulFilledCallback so that when the Promise is fulfilled it is called.
     * <p/>
     *
     * You can register multiple callbacks by calling the method multiple times.
     * The order of callback trigger is based on the order they are registered.
     * <p/>
     *
     * @param onFulfilled The callback to be executed when the promise is fulfilled
     *
     * @return The promise returned from the callback
     */
    <R> Promise<R> then(Callback<F, R> onFulfilled);

    /**
     * Register a FulFilledCallback and a RejectedCallback.
     * <p/>
     *
     * You can register multiple callbacks by calling the method multiple times.
     * The order of callback trigger is based on the order they are registered.
     * <p/>
     *
     * @param onFulfilled The callback to be executed when the promise is fulfilled
     * @param onRejected  The callback to be executed when the promise is rejected
     *
     * @return The promise returned from the callback
     */
    <R> Promise<R> then(Callback<F, R> onFulfilled, Callback<Throwable, R> onRejected);
}
