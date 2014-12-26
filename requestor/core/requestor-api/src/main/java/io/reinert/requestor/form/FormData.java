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
package io.reinert.requestor.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.FormElement;

/**
 * Represents FormData interface.
 *
 * @author Danilo Reinert
 */
public class FormData implements Iterable<FormData.Param> {

    private final FormElement formElement;
    private final ArrayList<Param> params;

    private FormData(ArrayList<Param> params) {
        this.formElement = null;
        this.params = params;
    }

    private FormData(FormElement formElement) {
        this.formElement = formElement;
        this.params = null;
    }

    public static FormData wrap(FormElement formElement) {
        return new FormData(formElement);
    }

    public static FormData.Builder builder() {
        return new FormData.Builder();
    }

    public FormElement getFormElement() {
        return formElement;
    }

    @Override
    public Iterator<Param> iterator() {
        return params == null ? Collections.<Param>emptyIterator() : params.iterator();
    }

    public static class Builder {

        private final ArrayList<Param> params = new ArrayList<Param>();

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

        public Builder append(String name, JavaScriptObject file, String fileName) {
            params.add(new Param(name, file, fileName));
            return this;
        }

        public FormData build() {
            return new FormData(params);
        }
    }

    static class Param {

        private String name;
        private JavaScriptObject jsoValue;
        private String stringValue;
        private String fileName;

        public Param(String name, String value) {
            this.name = name;
            this.stringValue = value;
        }

        public Param(String name, JavaScriptObject value, String fileName) {
            this.name = name;
            this.jsoValue = value;
            this.fileName = fileName;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return jsoValue != null ? jsoValue : stringValue;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
