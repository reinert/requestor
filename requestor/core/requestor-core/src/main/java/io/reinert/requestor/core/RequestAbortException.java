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
package io.reinert.requestor.core;

/**
 * Thrown to indicate that a request in process has been aborted before being invoked.
 *
 * @author Danilo Reinert
 */
public class RequestAbortException extends RequestException {

    private static final long serialVersionUID = 1153921460393658952L;

    protected RequestAbortException() {
        super();
    }

    public RequestAbortException(RequestOptions requestOptions, String message) {
        super(requestOptions, message);
    }

    public RequestAbortException(RequestOptions requestOptions, String message, Throwable cause) {
        super(requestOptions, message, cause);
    }
}
