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
package io.reinert.requestor.test;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * Custom XML Serializer for {@link Book}.
 *
 * @author Danilo Reinert
 */
public class BookXmlSerializer implements Serializer<Book> {

    public static final String[] CONTENT_TYPE_PATTERNS = new String[]{"*/xml", "*/*+xml", "*/xml+*"};

    @Override
    public Class<Book> handledType() {
        return Book.class;
    }

    @Override
    public String[] mediaType() {
        return CONTENT_TYPE_PATTERNS;
    }

    @Override
    public String serialize(Book book, SerializationContext context) {
        StringBuilder xmlBuilder = buildXml(book);
        return xmlBuilder.toString();
    }

    @Override
    public String serialize(Collection<Book> c, SerializationContext context) {
        StringBuilder xmlBuilder = new StringBuilder("<books>");
        for (Book book : c) {
            xmlBuilder.append(buildXml(book));
        }
        xmlBuilder.append("</books>");
        return xmlBuilder.toString();
    }

    @Override
    public Book deserialize(String response, DeserializationContext context) {
        Document xml;
        try {
            xml = XMLParser.parse(response);
        } catch (DOMParseException e) {
            throw new UnableToDeserializeException("Could not read response as xml.", e);
        }
        return parseXmlDocumentAsBook(xml)[0];
    }

    @Override
    public <C extends Collection<Book>> C deserialize(Class<C> collectionType, String response,
                                                      DeserializationContext context) {
        C col = context.getInstance(collectionType);

        Document xml;
        try {
            xml = XMLParser.parse(response);
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
            books[i] = new Book(Integer.valueOf(id), title, author, new Date(Long.valueOf(publicationDate)));
        }

        return books;
    }
}
