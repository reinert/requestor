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
 * Allows one to modify a HTTP Response Payload.
 *
 * @author Danilo Reinert
 */
public interface ResponseInterceptorContext {

    String getHeader(String header);

    String getContentType();

    int getStatusCode();

    Response.StatusType getStatus();

    Payload getPayload();

    ResponseType getResponseType();

    void setContentType(String mediaType);

    void setPayload(Payload payload);

    void setResponseType(ResponseType responseType);

}
