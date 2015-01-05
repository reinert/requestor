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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class Serialization extends Composite {

    public interface Handler {
    }

    interface SerializationUiBinder extends UiBinder<HTMLPanel, Serialization> {}

    private static SerializationUiBinder uiBinder = GWT.create(SerializationUiBinder.class);

    @UiField PreElement overlaysSetup, autobeansSetup, gwtjacksonSetup, mySerializer, myDeserializer;
//    @UiField TextAreaElement requestFilterTextArea, responseFilterTextArea, responseFilterTextArea2;

    private Handler handler;

    public Serialization() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(overlaysSetup, autobeansSetup, gwtjacksonSetup, mySerializer, myDeserializer);
    }

//    @UiHandler("requestFilterButton")
//    public void onRequestFilterButtonClick(ClickEvent e) {
//        handler.onRequestFilterButtonClick();
//    }
//
//    @UiHandler("responseFilterButton")
//    public void onResponseFilterButtonClick(ClickEvent e) {
//        handler.onResponseFilterButtonClick();
//    }
//
//    public void setRequestFilterText(String content) {
//        requestFilterTextArea.setInnerText(content);
//    }
//
//    public void setResponseFilterText(String headers, String content) {
//        responseFilterTextArea.setInnerText(content);
//        responseFilterTextArea2.setInnerText(headers);
//    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}