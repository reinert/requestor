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
package io.reinert.requestor.gwt;

import com.google.gwt.dom.client.FormElement;

import io.reinert.requestor.core.FormData;

/**
 * Represents FormData interface.
 *
 * @author Danilo Reinert
 */
public class JsFormData extends FormData {

    private final FormElement formElement;

    private JsFormData(FormElement formElement) {
        super(null, false);
        this.formElement = formElement;
    }

    public static JsFormData wrap(FormElement formElement) {
        return new JsFormData(formElement);
    }

    public FormElement getFormElement() {
        return formElement;
    }

    @Override
    public boolean isEmpty() {
        return (formElement == null || formElement.getElements().getLength() == 0) && super.isEmpty();
    }
}
