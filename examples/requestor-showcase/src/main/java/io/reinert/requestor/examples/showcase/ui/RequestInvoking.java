/*
 * Copyright 2015-2021 Danilo Reinert
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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class RequestInvoking extends Composite {

    public interface Handler {
        void onPostButtonClick();
        void onPutButtonClick();
        void onDeleteButtonClick();
        void onHeadButtonClick();
        void onOptionsButtonClick();
        void onPatchButtonClick();
    }

    interface SendingRequestsUiBinder extends UiBinder<HTMLPanel, RequestInvoking> { }

    private static SendingRequestsUiBinder uiBinder = GWT.create(SendingRequestsUiBinder.class);

    private Handler handler;

    @UiField Element hsmNoArg, hsmOneArg, hsmTwoArgs, postSample, putSample, deleteSample, headSample, optionsSample,
            patchSample;

    @UiField TextAreaElement postTextArea, putTextArea, deleteTextArea, headTextArea, optionsTextArea, patchTextArea;

    public RequestInvoking() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(hsmNoArg, hsmOneArg, hsmTwoArgs, postSample, putSample, deleteSample, headSample,
                optionsSample, patchSample);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @UiHandler("postButton")
    public void onPostButtonClick(ClickEvent event) {
        handler.onPostButtonClick();
    }

    @UiHandler("putButton")
    public void onPutButtonClick(ClickEvent event) {
        handler.onPutButtonClick();
    }

    @UiHandler("deleteButton")
    public void onDeleteButtonClick(ClickEvent event) {
        handler.onDeleteButtonClick();
    }

    @UiHandler("headButton")
    public void onHeadButtonClick(ClickEvent event) {
        handler.onHeadButtonClick();
    }

    @UiHandler("optionsButton")
    public void onOptionsButtonClick(ClickEvent event) {
        handler.onOptionsButtonClick();
    }

    @UiHandler("patchButton")
    public void onPatchButtonClick(ClickEvent event) {
        handler.onPatchButtonClick();
    }

    public void setPostText(String content) {
        postTextArea.setValue(content);
    }

    public void setPutText(String content) {
        putTextArea.setValue(content);
    }

    public void setDeleteText(String content) {
        deleteTextArea.setValue(content);
    }

    public void setHeadText(String content) {
        headTextArea.setValue(content);
    }

    public void setOptionsText(String content) {
        optionsTextArea.setValue(content);
    }

    public void setPatchText(String content) {
        patchTextArea.setValue(content);
    }
}
