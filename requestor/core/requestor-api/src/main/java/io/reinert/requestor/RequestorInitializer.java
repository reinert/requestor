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
 * This interface is responsible for performing the default configuration of every {@link Requestor} created.
 * Its implementation could do things like registering Serializers, Filters, Providers, etc.
 *
 * @author Danilo Reinert
 */
public interface RequestorInitializer {

    /**
     * Perform all desired configurations for the requestor initialization.
     *
     * @param requestor The requestor being initialized
     */
    void configure(Requestor requestor);
}
