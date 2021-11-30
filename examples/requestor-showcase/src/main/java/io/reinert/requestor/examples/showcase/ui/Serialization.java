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
package io.reinert.requestor.examples.showcase.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class Serialization extends Composite {

    public interface Handler {
        void onGwtJacksonGetBooks();
        void onGwtJacksonPostBook();
        void onXmlObjectGet();
        void onXmlCollectionGet();
        void onXmlObjectPost();
        void onXmlCollectionPost();
        void onJsonObjectGet();
        void onJsonCollectionGet();
        void onJsonObjectPost();
        void onJsonCollectionPost();
    }

    interface SerializationUiBinder extends UiBinder<HTMLPanel, Serialization> { }

    private static SerializationUiBinder uiBinder = GWT.create(SerializationUiBinder.class);

    @UiField PreElement overlaysSetup, autobeansSetup, gwtjacksonDependency, gwtjacksonSetup, myXmlSerializer,
            myXmlSerializerReg, gwtjacksonSerializationModule, gwtjacksonGetBooks, gwtjacksonPostBook,
            myXmlDeserializer, myXmlDeserializerReg, myJsonSerializer, testReg, singleXmlGet, collectionXmlGet,
            singleXmlPost, collectionXmlPost, singleJsonGet, collectionJsonGet, singleJsonPost, collectionJsonPost;

    @UiField TextAreaElement gwtjacksonGetBooksTextArea, gwtjacksonPostBookTextArea, singleXmlGetTextArea,
            collectionXmlGetTextArea,
            singleXmlPostTextArea, collectionXmlPostTextArea, singleJsonGetTextArea, collectionJsonGetTextArea,
            singleJsonPostTextArea, collectionJsonPostTextArea;

    private Handler handler;

    public Serialization() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(overlaysSetup, autobeansSetup, gwtjacksonDependency, gwtjacksonSetup,
                gwtjacksonSerializationModule, gwtjacksonGetBooks, gwtjacksonPostBook,
                myXmlSerializer, myXmlSerializerReg,
                myXmlDeserializer, myXmlDeserializerReg, myJsonSerializer,
                testReg, singleXmlGet, collectionXmlGet, singleXmlPost,  collectionXmlPost, singleJsonGet,
                collectionJsonGet, singleJsonPost, collectionJsonPost);
    }

    @UiHandler("gwtjacksonGetBooksButton")
    public void onGwtjacksonGetBooksButtonClick(ClickEvent e) {
        handler.onGwtJacksonGetBooks();
    }

    public void setGwtjacksonGetBooksText(String content) {
        gwtjacksonGetBooksTextArea.setValue(content);
    }

    @UiHandler("gwtjacksonPostBookButton")
    public void onGwtjacksonPostBookButtonClick(ClickEvent e) {
        handler.onGwtJacksonPostBook();
    }

    public void setGwtjacksonPostBookText(String content) {
        gwtjacksonPostBookTextArea.setValue(content);
    }

    @UiHandler("singleXmlGetButton")
    public void onSingleXmlGetButtonClick(ClickEvent e) {
        handler.onXmlObjectGet();
    }

    public void setSingleXmlGetText(String content) {
        singleXmlGetTextArea.setValue(content);
    }

    @UiHandler("collectionXmlGetButton")
    public void onCollectionXmlGetButtonClick(ClickEvent e) {
        handler.onXmlCollectionGet();
    }

    public void setCollectionXmlGetText(String content) {
        collectionXmlGetTextArea.setValue(content);
    }

    @UiHandler("singleXmlPostButton")
    public void onSingleXmlPostButtonClick(ClickEvent e) {
        handler.onXmlObjectPost();
    }

    public void setSingleXmlPostText(String content) {
        singleXmlPostTextArea.setValue(content);
    }

    @UiHandler("collectionXmlPostButton")
    public void onCollectionXmlPostButtonClick(ClickEvent e) {
        handler.onXmlCollectionPost();
    }

    public void setCollectionXmlPostText(String content) {
        collectionXmlPostTextArea.setValue(content);
    }

    @UiHandler("singleJsonGetButton")
    public void onSingleJsonGetButtonClick(ClickEvent e) {
        handler.onJsonObjectGet();
    }

    public void setSingleJsonGetText(String content) {
        singleJsonGetTextArea.setValue(content);
    }

    @UiHandler("collectionJsonGetButton")
    public void onCollectionJsonGetButtonClick(ClickEvent e) {
        handler.onJsonCollectionGet();
    }

    public void setCollectionJsonGetText(String content) {
        collectionJsonGetTextArea.setValue(content);
    }

    @UiHandler("singleJsonPostButton")
    public void onSingleJsonPostButtonClick(ClickEvent e) {
        handler.onJsonObjectPost();
    }

    public void setSingleJsonPostText(String content) {
        singleJsonPostTextArea.setValue(content);
    }

    @UiHandler("collectionJsonPostButton")
    public void onCollectionJsonPostButtonClick(ClickEvent e) {
        handler.onJsonCollectionPost();
    }

    public void setCollectionJsonPostText(String content) {
        collectionJsonPostTextArea.setValue(content);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
