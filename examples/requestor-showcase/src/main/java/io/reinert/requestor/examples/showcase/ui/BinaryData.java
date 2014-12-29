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
package io.reinert.requestor.examples.showcase.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class BinaryData extends Composite {

    public interface Handler {
//        void onBasicButtonClick(String user, String password);
//        void onCustomButtonClick(String key);
        void onSendButtonClick(JavaScriptObject file);
        void onRetrieveButtonClick(String url);
    }

    interface AuthenticationUiBinder extends UiBinder<HTMLPanel, BinaryData> {}

    private static AuthenticationUiBinder uiBinder = GWT.create(AuthenticationUiBinder.class);

    @UiField PreElement send, retrieve;
    @UiField TextAreaElement sendTextArea;
    @UiField InputElement file, imgUrl;
    @UiField Element sendProgress, retrieveProgress;

    private Handler handler;

    public BinaryData() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(send);
    }

    @UiHandler("sendButton")
    public void onSendButtonClick(ClickEvent e) {
        handler.onSendButtonClick(getFile(file, 0));
    }

    public void setSendText(String content) {
        sendTextArea.setInnerText(content);
    }

    public void setSendProgressStatus(double pctComplete) {
        sendProgress.getStyle().setWidth(pctComplete, Style.Unit.PCT);
        sendProgress.setAttribute("aria-valuenow", String.valueOf(pctComplete));
    }

    @UiHandler("retrieveButton")
    public void onRetrieveButtonClick(ClickEvent e) {
        handler.onRetrieveButtonClick(imgUrl.getValue());
    }

    public void setRetrieveProgressStatus(double pctComplete) {
        retrieveProgress.getStyle().setWidth(pctComplete, Style.Unit.PCT);
        retrieveProgress.setAttribute("aria-valuenow", String.valueOf(pctComplete));
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private static native JavaScriptObject getFile(InputElement input, int index) /*-{
        return input.files[index];
     }-*/;
}