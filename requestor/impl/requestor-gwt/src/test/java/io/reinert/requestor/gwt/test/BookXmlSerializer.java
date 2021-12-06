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
package io.reinert.requestor.gwt.test;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;

/**
 * Custom XML Serializer for {@link Book}.
 *
 * @author Danilo Reinert
 */
public class BookXmlSerializer implements Serializer<Book> {

    public static final String[] CONTENT_TYPE_PATTERNS = new String[]{"*/xml", "*/*+xml", "*/xml+*"};

    public Class<Book> handledType() {
        return Book.class;
    }

    public String[] mediaType() {
        return CONTENT_TYPE_PATTERNS;
    }

    public SerializedPayload serialize(Book book, SerializationContext context) {
        StringBuilder xmlBuilder = buildXml(book);
        return new SerializedPayload(xmlBuilder.toString());
    }

    public SerializedPayload serialize(Collection<Book> c, SerializationContext context) {
        StringBuilder xmlBuilder = new StringBuilder("<books>");
        for (Book book : c) {
            xmlBuilder.append(buildXml(book));
        }
        xmlBuilder.append("</books>");
        return new SerializedPayload(xmlBuilder.toString());
    }

    public Book deserialize(SerializedPayload payload, DeserializationContext context) {
        Document xml;
        try {
            xml = XMLParser.parse(payload.asString());
        } catch (DOMParseException e) {
            throw new UnableToDeserializeException("Could not read response as xml.", e);
        }
        return parseXmlDocumentAsBook(xml)[0];
    }

    public <C extends Collection<Book>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                      DeserializationContext context) {
        C col = context.getInstance(collectionType);

        Document xml;
        try {
            xml = XMLParser.parse(payload.asString());
        } catch (DOMParseException e) {
            throw new UnableToDeserializeException("Could not read response as xml.", e);
        }

        Collections.addAll(col, parseXmlDocumentAsBook(xml));
        return col;
    }

    private StringBuilder buildXml(Book book) {
        StringBuilder xmlBuilder = new StringBuilder("<book>");
        xmlBuilder.append("<id>").append(book.getId()).append("</id>");
        xmlBuilder.append("<title>").append(book.getTitle()).append("</title>");
        xmlBuilder.append("<author>").append(book.getAuthor()).append("</author>");
        xmlBuilder.append("<publicationDate>").append(book.getPublicationDate().getTime()).append("</publicationDate>");
        xmlBuilder.append("</book>");
        return xmlBuilder;
    }

    private Book[] parseXmlDocumentAsBook(Document xml) {
        final NodeList idNodes = xml.getElementsByTagName("id");
        final NodeList titleNodes = xml.getElementsByTagName("title");
        final NodeList authorNodes = xml.getElementsByTagName("author");
        final NodeList publicationDateNodes = xml.getElementsByTagName("publicationDate");

        int length = idNodes.getLength();
        Book[] books = new Book[length];

        for (int i = 0; i < length; i++) {
            String id = ((Text) idNodes.item(i).getFirstChild()).getData();
            String title = ((Text) titleNodes.item(i).getFirstChild()).getData();
            String author = ((Text) authorNodes.item(i).getFirstChild()).getData();
            String publicationDate = ((Text) publicationDateNodes.item(i).getFirstChild()).getData();
            books[i] = new Book(Integer.parseInt(id), title, author, new Date(Long.parseLong(publicationDate)));
        }

        return books;
    }
}
