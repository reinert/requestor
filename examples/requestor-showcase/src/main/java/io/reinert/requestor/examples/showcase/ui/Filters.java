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

public class Filters extends Composite {

    public interface Handler {
        void onRequestFilterButtonClick();
        void onResponseFilterButtonClick();
    }

    interface FiltersUiBinder extends UiBinder<HTMLPanel, Filters> { }

    private static FiltersUiBinder uiBinder = GWT.create(FiltersUiBinder.class);

    @UiField PreElement requestFilter, requestFilterReq, responseFilter, responseFilterReq;
    @UiField TextAreaElement requestFilterTextArea, responseFilterBody, responseFilterHeaders;

    private Handler handler;

    public Filters() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(requestFilter, requestFilterReq, responseFilter, responseFilterReq);
    }

    @UiHandler("requestFilterButton")
    public void onRequestFilterButtonClick(ClickEvent e) {
        handler.onRequestFilterButtonClick();
    }

    @UiHandler("responseFilterButton")
    public void onResponseFilterButtonClick(ClickEvent e) {
        handler.onResponseFilterButtonClick();
    }

    public void setRequestFilterText(String content) {
        requestFilterTextArea.setInnerText(content);
    }

    public void setResponseBody(String content) {
        responseFilterBody.setInnerText(content);
    }

    public void setResponseHeaders(String content) {
        responseFilterHeaders.setInnerText(content);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
