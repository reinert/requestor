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

/**
 * Holds a deserialized payload.
 *
 * @author Danilo Reinert
 */
public class Payload {

    private final Object object;
    private final String[] fields;

    public Payload(Object object, String... fields) {
        this.object = object;
        this.fields = fields;
    }

    public static final Payload EMPTY_PAYLOAD = new Payload(null);

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return object == null;
    }

    public Object getObject() {
        return object;
    }

    public String[] getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return object != null ? object.toString() : "";
    }
}
