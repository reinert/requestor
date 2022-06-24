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
 * <p>Holds the information associated with a request attempt that may be retried.</p>
 *
 * <p>It is the argument of the {@link RetryPolicy} functional interface.</p>
 *
 * @author Danilo Reinert
 */
public class RequestAttempt {

    private final PreparedRequest request;
    private final Response response;
    private final RequestException exception;
    private final int retryCount;

    RequestAttempt(PreparedRequest request, int retryCount, Response response) {
        this.request = request;
        this.response = response;
        this.retryCount = retryCount;
        this.exception = null;
    }

    RequestAttempt(PreparedRequest request, int retryCount, RequestException exception) {
        this.request = request;
        this.exception = exception;
        this.retryCount = retryCount;
        this.response = null;
    }

    public boolean isResponseAvailable() {
        return response != null;
    }

    public boolean isExceptionAvailable() {
        return exception != null;
    }

    public RequestOptions getRequest() {
        return request;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Response getResponse() {
        return response;
    }

    public RequestException getException() {
        return exception;
    }

    public RequestEvent getEvent() {
        return response != null ? response.getStatus() : exception.getEvent();
    }

    public int getTimeout() {
        return request.getTimeout();
    }

    public void setTimeout(int timeoutMillis) {
        request.setTimeout(timeoutMillis);
    }
}
