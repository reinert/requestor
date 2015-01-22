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
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class Authentication extends Composite {

    public interface Handler {
        void onBasicButtonClick(String user, String password);
        void onDigestButtonClick(String user, String password);
        void onCustomButtonClick(String key);
    }

    interface AuthenticationUiBinder extends UiBinder<HTMLPanel, Authentication> {}

    private static AuthenticationUiBinder uiBinder = GWT.create(AuthenticationUiBinder.class);

    @UiField PreElement basic, digest, myAuth, custom;
    @UiField TextAreaElement basicTextArea, digestTextArea, customTextArea;
    @UiField InputElement user, password, digestUser, digestPassword, key;

    private Handler handler;

    public Authentication() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(basic, myAuth, custom);
    }

    @UiHandler("basicButton")
    public void onBasicButtonClick(ClickEvent e) {
        handler.onBasicButtonClick(user.getValue(), password.getValue());
    }

    @UiHandler("digestButton")
    public void onDigestButtonClick(ClickEvent e) {
        handler.onDigestButtonClick(digestUser.getValue(), digestPassword.getValue());
    }

    @UiHandler("customButton")
    public void onCustomButtonClick(ClickEvent e) {
        handler.onCustomButtonClick(key.getValue());
    }

    public void setBasicText(String content) {
        basicTextArea.setInnerText(content);
    }

    public void setDigestText(String content) {
        digestTextArea.setInnerText(content);
    }

    public void setCustomText(String content) {
        customTextArea.setInnerText(content);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}