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
package io.reinert.requestor.core;

/**
 * Responsible for retrying a request.
 *
 * @author Danilo Reinert
 */
public class RequestRetrier {

    private final PreparedRequest preparedRequest;
    private final AsyncRunner asyncRunner;
    private final RetryPolicy retryPolicy;
    private int retryCount;

    RequestRetrier(PreparedRequest preparedRequest, AsyncRunner asyncRunner, RetryPolicy retryPolicy) {
        this.preparedRequest = preparedRequest;
        this.asyncRunner = asyncRunner;
        this.retryPolicy = retryPolicy;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public boolean maybeRetry(Response response) {
        return maybeRetry(new RequestAttempt(preparedRequest, retryCount, response));
    }

    public boolean maybeRetry(RequestException exception) {
        return maybeRetry(new RequestAttempt(preparedRequest, retryCount, exception));
    }

    private boolean maybeRetry(RequestAttempt attempt) {
        int nextRetryDelay = retryPolicy.retryIn(attempt);

        if (nextRetryDelay > 0) {
            retryCount++;
            asyncRunner.run(new Runnable() {
                public void run() {
                    preparedRequest.send();
                }
            }, nextRetryDelay);
            return true;
        }

        return false;
    }
}
