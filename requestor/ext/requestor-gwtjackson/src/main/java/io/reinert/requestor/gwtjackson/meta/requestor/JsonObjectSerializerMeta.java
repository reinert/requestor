/*
 * Copyright 2015 Danilo Reinert
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
package io.reinert.requestor.gwtjackson.meta.requestor;

import io.reinert.requestor.gwt.serialization.JsonObjectSerializer;

/**
 * Metadata for {@link JsonObjectSerializer}.
 */
public interface JsonObjectSerializerMeta extends SerializerMeta {

    interface Method extends SerializerMeta.Method {
        String READ_JSON = "readJson";
        String WRITE_JSON = "writeJson";
    }
}
