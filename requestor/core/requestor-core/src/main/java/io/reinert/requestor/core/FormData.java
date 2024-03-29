/*
 * Copyright 2014-2021 Danilo Reinert
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents FormData interface.
 *
 * @author Danilo Reinert
 */
public class FormData implements Iterable<FormData.Param> {

    private final List<Param> params;
    private final boolean plain;

    protected FormData(ArrayList<Param> params, boolean isPlain) {
        this.params = params;
        this.plain = isPlain;
    }

    public static FormData.Builder builder() {
        return new FormData.Builder();
    }

    public boolean isEmpty() {
        return params == null || params.isEmpty();
    }

    public boolean isPlain() {
        return plain;
    }

    @Override
    public Iterator<Param> iterator() {
        return params == null ? Collections.<Param>emptySet().iterator() : params.iterator();
    }

    public static class Builder {

        private final ArrayList<Param> params = new ArrayList<Param>();
        private boolean plain = true;

        private Builder() {
        }

        public Builder append(String name, String value) {
            params.add(new Param(name, value));
            return this;
        }

        public Builder append(String name, int value) {
            params.add(new Param(name, String.valueOf(value)));
            return this;
        }

        public Builder append(String name, long value) {
            params.add(new Param(name, String.valueOf(value)));
            return this;
        }

        public Builder append(String name, double value) {
            params.add(new Param(name, String.valueOf(value)));
            return this;
        }

        public Builder append(String name, boolean value) {
            params.add(new Param(name, String.valueOf(value)));
            return this;
        }

        public Builder append(String name, Iterable<?> values) {
            for (Object value : values) {
                params.add(new Param(name, value.toString()));
            }
            return this;
        }

        public Builder append(String name, Object file, String fileName) {
            params.add(new Param(name, file, fileName));
            plain = false;
            return this;
        }

        public FormData build() {
            return new FormData(params, plain);
        }
    }

    public static class Param {

        private String name;
        private Object objectValue;
        private String stringValue;
        private String fileName;

        public Param(String name, String value) {
            this.name = name;
            this.stringValue = value;
        }

        public Param(String name, Object value, String fileName) {
            this.name = name;
            this.objectValue = value;
            this.fileName = fileName;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return objectValue != null ? objectValue : stringValue;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
