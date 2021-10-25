/*
 * Copyright 2015 Danilo Reinert
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

import io.reinert.requestor.Registration;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.callback.PayloadCallback;
import io.reinert.requestor.examples.showcase.ui.Serialization;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.json.JsonObjectSerializer;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;

public class SerializationActivity extends ShowcaseActivity implements Serialization.Handler {

    private final Serialization view;
    private final Requestor requestor;

    private Registration xmlSerializerRegistration;
    private Registration jsonSerializerRegistration;

    public SerializationActivity(String section, Serialization view, Requestor requestor) {
        super(section);
        this.view = view;
        this.requestor = requestor;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);

        xmlSerializerRegistration = requestor.register(new MySerializer());
        jsonSerializerRegistration = requestor.register(new MyJsonSerializer());

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
    public void onXmlObjectGet() {
        requestor.req("http://www.mocky.io/v2/54aa8cf807b5f2bc0f21ba08")
                .get(MyObject.class)
                .success(new PayloadCallback<MyObject>() {
                    @Override
                    public void execute(MyObject result) {
                        view.setSingleXmlGetText(result.toString());
                    }
                });
    }

    @Override
    public void onXmlCollectionGet() {
        requestor.req("http://www.mocky.io/v2/54aa8e1407b5f2d20f21ba09")
                .get(List.class, MyObject.class)
                .success(new PayloadCallback<List<MyObject>>() {
                    @Override
                    public void execute(List<MyObject> result) {
                        view.setCollectionXmlGetText(Arrays.toString(result.toArray()));
                    }
                });
    }

    @Override
    public void onXmlObjectPost() {
        requestor.req("http://httpbin.org/post")
                .contentType("application/xml")
                .payload(new MyObject("Lorem", 1900, new Date(1420416000000L)))
                .post(String.class)
                .success(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setSingleXmlPostText(result);
                    }
                });
    }

    @Override
    public void onXmlCollectionPost() {
        requestor.req("http://httpbin.org/post")
                .contentType("application/xml")
                .payload(Arrays.asList(
                        new MyObject("Lorem", 1900, new Date(1420416000000L)),
                        new MyObject("Ipsum", 210, new Date(1420070400000L))))
                .post(String.class)
                .success(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setCollectionXmlPostText(result);
                    }
                });
    }

    @Override
    public void onJsonObjectGet() {
            requestor.req("http://www.mocky.io/v2/54aa93c307b5f2671021ba0c")
                    .get(MyObject.class)
                    .success(new PayloadCallback<MyObject>() {
                        @Override
                        public void execute(MyObject result) {
                            view.setSingleJsonGetText(result.toString());
                        }
                    });
    }

    @Override
    public void onJsonCollectionGet() {
        requestor.req("http://www.mocky.io/v2/54aa937407b5f2601021ba0b")
                .get(List.class, MyObject.class)
                .success(new PayloadCallback<List<MyObject>>() {
                    @Override
                    public void execute(List<MyObject> result) {
                        view.setCollectionJsonGetText(Arrays.toString(result.toArray()));
                    }
                });
    }

    @Override
    public void onJsonObjectPost() {
        requestor.req("http://httpbin.org/post")
                .contentType("application/json")
                .payload(new MyObject("Lorem", 1900, new Date(1420416000000L)))
                .post(String.class)
                .success(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setSingleJsonPostText(result);
                    }
                });
    }

    @Override
    public void onJsonCollectionPost() {
        requestor.req("http://httpbin.org/post")
                .contentType("application/json")
                .payload(Arrays.asList(
                        new MyObject("Lorem", 1900, new Date(1420416000000L)),
                        new MyObject("Ipsum", 210, new Date(1420070400000L))))
                .post(String.class)
                .success(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setCollectionJsonPostText(result);
                    }
                });
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
        public MyObject deserialize(String response, DeserializationContext context) {
            int stringFieldStart = response.indexOf("<stringField>") + 13;
            int stringFieldEnd = response.indexOf("</stringField>", stringFieldStart);
            String stringField = response.substring(stringFieldStart, stringFieldEnd);

            int intFieldStart = response.indexOf("<intField>", stringFieldEnd) + 10;
            int intFieldEnd = response.indexOf("</intField>", intFieldStart);
            int intField = Integer.parseInt(response.substring(intFieldStart, intFieldEnd));

            int dateFieldStart = response.indexOf("<dateField>", intFieldEnd) + 11;
            int dateFieldEnd = response.indexOf("</dateField>", dateFieldStart);
            Date dateField = new Date(Long.parseLong(response.substring(dateFieldStart, dateFieldEnd)));

            return new MyObject(stringField, intField, dateField);
        }

        @Override
        public <C extends Collection<MyObject>> C deserialize(Class<C> collectionType, String response,
                                                              DeserializationContext ctx) {
            C collection = ctx.getInstance(collectionType);

            int nextStart = response.indexOf("<my>");
            while (nextStart != -1) {
                int nextEnd = response.indexOf("</my>", nextStart);
                collection.add(deserialize(response.substring(nextStart + 4, nextEnd), ctx));
                nextStart = response.indexOf("<my>", nextEnd);
            }

            return collection;
        }

        @Override
        public String serialize(MyObject myObject, SerializationContext context) {
            return "<my><stringField>" + myObject.getStringField() + "</stringField>"
                    + "<intField>" + myObject.getIntField() + "</intField>"
                    + "<dateField>" + myObject.getDateField().getTime() + "</dateField></my>";
        }

        @Override
        public String serialize(Collection<MyObject> myObjectCollection, SerializationContext context) {
            StringBuilder sb = new StringBuilder("<myList>");
            for (MyObject myObject : myObjectCollection) {
                sb.append(serialize(myObject, context));
            }
            return sb.append("</myList>").toString();
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
