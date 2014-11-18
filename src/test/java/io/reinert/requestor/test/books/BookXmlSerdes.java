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

import java.util.Collection;
import java.util.Collections;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.UnableToDeserializeException;

/**
 * @author Danilo Reinert
 */
public class BookXmlSerdes implements Serdes<Book> {

    public static final String[] CONTENT_TYPE_PATTERNS = new String[]{"*/xml", "*/*+xml", "*/xml+*"};

    /**
     * Method for accessing type of Objects this deserializer can handle.
     *
     * @return The class which this deserializer can deserialize
     */
    @Override
    public Class<Book> handledType() {
        return Book.class;
    }

    /**
     * Informs the content type this serializer serializes.
     *
     * @return The content type serialized.
     */
    @Override
    public String[] contentType() {
        return CONTENT_TYPE_PATTERNS;
    }

    /**
     * Serialize T to plain text.
     *
     * @param book    The object to be serialized
     * @param context Context of the serialization
     *
     * @return The object serialized.
     */
    @Override
    public String serialize(Book book, SerializationContext context) {
        StringBuilder xmlBuilder = buildXml(book);
        return xmlBuilder.toString();
    }

    /**
     * Serialize a collection of T to plain text.
     *
     * @param c       The collection of the object to be serialized
     * @param context Context of the serialization
     *
     * @return The object serialized.
     */
    @Override
    public String serialize(Collection<Book> c, SerializationContext context) {
        StringBuilder xmlBuilder = new StringBuilder("<books>");
        for (Book book : c) {
            xmlBuilder.append(buildXml(book));
        }
        xmlBuilder.append("</books>");
        return xmlBuilder.toString();
    }

    /**
     * Deserialize the plain text into an object of type T.
     *
     * @param response Http response body content
     * @param context  Context of deserialization
     *
     * @return The object deserialized
     */
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

    /**
     * Deserialize the plain text into an object of type T.
     *
     * @param collectionType The class of the collection
     * @param response       Http response body content
     * @param context        Context of deserialization
     *
     * @return The object deserialized
     */
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
        xmlBuilder.append("</book>");
        return xmlBuilder;
    }

    private Book[] parseXmlDocumentAsBook(Document xml) {
        final NodeList idNodes = xml.getElementsByTagName("id");
        final NodeList titleNodes = xml.getElementsByTagName("title");
        final NodeList authorNodes = xml.getElementsByTagName("author");

        int length = idNodes.getLength();
        Book[] books = new Book[length];

        for (int i = 0; i < length; i++) {
            String id = ((Text) idNodes.item(i).getFirstChild()).getData();
            String title = ((Text) titleNodes.item(i).getFirstChild()).getData();
            String author = ((Text) authorNodes.item(i).getFirstChild()).getData();
            books[i] = new Book(Integer.valueOf(id), title, author);
        }

        return books;
    }
}
