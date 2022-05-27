/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.gson;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.google.gson.stream.JsonWriter;

/**
 * A JsonWriter capable of filtering the fields that should be serialized.
 *
 * @author Danilo Reinert
 */
public class FilterableJsonWriter extends JsonWriter {

    private final Set<String> fields;
    private String skippedField;

    public FilterableJsonWriter(Writer out, Set<String> fields) {
        super(out);
        this.fields = fields;
    }

    @Override
    public JsonWriter name(String name) throws IOException {
        if (shouldSkipField(name)) {
            skipField(name);
        } else {
            cleanSkippedField();
            super.name(name);
        }
        return this;
    }

    @Override
    public JsonWriter value(String value) throws IOException {
        if (isFieldFiltered()) {
            super.value(value);
        }
        return this;
    }

    @Override
    public JsonWriter jsonValue(String value) throws IOException {
        if (isFieldFiltered()) {
            super.jsonValue(value);
        }
        return this;
    }

    @Override
    public JsonWriter nullValue() throws IOException {
        if (isFieldFiltered()) {
            super.nullValue();
        }
        return this;
    }

    @Override
    public JsonWriter value(boolean value) throws IOException {
        if (isFieldFiltered()) {
            super.value(value);
        }
        return this;
    }

    @Override
    public JsonWriter value(Boolean value) throws IOException {
        if (isFieldFiltered()) {
            super.value(value);
        }
        return this;
    }

    @Override
    public JsonWriter value(double value) throws IOException {
        if (isFieldFiltered()) {
            super.value(value);
        }
        return this;
    }

    @Override
    public JsonWriter value(long value) throws IOException {
        if (isFieldFiltered()) {
            super.value(value);
        }
        return this;
    }

    @Override
    public JsonWriter value(Number value) throws IOException {
        if (isFieldFiltered()) {
            super.value(value);
        }
        return this;
    }

    private void cleanSkippedField() {
        this.skippedField = null;
    }

    private boolean isFieldFiltered() {
        return skippedField == null;
    }

    private boolean shouldSkipField(String field) {
        return !(fields.isEmpty() || fields.contains(field));
    }

    private void skipField(String field) {
        this.skippedField = field;
    }
}
