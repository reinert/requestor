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
package io.reinert.requestor.form;

import io.reinert.requestor.Payload;

/**
 * Serializes {@link FormData} objects.
 */
public interface FormDataSerializer {

    /**
     * The media type which should be placed as the Content-Type header of the request.
     * <p>
     * Return either a valid media type or null if the Content-Type header shouldn't be set.
     *
     * @return the media type of the payload or {@code null} the the Content-Type header shouldn't be set.
     */
    String mediaType();

    /**
     * Receives a {@link FormData} and serializes it into a {@link Payload}.
     *
     * @param formData the FormData to be serialized
     *
     * @return the serialized Payload
     */
    Payload serialize(FormData formData);
}
