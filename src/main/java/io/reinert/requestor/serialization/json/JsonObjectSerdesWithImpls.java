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
package io.reinert.requestor.serialization.json;

import io.reinert.requestor.serialization.HasImpls;

/**
 * Base class for JSON Object SerDes that handles interfaces and has many implementations.
 * It is used by {@link io.reinert.requestor.rebind.JsonAutoBeanGenerator}.
 *
 * @param <T>   Type of the object to serialize/deserialize.
 *
 * @author Danilo Reinert
 */
public abstract class JsonObjectSerdesWithImpls<T> extends JsonObjectSerdes<T> implements HasImpls {

    public JsonObjectSerdesWithImpls(Class<T> handledType) {
        super(handledType);
    }

    @Override
    public Class<?>[] implTypes() {
        return null;
    }
}
