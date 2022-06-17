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
package io.reinert.requestor.java.serialization;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Random;

import io.reinert.requestor.core.FormData;
import io.reinert.requestor.core.HttpSerializationContext;
import io.reinert.requestor.core.header.ContentTypeHeader;
import io.reinert.requestor.core.header.Param;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.SerializationException;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.java.payload.CompositeSerializedPayload;

/**
 * InputStream serializer for FormDatas.
 *
 * @author Danilo Reinert
 */
public class FormDataMultiPartSerializer implements Serializer<FormData> {

    public static final String LINE_FEED = "\r\n";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static String[] MEDIA_TYPE_PATTERNS = new String[]{MULTIPART_FORM_DATA};

    private static final FormDataMultiPartSerializer INSTANCE = new FormDataMultiPartSerializer();

    public static FormDataMultiPartSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<FormData> handledType() {
        return FormData.class;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(FormData formData, SerializationContext context) {
        if (formData == null || formData.isEmpty()) return SerializedPayload.EMPTY_PAYLOAD;

        final String boundary = "----RequestorFormBoundary" + generateRandomString(16);

        final String charset = context.getCharset();

        ((HttpSerializationContext) context).getRequest().setContentType(
                new ContentTypeHeader(MULTIPART_FORM_DATA, Param.of("boundary", boundary)));

        if (formData.isPlain()) {
            final StringBuilder sb = new StringBuilder();

            for (FormData.Param param : formData) {
                if (param == null) continue;

                Object value = param.getValue();
                String name = param.getName();

                checkNameNotNull(name);
                checkValueNotNull(value, name);

                addFormField(sb, boundary, name, (String) value, charset);
            }

            sb.append("--").append(boundary).append("--").append(LINE_FEED);

            return new TextSerializedPayload(sb.toString(), charset);
        }

        final CompositeSerializedPayload csp = new CompositeSerializedPayload();

        for (FormData.Param param : formData) {
            if (param == null) continue;

            Object value = param.getValue();
            String name = param.getName();

            checkNameNotNull(name);
            checkValueNotNull(value, name);

            if (value instanceof String) {
                addFormField(csp, boundary, name, (String) value, charset);
            } else if (value instanceof File) {
                File file = (File) value;
                String fileName = param.getFileName() != null ? param.getFileName() : file.getName();
                addFilePart(csp, boundary, name, file, fileName, context);
            } else if (value instanceof InputStream) {
                String fileName = param.getFileName() != null ? param.getFileName() : generateRandomString(8);
                addStreamPart(csp, boundary, name, (InputStream) value, fileName, context);
            } else {
                throw new SerializationException("Cannot serialize an instance of " + value.getClass().getName()
                        + " as part of a multipart/form-data serialized payload.");
            }
        }

        csp.add(new TextSerializedPayload(LINE_FEED + "--" + boundary + "--" + LINE_FEED, charset));

        return csp;
    }

    private void checkValueNotNull(Object value, String name) {
        if (value == null) {
            throw new SerializationException("Cannot serialized a null FormData param." +
                    " The value associated with the name '" + name + "' is null.");
        }
    }

    private void checkNameNotNull(String name) {
        if (name == null) {
            throw new SerializationException("Cannot serialized a FormData param with name equals to null.");
        }
    }

    @Override
    public SerializedPayload serialize(Collection<FormData> c, SerializationContext context) {
        throw new UnsupportedOperationException("Cannot serialize a collection of FormData.");
    }

    @Override
    public FormData deserialize(SerializedPayload payload, DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a FormData.");
    }

    @Override
    public <C extends Collection<FormData>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                          DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a collection of FormData.");
    }

    public void addFormField(StringBuilder sb, String boundary, String name, String value, String charset) {
        sb.append("--").append(boundary)
                .append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"").append(name).append('"')
                .append(LINE_FEED)
                .append("Content-Type: text/plain; charset=").append(charset)
                .append(LINE_FEED)
                .append(LINE_FEED)
                .append(value)
                .append(LINE_FEED);
    }

    public void addFormField(CompositeSerializedPayload csp, String boundary, String name, String value,
                             String charset) {
        addPart(csp, boundary, name, null, "text/plain; charset=" + charset, null, value, charset);
    }

    public void addStreamPart(CompositeSerializedPayload csp, String boundary, String fieldName, InputStream in,
                              String fileName, SerializationContext context) {
        String contentType = URLConnection.guessContentTypeFromName(fileName);

        addPart(csp, boundary, fieldName, fileName, contentType, "binary", null, context.getCharset());

        csp.add(InputStreamSerializer.getInstance().serialize(in, context));
    }

    public void addFilePart(CompositeSerializedPayload csp, String boundary, String fieldName, File file,
                            String fileName, SerializationContext context) {
        String contentType = URLConnection.guessContentTypeFromName(fileName);

        addPart(csp, boundary, fieldName, fileName, contentType, "binary", null, context.getCharset());

        csp.add(FileSerializer.getInstance().serialize(file, context));
    }

    private void addPart(CompositeSerializedPayload csp, String boundary, String fieldName, String fileName,
                         String contentType, String contentEncoding, String value, String charset) {
        csp.add(new TextSerializedPayload((csp.isEmpty() ? "--" : LINE_FEED + "--") +
                boundary +
                LINE_FEED +
                "Content-Disposition: form-data; name=\"" + fieldName +
                (fileName != null ? "\"; filename=\"" + fileName + '"' : '"') +
                LINE_FEED +
                ((contentType != null && !contentType.isEmpty()) ? "Content-Type: " + contentType + LINE_FEED : "") +
                (contentEncoding != null ? "Content-Transfer-Encoding: " + contentEncoding + LINE_FEED : "") +
                LINE_FEED +
                (value != null ? value : ""), charset));
    }

    private static String generateRandomString(int length) {
        return new Random().ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
