/*
 * Copyright 2014-2021 Danilo Reinert
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
 *
 * @author Danilo Reinert
 */
public class RequestException extends RuntimeException {

    private static final long serialVersionUID = -3805456297999473202L;

    private String uri;
    private HttpMethod method;

    protected RequestException() {
        super();
    }

    protected RequestException(RequestOptions requestOptions, String message) {
        super(message);
        this.uri = requestOptions.getUri().toString();
        this.method = requestOptions.getMethod();
    }

    protected RequestException(RequestOptions requestOptions, Throwable cause) {
        super(cause);
        this.uri = requestOptions.getUri().toString();
        this.method = requestOptions.getMethod();
    }

    protected RequestException(RequestOptions requestOptions, String message, Throwable cause) {
        super(message, cause);
        this.uri = requestOptions.getUri().toString();
        this.method = requestOptions.getMethod();
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
