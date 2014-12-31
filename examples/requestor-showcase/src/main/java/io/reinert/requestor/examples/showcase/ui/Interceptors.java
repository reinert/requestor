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
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class Interceptors extends Composite {

    public interface Handler {
        void onRequestInterceptorButtonClick();
        void onResponseInterceptorButtonClick();
    }

    interface InterceptorsUiBinder extends UiBinder<HTMLPanel, Interceptors> {}

    private static InterceptorsUiBinder uiBinder = GWT.create(InterceptorsUiBinder.class);

    @UiField PreElement requestInterceptor, requestInterceptorReq, responseInterceptor, responseInterceptorReq;
    @UiField TextAreaElement requestInterceptorTextArea, responseInterceptorTextArea;

    private Handler handler;

    public Interceptors() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(requestInterceptor, requestInterceptorReq, responseInterceptor,
                responseInterceptorReq);
    }

    @UiHandler("requestInterceptorButton")
    public void onRequestInterceptorButtonClick(ClickEvent e) {
        handler.onRequestInterceptorButtonClick();
    }

    @UiHandler("responseInterceptorButton")
    public void onResponseInterceptorButtonClick(ClickEvent e) {
        handler.onResponseInterceptorButtonClick();
    }

    public void setRequestInterceptorText(String content) {
        requestInterceptorTextArea.setInnerText(content);
    }
    
    public void setResponseInterceptorText(String content) {
        responseInterceptorTextArea.setInnerText(content);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}