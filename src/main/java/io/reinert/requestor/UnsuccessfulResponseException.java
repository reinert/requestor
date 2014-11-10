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
package io.reinert.requestor;

/**
 * Thrown to indicate that an HTTP request received a response with other status code then 2xx.
 * It provides access to the response in order to better handle the return.
 *
 * @author Danilo Reinert
 */
public class UnsuccessfulResponseException extends RuntimeException {

    private final Response response;

    /**
     * Constructs the exception with the request and respective response.
     *
     * @param response The response received from request.
     */
    public UnsuccessfulResponseException(Response response) {
        super("The response was received but the status code was not from 'Success' class (2xx).");
        this.response = response;
    }

    /**
     * Returns the unsuccessful response.
     *
     * @return The unsuccessful response.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Response's HTTP status code.
     *
     * @return The response's status code.
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }
}
