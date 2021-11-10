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
package io.reinert.requestor.core;

/**
 * Superclass for the HTTP request related exceptions.
 */
public class RequestException extends RuntimeException {

    private final RequestOptions requestOptions;

    public RequestException(RequestOptions requestOptions, String message) {
        super(message);
        this.requestOptions = requestOptions;
    }

    public RequestException(RequestOptions requestOptions, Throwable cause) {
        super(cause);
        this.requestOptions = requestOptions;
    }

    public RequestException(RequestOptions requestOptions, String message, Throwable cause) {
        super(message, cause);
        this.requestOptions = requestOptions;
    }

    public RequestOptions getRequestOptions() {
        return requestOptions;
    }
}
