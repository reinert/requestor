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
package io.reinert.requestor.test.books;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;

/**
 * @author Danilo Reinert
 */
public class BookJsonSerdes extends JsonObjectSerdes<Book> {

    private static BookJsonSerdes INSTANCE = new BookJsonSerdes();

    public BookJsonSerdes() {
        super(Book.class);
    }

    public static BookJsonSerdes getInstance() {
        return INSTANCE;
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
    public Book readJson(JsonRecordReader reader, DeserializationContext context) {
        return new Book(reader.readInteger("id"),
                reader.readString("title"),
                reader.readString("author"));
    }

    /**
     * Map T as JavaScriptObject to serialize using JSON.stringify.
     * <p/>
     * You may use {@link org.turbogwt.core.util.Overlays} helper methods to easily perform this mapping.
     *
     * @param book    The object to be serialized
     * @param writer  The serializing JSON
     * @param context Context of the serialization
     */
    @Override
    public void writeJson(Book book, JsonRecordWriter writer, SerializationContext context) {
        writer.writeInt("id", book.getId())
                .writeString("title", book.getTitle())
                .writeString("author", book.getAuthor());
    }
}
