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
package io.reinert.requestor.core;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.core.uri.UriCodec;

/**
 * FormDataSerializer that serialize the {@link FormData} into chained URL encoded key-value pairs.
 *
 * @author Danilo Reinert
 */
public class FormDataSerializerUrlEncoded implements Serializer<FormData> {

    public static final String MEDIA_TYPE = "application/x-www-form-urlencoded";

    private static final Logger logger = Logger.getLogger(FormDataSerializerUrlEncoded.class.getName());
    private static final UriCodec uriCodec = UriCodec.getInstance();

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
        StringBuilder serialized = new StringBuilder();

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

    protected String encode(String value) {
        return uriCodec.encodeQueryString(value);
    }
}