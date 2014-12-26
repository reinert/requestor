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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FormElement;
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

public class Form extends Composite {

    public interface Handler {
        void onWrappingPostButtonClick(FormElement formElement);
        void onBuildingPostButtonClick(String custname, String custtel, String custemail, String size,
                                       List<String> toppings, String time, String comments);
    }

    interface FormUiBinder extends UiBinder<HTMLPanel, Form> {}

    private static FormUiBinder uiBinder = GWT.create(FormUiBinder.class);

    @UiField FormElement form;
    @UiField TextAreaElement comments, wrappingTextArea, buildingTextArea;
    @UiField PreElement wrapping, building;
    @UiField InputElement custname, custtel, custemail, sizeSmall, sizeMedium, sizeLarge, topBacon, topCheese, topOnion,
            topMushroom, time;


    private Handler handler;

    public Form() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(wrapping, building);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @UiHandler("wrappingPostButton")
    public void onWrappingPostButtonClick(ClickEvent e) {
        handler.onWrappingPostButtonClick(form);
    }

    @UiHandler("buildingPostButton")
    public void onBuildingPostButtonClick(ClickEvent e) {
        String custname = this.custname.getValue();
        String custtel = this.custtel.getValue();
        String custemail = this.custemail.getValue();

        String size = sizeSmall.isChecked() ? sizeSmall.getValue() :
                sizeMedium.isChecked() ? sizeMedium.getValue() :
                sizeLarge.isChecked() ? sizeLarge.getValue() : "";

        ArrayList<String> toppings = new ArrayList<String>();
        if (topBacon.isChecked()) toppings.add(topBacon.getValue());
        if (topCheese.isChecked()) toppings.add(topCheese.getValue());
        if (topOnion.isChecked()) toppings.add(topOnion.getValue());
        if (topMushroom.isChecked()) toppings.add(topMushroom.getValue());

        String time = this.time.getValue();
        String comments = this.comments.getValue();

        handler.onBuildingPostButtonClick(custname, custtel, custemail, size, toppings, time, comments);
    }

    public void setWrappingText(String content) {
        wrappingTextArea.setValue(content);
    }

    public void setBuildingText(String content) {
        buildingTextArea.setValue(content);
    }
}