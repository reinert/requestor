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

import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.core.uri.UriCodec;

/**
 * FormDataSerializer that serialize the {@link FormData} into chained URL encoded key-value pairs.
 *
 * @author Danilo Reinert
 */
public class FormDataUrlEncodedSerializer implements Serializer<FormData> {

    public static final String MEDIA_TYPE = "application/x-www-form-urlencoded";

    @Override
    public Class<FormData> handledType() {
        return FormData.class;
    }

    @Override
    public String[] mediaType() {
        return new String[] { MEDIA_TYPE };
    }

    @Override
    public SerializedPayload serialize(FormData formData, SerializationContext context) {
        if (formData == null || formData.isEmpty()) return SerializedPayload.EMPTY_PAYLOAD;

        StringBuilder serialized = new StringBuilder();

        for (FormData.Param param : formData) {
            final Object value = param.getValue();
            if (value instanceof String) {
                // append 'name=value&'
                serialized.append(encode(param.getName())).append('=').append(encode((String) value)).append('&');
            } else {
                throw new UnsupportedOperationException("An attempt to serialize a non-string value from a FormData" +
                        " has failed. Files and Blobs are not supported by FormDataSerializerUrlEncoded." +
                        " You may want to switch the Content-Type to 'multipart/form-data'.");
            }
        }
        serialized.setLength(serialized.length() - 1); // remove last '&' character
        return new TextSerializedPayload(serialized.toString());
    }

    @Override
    public SerializedPayload serialize(Collection<FormData> c, SerializationContext context) {
        throw new UnsupportedOperationException("Can only serialize a single instance of FormData.");
    }

    @Override
    public FormData deserialize(SerializedPayload payload, DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to FormData.");
    }

    @Override
    public <C extends Collection<FormData>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                          DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to FormData.");
    }

    protected String encode(String value) {
        return UriCodec.getInstance().encodeQueryString(value);
    }
}
