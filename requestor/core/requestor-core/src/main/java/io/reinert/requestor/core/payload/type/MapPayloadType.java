/*
 * Copyright 2021 Danilo Reinert
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
package io.reinert.requestor.core.payload.type;

import java.util.Map;

/**
 * Represents a map expected type in the response body.
 *
 * @author Danilo Reinert
 */
public class MapPayloadType<V, K> extends RootPayloadType<Map<K, V>> {

    private final RootPayloadType<V> valuePayloadType;
    private final Class<K> keyClass;
    private final Class<? extends Map> mapClass;

    public MapPayloadType(RootPayloadType<V> valuePayloadType, Class<K> keyClass, Class<? extends Map> mapClass) {
        this.valuePayloadType = valuePayloadType;
        this.keyClass = keyClass;
        this.mapClass = mapClass;
    }

    public RootPayloadType<V> getValuePayloadType() {
        return valuePayloadType;
    }

    public Class<K> getKeyType() {
        return keyClass;
    }

    @Override
    public Class<? extends Map> getType() {
        return mapClass;
    }
}
