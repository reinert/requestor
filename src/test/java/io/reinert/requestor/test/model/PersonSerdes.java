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
package io.reinert.requestor.test.model;

import java.util.Date;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;
import io.reinert.requestor.serialization.json.JsonSerdes;

/**
 * @author Danilo Reinert
 */
public class PersonSerdes extends JsonObjectSerdes<Person> {

    private String[] acceptPatterns = JsonSerdes.ACCEPT_PATTERNS;
    private String[] contentTypePatterns = JsonSerdes.CONTENT_TYPE_PATTERNS;

    public PersonSerdes() {
        super(Person.class);
    }

    public void setAcceptPatterns(String... acceptPatterns) {
        this.acceptPatterns = acceptPatterns;
    }

    public void setContentTypePatterns(String... contentTypePatterns) {
        this.contentTypePatterns = contentTypePatterns;
    }

    /**
     * Informs the content type this serializer handle.
     *
     * @return The content type handled by this serializer.
     */
    @Override
    public String[] accept() {
        return acceptPatterns;
    }

    /**
     * Informs the content type this serializer serializes.
     *
     * @return The content type serialized.
     */
    @Override
    public String[] contentType() {
        return contentTypePatterns;
    }

    /**
     * Map response deserialized as JavaScriptObject to T.
     * <p/>
     * You may use {@link org.turbogwt.core.util.Overlays} helper methods to easily perform this mapping.
     *
     * @param reader  The evaluated response
     * @param context Context of the deserialization
     *
     * @return The object deserialized
     */
    @Override
    public Person readJson(JsonRecordReader reader, DeserializationContext context) {
        return new Person(reader.readInteger("id"),
                reader.readString("name"),
                reader.readDouble("weight"),
                new Date(reader.readLong("birthday")));
    }

    /**
     * Map T as JavaScriptObject to serialize using JSON.stringify.
     * <p/>
     * You may use {@link org.turbogwt.core.util.Overlays} helper methods to easily perform this mapping.
     *
     * @param person  The object to be serialized
     * @param writer  The serializing JSON
     * @param context Context of the serialization
     */
    @Override
    public void writeJson(Person person, JsonRecordWriter writer, SerializationContext context) {
        writer.writeInt("id", person.getId())
                .writeString("name", person.getName())
                .writeDouble("weight", person.getWeight())
                .writeDouble("birthday", person.getBirthday().getTime());
    }
}
