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
 * Thrown to indicate that an HTTP request received a response with other status code then 2xx.
 * It provides access to the response in order to better handle the return.
 *
 * @author Danilo Reinert
 */
public class UnsuccessfulResponseException extends RequestException {

    private static final long serialVersionUID = 1102976224578831562L;

    private HttpStatus status;;

    protected UnsuccessfulResponseException() {
        super();
    }

    /**
     * Constructs the exception with the request and respective response.
     *
     * @param response The response received from request.
     */
    public UnsuccessfulResponseException(RequestOptions requestOptions, Response response) {
        super(requestOptions, "The response was received but the status code was not from 'Success' class (2xx).");
        this.status = response.getStatus();
    }

    public static UnsuccessfulResponseException cast(Throwable throwable) throws ClassCastException {
        return (UnsuccessfulResponseException) throwable;
    }

    /**
     * Response's HTTP status.
     *
     * @return The response's status
     */
    public HttpStatus getStatus() {
        return status;
    }
}
