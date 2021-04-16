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
package io.reinert.requestor.payload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CompositePayloadType implements PayloadType {

    private final HashMap<String, PayloadType> typeMap;

    private CompositePayloadType(HashMap<String, PayloadType> typeMap) {
        this.typeMap = typeMap;
    }

    @Override
    public Iterator<Map.Entry<String, PayloadType>> iterator() {
        return typeMap.entrySet().iterator();
    }

    @Override
    public Class<?> getType() {
        return HashMap.class;
    }

    public static class Builder {
        private final HashMap<String, PayloadType> typeMap = new HashMap<String, PayloadType>();

        public Builder add(String key, PayloadType type) {
            typeMap.put(key, type);
            return this;
        }

        public CompositePayloadType build() {
            return new CompositePayloadType(typeMap);
        }
    }
}
