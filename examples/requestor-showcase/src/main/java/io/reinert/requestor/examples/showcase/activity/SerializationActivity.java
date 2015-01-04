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

import java.util.Collection;
import java.util.Date;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.Requestor;
import io.reinert.requestor.examples.showcase.ui.Serialization;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;

public class SerializationActivity extends AbstractActivity implements Serialization.Handler {

    private final Serialization view;
    private final Requestor requestor;

    private HandlerRegistration deserializerRegistration;
    private HandlerRegistration serializerRegistration;
    private HandlerRegistration serdesRegistration;

    public SerializationActivity(Serialization view, Requestor requestor) {
        this.view = view;
        this.requestor = requestor;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);

        deserializerRegistration = requestor.addDeserializer(new MyDeserializer());
        serializerRegistration = requestor.addSerializer(new MySerializer());
        serdesRegistration = requestor.addSerdes(new MyJsonSerdes());

        Page.setTitle("Serialization");
        Page.setDescription("Exchange any media type with a powerful serialization mechanism.");
        panel.setWidget(view);
    }

    @Override
    public void onStop() {
        view.setHandler(null);
        deserializerRegistration.removeHandler();
        serializerRegistration.removeHandler();
        serdesRegistration.removeHandler();
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

    private static class MyDeserializer implements Deserializer<MyObject> {

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
        public <C extends Collection<MyObject>> C deserialize(Class<C> collectionType, String response, DeserializationContext ctx) {
            C collection = ctx.getInstance(collectionType);

            int nextStart = response.indexOf("<my>");
            while (nextStart != -1) {
                int nextEnd = response.indexOf("</my>", nextStart);
                collection.add(deserialize(response.substring(nextStart + 4, nextEnd), ctx));
                nextStart = response.indexOf("<my>", nextEnd);
            }

            return collection;
        }
    }

    private static class MyJsonSerdes extends JsonObjectSerdes<MyObject> {

        public MyJsonSerdes() {
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
