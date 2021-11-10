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

    private final Request request;

    public RequestException(Request request, String message) {
        super(message);
        this.request = request;
    }

    public RequestException(Request request, Throwable cause) {
        super(cause);
        this.request = request;
    }

    public RequestException(Request request, String message, Throwable cause) {
        super(message, cause);
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
