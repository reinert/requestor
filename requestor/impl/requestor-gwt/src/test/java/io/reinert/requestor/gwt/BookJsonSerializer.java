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

import com.google.gwt.i18n.client.DateTimeFormat;

import io.reinert.requestor.gwt.serialization.JsonObjectSerializer;
import io.reinert.requestor.gwt.serialization.JsonRecordReader;
import io.reinert.requestor.gwt.serialization.JsonRecordWriter;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;

/**
 * Custom JSON Serializer for {@link Book}.
 *
 * @author Danilo Reinert
 */
public class BookJsonSerializer extends JsonObjectSerializer<Book> {

    private static DateTimeFormat ISO_8601 = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);
    private static BookJsonSerializer INSTANCE = new BookJsonSerializer();

    public BookJsonSerializer() {
        super(Book.class);
    }

    public static BookJsonSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Book readJson(JsonRecordReader reader, DeserializationContext context) {
        return new Book(Integer.parseInt(reader.readString("id")),
                reader.readString("title"),
                reader.readString("author"),
                ISO_8601.parse(reader.readString("publicationDate")));
    }

    @Override
    public void writeJson(Book book, JsonRecordWriter writer, SerializationContext context) {
        if (book.getId() != null) writer.writeString("id", book.getId().toString());
        writer.writeString("title", book.getTitle())
                .writeString("author", book.getAuthor())
                .writeString("publicationDate", ISO_8601.format(book.getPublicationDate()));
    }
}
