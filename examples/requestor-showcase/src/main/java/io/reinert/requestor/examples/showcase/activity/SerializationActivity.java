/*
 * Copyright 2015-2022 Danilo Reinert
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
package io.reinert.requestor.examples.showcase.activity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.autobean.RequestorAutoBean;
import io.reinert.requestor.autobean.annotations.AutoBeanSerializationModule;
import io.reinert.requestor.core.Registration;
import io.reinert.requestor.core.SerializationModule;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.examples.showcase.Showcase;
import io.reinert.requestor.examples.showcase.ui.Serialization;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.gwt.serialization.JsonObjectSerializer;
import io.reinert.requestor.gwt.serialization.JsonRecordReader;
import io.reinert.requestor.gwt.serialization.JsonRecordWriter;
import io.reinert.requestor.gwtjackson.RequestorGwtJackson;
import io.reinert.requestor.gwtjackson.annotations.JsonSerializationModule;

public class SerializationActivity extends ShowcaseActivity implements Serialization.Handler {

    private final Serialization view;
    private final Session session;
    private final Session gwtjacksonSession;
    private final Session autobeanSession;

    private Registration xmlSerializerRegistration;
    private Registration jsonSerializerRegistration;

    public SerializationActivity(String section, Serialization view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
        this.gwtjacksonSession = RequestorGwtJackson.newSession();
        this.autobeanSession = RequestorAutoBean.newSession();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);

        xmlSerializerRegistration = session.register(new MySerializer());
        jsonSerializerRegistration = session.register(new MyJsonSerializer());

        Page.setTitle("Serialization");
        Page.setDescription("Exchange any media type with a powerful serialization mechanism.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
        xmlSerializerRegistration.cancel();
        jsonSerializerRegistration.cancel();
    }

    @Override
    public void onGwtJacksonGetBooks() {
        gwtjacksonSession.get(Showcase.CLIENT_FACTORY.getBooksUri().toString(), List.class, BookImpl.class)
                .onSuccess(new PayloadCallback<Collection<BookImpl>>() {
                    public void execute(Collection<BookImpl> books) {
                        view.setGwtJacksonGetBooksText(booksToString(books));
                    }
                });
    }

    @Override
    public void onGwtJacksonPostBook() {
        final BookImpl cleanCode = new BookImpl(1, "Clean Code", "Tech", "9788550811482", "Robert C. Martin",
                new Date(1217552400000L));

        gwtjacksonSession.post(Showcase.CLIENT_FACTORY.getBooksUri().toString(), cleanCode, BookImpl.class)
                .onSuccess(new PayloadCallback<BookImpl>() {
                    public void execute(BookImpl book) {
                        view.setGwtJacksonPostBookText(bookToString(book));
                    }
                });
    }

    @Override
    public void onAutoBeanGetBooks() {
        autobeanSession.get(Showcase.CLIENT_FACTORY.getBooksUri().toString(), List.class, Book.class)
                .onSuccess(new PayloadCallback<Collection<Book>>() {
                    public void execute(Collection<Book> books) {
                        view.setAutoBeanGetBooksText(booksToString(books));
                    }
                });
    }

    @Override
    public void onAutoBeanPostBook() {
        final Book cleanCode = autobeanSession.getInstance(Book.class);
        cleanCode.setId(1);
        cleanCode.setTitle("Clean Code");
        cleanCode.setGenre("Tech");
        cleanCode.setIsbn("9788550811482");
        cleanCode.setAuthor("Robert C. Martin");
        cleanCode.setPubDate(new Date(1217552400000L));

        autobeanSession.post(Showcase.CLIENT_FACTORY.getBooksUri().toString(), cleanCode, Book.class)
                .onSuccess(new PayloadCallback<Book>() {
                    public void execute(Book book) {
                        view.setAutoBeanPostBookText(bookToString(book));
                    }
                });
    }

    @Override
    public void onXmlObjectGet() {
        session.req("https://www.mocky.io/v2/54aa8cf807b5f2bc0f21ba08")
                .get(MyObject.class)
                .onSuccess(new PayloadCallback<MyObject>() {
                    public void execute(MyObject result) {
                        view.setSingleXmlGetText(result.toString());
                    }
                });
    }

    @Override
    public void onXmlCollectionGet() {
        session.req("https://www.mocky.io/v2/54aa8e1407b5f2d20f21ba09")
                .get(List.class, MyObject.class)
                .onSuccess(new PayloadCallback<Collection<MyObject>>() {
                    public void execute(Collection<MyObject> result) {
                        view.setCollectionXmlGetText(Arrays.toString(result.toArray()));
                    }
                });
    }

    @Override
    public void onXmlObjectPost() {
        session.req(Showcase.CLIENT_FACTORY.getPostUri())
                .contentType("application/xml")
                .payload(new MyObject("Lorem", 1900, new Date(1420416000000L)))
                .post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setSingleXmlPostText(result);
                    }
                });
    }

    @Override
    public void onXmlCollectionPost() {
        session.req(Showcase.CLIENT_FACTORY.getPostUri())
                .contentType("application/xml")
                .payload(Arrays.asList(
                        new MyObject("Lorem", 1900, new Date(1420416000000L)),
                        new MyObject("Ipsum", 210, new Date(1420070400000L))))
                .post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setCollectionXmlPostText(result);
                    }
                });
    }

    @Override
    public void onJsonObjectGet() {
            session.req("https://www.mocky.io/v2/54aa93c307b5f2671021ba0c")
                    .get(MyObject.class)
                    .onSuccess(new PayloadCallback<MyObject>() {
                        public void execute(MyObject result) {
                            view.setSingleJsonGetText(result.toString());
                        }
                    });
    }

    @Override
    public void onJsonCollectionGet() {
        session.req("https://www.mocky.io/v2/54aa937407b5f2601021ba0b")
                .get(List.class, MyObject.class)
                .onSuccess(new PayloadCallback<Collection<MyObject>>() {
                    public void execute(Collection<MyObject> result) {
                        view.setCollectionJsonGetText(Arrays.toString(result.toArray()));
                    }
                });
    }

    @Override
    public void onJsonObjectPost() {
        session.req(Showcase.CLIENT_FACTORY.getPostUri())
                .contentType("application/json")
                .payload(new MyObject("Lorem", 1900, new Date(1420416000000L)))
                .post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setSingleJsonPostText(result);
                    }
                });
    }

    @Override
    public void onJsonCollectionPost() {
        session.req(Showcase.CLIENT_FACTORY.getPostUri())
                .contentType("application/json")
                .payload(Arrays.asList(
                        new MyObject("Lorem", 1900, new Date(1420416000000L)),
                        new MyObject("Ipsum", 210, new Date(1420070400000L))))
                .post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setCollectionJsonPostText(result);
                    }
                });
    }

    private String bookToString(Book book) {
        return "Book{" +
                "id=" + book.getId() +
                ", title='" + book.getTitle() + '\'' +
                ", genre='" + book.getGenre() + '\'' +
                ", isbn='" + book.getIsbn() + '\'' +
                ", author='" + book.getAuthor() + '\'' +
                ", pubDate=" + book.getPubDate() +
                "}\n";
    }

    private String booksToString(Collection<? extends Book> books) {
        StringBuilder result = new StringBuilder();
        for (Book b : books) result.append(bookToString(b));
        return result.toString();
    }

    @AutoBeanSerializationModule(Book.class)
    interface BookAutoBeanSerializationModule extends SerializationModule { }

    @JsonSerializationModule(BookImpl.class)
    interface BookGwtJacksonSerializationModule extends SerializationModule { }

    public interface Book {
        int getId();

        void setId(int id);

        String getTitle();

        void setTitle(String title);

        String getAuthor();

        void setAuthor(String author);

        String getIsbn();

        void setIsbn(String isbn);

        String getGenre();

        void setGenre(String genre);

        Date getPubDate();

        void setPubDate(Date pubDate);
    }

    public static class BookImpl implements Book {
        private int id;
        private String title;
        private String genre;
        private String isbn;
        private String author;
        private Date pubDate;

        public BookImpl() { }

        public BookImpl(int id, String title, String genre, String isbn, String author, Date pubDate) {
            this.id = id;
            this.title = title;
            this.genre = genre;
            this.isbn = isbn;
            this.author = author;
            this.pubDate = pubDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public Date getPubDate() {
            return pubDate;
        }

        public void setPubDate(Date pubDate) {
            this.pubDate = pubDate;
        }
    }

    static class MyObject {

        private String stringField;
        private int intField;
        private Date dateField;

        public MyObject(String stringField, int intField, Date dateField) {
            this.stringField = stringField;
            this.intField = intField;
            this.dateField = dateField;
        }

        public String getStringField() {
            return stringField;
        }

        public int getIntField() {
            return intField;
        }

        public Date getDateField() {
            return dateField;
        }

        @Override
        public String toString() {
            return "MyObject{" +
                    "stringField='" + stringField + '\'' +
                    ", intField=" + intField +
                    ", dateField=" + dateField +
                    '}';
        }
    }

    private static class MySerializer implements Serializer<MyObject> {

        @Override
        public Class<MyObject> handledType() {
            return MyObject.class;
        }

        @Override
        public String[] mediaType() {
            return new String[]{"*/xml"};
        }

        @Override
        public MyObject deserialize(SerializedPayload serializedPayload, DeserializationContext context) {
            final String payload = serializedPayload.asString();
            int stringFieldStart = payload.indexOf("<stringField>") + 13;
            int stringFieldEnd = payload.indexOf("</stringField>", stringFieldStart);
            String stringField = payload.substring(stringFieldStart, stringFieldEnd);

            int intFieldStart = payload.indexOf("<intField>", stringFieldEnd) + 10;
            int intFieldEnd = payload.indexOf("</intField>", intFieldStart);
            int intField = Integer.parseInt(payload.substring(intFieldStart, intFieldEnd));

            int dateFieldStart = payload.indexOf("<dateField>", intFieldEnd) + 11;
            int dateFieldEnd = payload.indexOf("</dateField>", dateFieldStart);
            Date dateField = new Date(Long.parseLong(payload.substring(dateFieldStart, dateFieldEnd)));

            return new MyObject(stringField, intField, dateField);
        }

        @Override
        public <C extends Collection<MyObject>> C deserialize(Class<C> collectionType,
                                                              SerializedPayload serializedPayload,
                                                              DeserializationContext ctx) {
            C collection = ctx.getInstance(collectionType);
            final String payload = serializedPayload.asString();

            int nextStart = payload.indexOf("<my>");
            while (nextStart != -1) {
                int nextEnd = payload.indexOf("</my>", nextStart);
                collection.add(deserialize(new TextSerializedPayload(payload.substring(nextStart + 4, nextEnd)), ctx));
                nextStart = payload.indexOf("<my>", nextEnd);
            }

            return collection;
        }

        @Override
        public SerializedPayload serialize(MyObject myObject, SerializationContext context) {
            return new TextSerializedPayload("<my><stringField>" + myObject.getStringField() + "</stringField>"
                    + "<intField>" + myObject.getIntField() + "</intField>"
                    + "<dateField>" + myObject.getDateField().getTime() + "</dateField></my>");
        }

        @Override
        public SerializedPayload serialize(Collection<MyObject> myObjectCollection, SerializationContext context) {
            StringBuilder sb = new StringBuilder("<myList>");
            for (MyObject myObject : myObjectCollection) {
                sb.append(serialize(myObject, context).asString());
            }
            return new TextSerializedPayload(sb.append("</myList>").toString());
        }
    }

    private static class MyJsonSerializer extends JsonObjectSerializer<MyObject> {

        public MyJsonSerializer() {
            super(MyObject.class);
        }

        @Override
        public MyObject readJson(JsonRecordReader reader, DeserializationContext context) {
            return new MyObject(reader.readString("stringField"), reader.readIntPrimitive("intField"),
                    new Date(reader.readLong("dateField")));
        }

        @Override
        public void writeJson(MyObject myObject, JsonRecordWriter writer, SerializationContext context) {
            writer.writeString("stringField", myObject.getStringField());
            writer.writeInt("intField", myObject.getIntField());
            writer.writeDouble("dateField", myObject.getDateField().getTime());
        }
    }
}
