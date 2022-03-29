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
package io.reinert.requestor.net.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.SerializationException;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.net.payload.CompositeSerializedPayload;
import io.reinert.requestor.net.payload.InputStreamSerializedPayload;

/**
 * InputStream serializer for files.
 *
 * @author Danilo Reinert
 */
public class FileSerializer implements Serializer<File> {

    public static String[] MEDIA_TYPE_PATTERNS = new String[]{"*/*"};

    private static final FileSerializer INSTANCE = new FileSerializer();

    public static FileSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<File> handledType() {
        return File.class;
    }

    @Override
    public String[] mediaType() {
        return MEDIA_TYPE_PATTERNS;
    }

    @Override
    public SerializedPayload serialize(File file, SerializationContext context) {
        if (file == null || file.length() == 0) return SerializedPayload.EMPTY_PAYLOAD;
        try {
            return new InputStreamSerializedPayload(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new SerializationException("Failed to serialize a File object: not found.", e);
        }
    }

    @Override
    public SerializedPayload serialize(Collection<File> c, SerializationContext context) {
        final CompositeSerializedPayload csp = new CompositeSerializedPayload();
        for (File file : c) {
            try {
                csp.add(new InputStreamSerializedPayload(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                throw new SerializationException("Failed to serialize a File object: not found.", e);
            }
        }
        return csp;
    }

    @Override
    public File deserialize(SerializedPayload payload, DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a File.");
    }

    @Override
    public <C extends Collection<File>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                      DeserializationContext context) {
        throw new UnsupportedOperationException("Cannot deserialize to a collection of File.");
    }
}
