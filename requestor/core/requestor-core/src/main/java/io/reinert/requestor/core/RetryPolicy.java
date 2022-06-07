/*
 * Copyright 2022 Danilo Reinert
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

/**
 * Abstraction for retry policies.
 *
 * @author Danilo Reinert
 */
public interface RetryPolicy {

    interface Provider extends io.reinert.requestor.core.Provider<RetryPolicy> { }

    /**
     * <p>Checks the request result and returns the time to wait until the next retry in milliseconds.</p>
     *
     * <p>If the returned time is negative, then the request won't be retried and will fail.</p>
     *
     * @param attempt   The data of the request under submission
     *
     * @return  The time in milliseconds of the next retry.
     *          The request won't be retried If this number is negative.
     */
    int retryIn(RequestAttempt attempt);
}
