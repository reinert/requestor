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
 * Represents a HTTP response.
 *
 * @author Danilo Reinert
 */
public interface Response extends SerializedResponse {

    /**
     * Returns the payload deserialized.
     *
     * @return the response payload
     */
    <T> T getPayload();

    // ========================================================================
    // Store
    // ========================================================================

    @Override
    Response save(String key, Object value);

    @Override
    Response save(String key, Object value, Level level);

    @Override
    Response save(String key, Object value, long ttl, Level level);

    @Override
    Response save(String key, Object value, long ttl);

    @Override
    Response onSaved(String key, Handler handler);

    @Override
    Response onRemoved(String key, Handler handler);

    @Override
    Response onExpired(String key, Handler handler);

}
