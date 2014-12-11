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

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.Payload;

/**
 * FormDataSerializer based on native JS FormData object.
 *
 * @author Danilo Reinert
 */
public class FormDataSerializerNative implements FormDataSerializer {

    @Override
    public String mediaType() {
        return "multipart/form-data";
    }

    @Override
    public Payload serialize(FormData formData) {
        if (formData.getFormElement() != null)
            return new Payload(FormDataOverlay.create(formData.getFormElement()));

        FormDataOverlay overlay = FormDataOverlay.create();
        for (FormData.Param param : formData) {
            final Object value = param.getValue();
            if (value instanceof String) {
                overlay.append(param.getName(), (String) value);
            } else {
                overlay.append(param.getName(), (JavaScriptObject) value, param.getFileName());
            }
        }
        return new Payload(overlay);
    }
}
