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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.http.client.URL;

import io.reinert.requestor.core.FormData;
import io.reinert.requestor.core.FormDataSerializerUrlEncoded;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;

/**
 * FormDataSerializer that serialize the {@link FormData} into chained URL encoded key-value pairs.
 * It does not support serializing {@link com.google.gwt.core.client.JavaScriptObject}.
 *
 * @author Danilo Reinert
 */
class GwtFormDataSerializerUrlEncoded extends FormDataSerializerUrlEncoded {

    public static final String MEDIA_TYPE = "application/x-www-form-urlencoded";

    private static final Logger logger = Logger.getLogger(GwtFormDataSerializerUrlEncoded.class.getName());

    @Override
    public Class<FormData> handledType() {
        return FormData.class;
    }

    @Override
    public String[] mediaType() {
        return new String[] { MEDIA_TYPE };
    }

    @Override
    public String serialize(FormData formData, SerializationContext context) {
        if (!(formData instanceof JsFormData)) return super.serialize(formData, context);

        final FormElement formElement = ((JsFormData) formData).getFormElement();

        if (formElement == null) return super.serialize(formData, context);

        StringBuilder serialized = new StringBuilder();
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
        return serialized.toString();
    }

    @Override
    public String serialize(Collection<FormData> c, SerializationContext context) {
        throw new UnsupportedOperationException("Can only serialize a single instance of FormData.");
    }

    @Override
    public FormData deserialize(String response, DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to FormData.");
    }

    @Override
    public <C extends Collection<FormData>> C deserialize(Class<C> collectionType, String response,
                                                          DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to FormData.");
    }

    @Override
    protected String encode(String value) {
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
