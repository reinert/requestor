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

import java.io.Serializable;

/**
 * A progress event.
 *
 * @author Danilo Reinert
 */
public interface ProgressEvent extends Serializable {

    /**
     * Informs whether the request has a computable length available.
     *
     * @return if the request body length is computable
     */
    boolean lengthComputable();

    /**
     * Returns the loaded amount of the request.
     * If this property doesn't exist, then 0 is returned.
     *
     * @return The loaded amount if available, 0 otherwise
     */
    long loaded();

    /**
     * Returns the total amount of the request.
     * If this property doesn't exist, then 0 is returned.
     *
     * @return The total amount if available, 0 otherwise
     */
    long total();
}
