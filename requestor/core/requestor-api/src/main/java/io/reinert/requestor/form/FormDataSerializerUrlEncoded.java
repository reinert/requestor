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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.http.client.URL;

import io.reinert.requestor.Payload;


class FormDataSerializerUrlEncoded implements FormDataSerializer {

    private static Logger logger = Logger.getLogger(FormDataSerializerUrlEncoded.class.getName());

    @Override
    public String mediaType() {
        return "application/x-www-form-urlencoded";
    }

    @Override
    public Payload serialize(FormData formData) {
        StringBuilder serialized = new StringBuilder();

        final FormElement formElement = formData.getFormElement();
        if (formElement != null) {
            final NodeCollection<Element> elements = formElement.getElements();
            for (int i = 0; i < elements.getLength(); i++) {
                Element field =  elements.getItem(i);

                try {
                    if (!field.hasAttribute("name"))
                        continue;
                } catch (JavaScriptException e) {
                    continue; // variable is not an element
                }

                String type = field.getNodeName().equalsIgnoreCase("input") ?
                        field.getAttribute("type").toLowerCase() : "text";

                if (type.equals("file")) {
                    logger.log(Level.WARNING, "An attempt to serialize a non-string value from a FormData has failed." +
                            " Files and Blobs are not supported by FormDataSerializerUrlEncoded." +
                            " Maybe you want to switch the selected FormDataSerializer via deferred binding.");
                    continue;
                }

                if ((type.equals("radio") || type.equals("checkbox")) && !isChecked(field))
                    continue;

                final String name = encode(getName(field));
                final String value = encode(getValue(field));
                serialized.append(name).append('=').append(value).append('&'); // append 'name=value&'
            }
            serialized.setLength(serialized.length() - 1); // remove last '&' character
            return new Payload(serialized.toString());
        }

        for (FormData.Param param : formData) {
            final Object value = param.getValue();
            if (value instanceof String) {
                // append 'name=value&'
                serialized.append(encode(param.getName())).append('=').append(encode((String) value)).append('&');
            } else {
                logger.log(Level.WARNING, "An attempt to serialize a non-string value from a FormData has failed." +
                        " Files and Blobs are not supported by FormDataSerializerUrlEncoded" +
                        " You may want to switch the selected FormDataSerializer via deferred binding.");
            }
        }
        serialized.setLength(serialized.length() - 1); // remove last '&' character
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

    private native boolean isChecked(Element e) /*-{
        return e.checked || false;
    }-*/;
}
