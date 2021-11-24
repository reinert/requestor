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
import com.google.gwt.user.client.ui.Image;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class Auth extends Composite {

    public interface Handler {
        void onBasicButtonClick(String user, String password);
        void onBearerButtonClick(String token);
        void onDigestButtonClick(String user, String password, String qop);
        void onCustomButtonClick(String key);
        void onGoogleButtonClick();
        void onFacebookButtonClick();
        void onWindowsButtonClick();
    }

    interface AuthenticationUiBinder extends UiBinder<HTMLPanel, Auth> { }

    private static AuthenticationUiBinder uiBinder = GWT.create(AuthenticationUiBinder.class);

    @UiField PreElement basic, bearer, digest, digestImport, digestProvider, myAuth, custom, oauth2;
    @UiField TextAreaElement basicTextArea, bearerTextArea, digestTextArea, customTextArea;
    @UiField InputElement basicUser, basicPassword, bearerToken, digestUser, digestPassword, noQop, authQop, authIntQop,
            key;
    @UiField HTMLPanel faces;

    private Handler handler;

    public Auth() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(basic, bearer, digest, digestImport, digestProvider, myAuth, custom, oauth2);
    }

    @UiHandler("basicButton")
    public void onBasicButtonClick(ClickEvent e) {
        handler.onBasicButtonClick(basicUser.getValue(), basicPassword.getValue());
    }

    @UiHandler("bearerButton")
    public void onBearerButtonClick(ClickEvent e) {
        handler.onBearerButtonClick(bearerToken.getValue());
    }

    @UiHandler("digestButton")
    public void onDigestButtonClick(ClickEvent e) {
        final String qop = noQop.isChecked() ? noQop.getValue() :
                authQop.isChecked() ? authQop.getValue() :
                authIntQop.isChecked() ? authIntQop.getValue() : "";
        handler.onDigestButtonClick(digestUser.getValue(), digestPassword.getValue(), qop);
    }

    @UiHandler("customButton")
    public void onCustomButtonClick(ClickEvent e) {
        handler.onCustomButtonClick(key.getValue());
    }

    @UiHandler("googleButton")
    public void onGoogleButtonClick(ClickEvent e) {
        handler.onGoogleButtonClick();
    }

    @UiHandler("facebookButton")
    public void onFacebookButtonClick(ClickEvent e) {
        handler.onFacebookButtonClick();
    }

    @UiHandler("windowsButton")
    public void onWindowsButtonClick(ClickEvent e) {
        handler.onWindowsButtonClick();
    }

    public void setBasicText(String content) {
        basicTextArea.setInnerText(content);
    }

    public void setBearerText(String content) {
        bearerTextArea.setInnerText(content);
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

    public void addImage(String imageUrl) {
        final Image img = new Image(imageUrl);
        img.setStyleName("img-circle");
        img.getElement().getStyle().setMarginRight(4, Style.Unit.PX);
        faces.add(img);
    }
}
