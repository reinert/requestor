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

import java.util.concurrent.Future;

import io.reinert.requestor.core.payload.SerializedPayload;

/**
 * An HTTP response with futures.
 *
 * @author Danilo Reinert
 */
public interface IncomingResponse extends ResponseHeader {

    /**
     * Return the serialized payload future.
     *
     * @return the response's serialized payload future
     */
    Future<SerializedPayload> getSerializedPayload();

    /**
     * Return the response payload future.
     *
     * @return the response's payload future
     */
    <T> Future<T> getPayload();

}
