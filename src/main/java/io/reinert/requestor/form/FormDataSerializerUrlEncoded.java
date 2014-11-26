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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.http.client.URL;

import io.reinert.requestor.Payload;

class FormDataSerializerUrlEncoded implements FormDataSerializer {

    private static Logger logger = Logger.getLogger(FormDataSerializerUrlEncoded.class.getName());

    @Override
    public Payload serialize(FormData formData) {
        StringBuilder serialized = new StringBuilder();

        final FormElement formElement = formData.getFormElement();
        if (formElement != null) {
            final NodeCollection<Element> elements = formElement.getElements();
            for (int i = 0; i < elements.getLength(); i++) {
                Element field =  elements.getItem(i);
                if (!field.hasAttribute("name"))
                    continue;

                String type = field.getNodeName().equalsIgnoreCase("input") ?
                        field.getAttribute("type").toUpperCase() : "TEXT";

                if (type.equals("FILE")) {
                    logger.log(Level.WARNING, "An attempt to serialize a non-string value from a FormData has failed." +
                            " Files and Blobs are not supported by FormDataSerializerUrlEncodedImpl." +
                            " Maybe you want to switch the selected FormDataSerializer via deferred binding.");
                    continue;
                }

                serialized.append(encode(getName(field)));
                serialized.append('=');
                serialized.append(encode(getValue(field)));
                serialized.append('&');
            }
            serialized.setLength(serialized.length() - 1);
            return new Payload(serialized.toString());
        }

        for (FormData.Param param : formData) {
            final Object value = param.getValue();
            if (value instanceof String) {
                serialized.append(encode(param.getName()));
                serialized.append('=');
                serialized.append(encode((String) value));
                serialized.append('&');
            } else {
                logger.log(Level.WARNING, "An attempt to serialize a non-string value from a FormData has failed." +
                        " Files and Blobs are not supported by FormDataSerializerUrlEncodedImpl." +
                        " Maybe you want to switch the selected FormDataSerializer via deferred binding.");
            }
        }
        serialized.setLength(serialized.length() - 1);
        return new Payload(serialized.toString());
    }

    private String encode(String value) {
        return URL.encodeQueryString(value);
    }

    private native String getName(Element e) /*-{
        return e.name;
    }-*/;

    private native String getValue(Element e) /*-{
        return (e.value && '' + e.value) || '';
    }-*/;
}
