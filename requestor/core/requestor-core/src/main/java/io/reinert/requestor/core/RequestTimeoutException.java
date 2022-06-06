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

/**
 * Thrown to indicate that an HTTP request has timed out.
 *
 * @author Danilo Reinert
 */
public class RequestTimeoutException extends RequestException {

    private static final long serialVersionUID = -7097854407360606948L;

    /**
     * Time, in milliseconds, of the timeout.
     */
    private int timeoutMillis;

    protected RequestTimeoutException() {
    }

    @Override
    public Event getEvent() {
        return RequestEvent.TIMEOUT;
    }

    /**
     * Constructs a timeout exception for the given {@link RequestOptions}.
     *
     * @param requestOptions       the request which timed out
     * @param timeoutMillis the number of milliseconds which expired
     */
    public RequestTimeoutException(RequestOptions requestOptions, int timeoutMillis) {
        super(requestOptions, "A request timeout has expired after " + timeoutMillis + " ms");
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * Returns the request timeout value in milliseconds.
     *
     * @return the request timeout value in milliseconds
     */
    public int getTimeoutMillis() {
        return timeoutMillis;
    }
}
