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
package io.reinert.requestor.examples.showcase.activity;

import java.util.List;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.FormData;
import io.reinert.requestor.Response;
import io.reinert.requestor.Session;
import io.reinert.requestor.callback.PayloadCallback;
import io.reinert.requestor.examples.showcase.ui.Form;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.gwt.JsFormData;

public class FormActivity extends ShowcaseActivity implements Form.Handler {

    private final Form view;
    private final Session session;

    public FormActivity(String section, Form form, Session session) {
        super(section);
        this.view = form;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Form Data");
        Page.setDescription("Submit AJAX Forms easily.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    @Override
    public void onWrappingPostButtonClick(FormElement formElement) {
        FormData formData = JsFormData.wrap(formElement);

        session.req("http://httpbin.org/post")
                .payload(formData)
                .post(Response.class) // retrieve the raw response
                .success(new PayloadCallback<Response>() {
                    @Override
                    public void execute(Response response) {
                        // the payload is parsed as string by default
                        // to change it, we can set the desired responseType in the RequestBuilder
                        final String payload = response.getSerializedPayload().getString();
                        view.setWrappingText(payload);
                    }
                });
    }

    @Override
    public void onBuildingPostButtonClick(String custname, String custtel, String custemail, String size,
                                          List<String> toppings, String time, String comments) {
        FormData formData = FormData.builder()
                .append("custname", custname)
                .append("custtel", custtel)
                .append("custemail", custemail)
                .append("size", size)
                .append("topping", toppings)
                .append("time", time)
                .append("comments", comments)
                .build();

        session.req("http://httpbin.org/post")
                .payload(formData)
                .post(Response.class) // retrieve the raw response
                .success(new PayloadCallback<Response>() {
                    @Override
                    public void execute(Response response) {
                        // the payload is parsed as string by default
                        // to change it, we can set the desired responseType in the RequestBuilder
                        final String payload = response.getSerializedPayload().getString();
                        view.setBuildingText(payload);
                    }
                });
    }

    @Override
    public void onBuildingUrlEncodedPostButtonClick(String custname, String custtel, String custemail, String size,
                                                    List<String> toppings, String time, String comments) {
        FormData formData = FormData.builder()
                .append("custname", custname)
                .append("custtel", custtel)
                .append("custemail", custemail)
                .append("size", size)
                .append("topping", toppings)
                .append("time", time)
                .append("comments", comments)
                .build();

        session.req("http://httpbin.org/post")
                .payload(formData)
                .contentType("application/x-www-form-urlencoded")
                .post(Response.class) // retrieve the raw response
                .success(new PayloadCallback<Response>() {
                    @Override
                    public void execute(Response response) {
                        // the payload is parsed as string by default
                        // to change it, we can set the desired responseType in the RequestBuilder
                        final String payload = response.getSerializedPayload().getString();
                        view.setBuildingUrlEncodedText(payload);
                    }
                });
    }
}
