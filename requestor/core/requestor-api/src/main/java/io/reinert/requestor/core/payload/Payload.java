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
package io.reinert.requestor.core.payload;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds a deserialized payload.
 *
 * @author Danilo Reinert
 */
public class Payload {

    public static final Payload EMPTY_PAYLOAD = new Payload(null);

    private final Object object;
    private final Set<String> fields;

    public Payload(Object object, String... fields) {
        this.object = object;
        if (fields.length == 0) {
            this.fields = Collections.emptySet();
        } else {
            Set<String> fieldsSet = new HashSet<String>(fields.length);
            Collections.addAll(fieldsSet, fields);
            this.fields = Collections.unmodifiableSet(fieldsSet);
        }
    }

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return object == null;
    }

    @SuppressWarnings("unchecked")
    public <T> T asObject() {
        return (T) object;
    }

    public Set<String> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return object != null ? object.toString() : "";
    }
}
