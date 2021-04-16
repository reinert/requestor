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

import java.util.Collection;

public class CollectionPayloadType extends RootPayloadType {

    private final Class<? extends Collection> collectionType;
    private final RootPayloadType parametrizedType;

    public CollectionPayloadType(Class<? extends Collection> collectionType, RootPayloadType parametrizedType) {
        this.collectionType = collectionType;
        this.parametrizedType = parametrizedType;
    }

    public Class<? extends Collection> getCollectionType() {
        return collectionType;
    }

    public RootPayloadType getParametrizedType() {
        return parametrizedType;
    }

    @Override
    public Class<?> getType() {
        return collectionType;
    }
}
