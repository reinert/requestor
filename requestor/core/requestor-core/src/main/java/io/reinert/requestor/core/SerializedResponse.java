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

import io.reinert.requestor.core.payload.SerializedPayload;

/**
 * Represents a raw HTTP response.
 *
 * @author Danilo Reinert
 */
public interface SerializedResponse extends ResponseHeader {

    /**
     * Return the serialized response body.
     *
     * @return the response's serialized payload
     */
    SerializedPayload getSerializedPayload();

    // ========================================================================
    // Store
    // ========================================================================

    @Override
    SerializedResponse save(String key, Object value);

    @Override
    SerializedResponse save(String key, Object value, Level level);

    @Override
    SerializedResponse save(String key, Object value, long ttl, Level level);

    @Override
    SerializedResponse save(String key, Object value, long ttl);

    @Override
    SerializedResponse onSaved(String key, Handler handler);

    @Override
    SerializedResponse onRemoved(String key, Handler handler);

    @Override
    SerializedResponse onExpired(String key, Handler handler);

}
