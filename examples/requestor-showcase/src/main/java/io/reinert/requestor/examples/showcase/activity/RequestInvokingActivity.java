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
package io.reinert.requestor.examples.showcase.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.examples.showcase.Showcase;
import io.reinert.requestor.examples.showcase.ui.RequestInvoking;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.examples.showcase.util.Util;

public class RequestInvokingActivity extends ShowcaseActivity implements RequestInvoking.Handler {

    private final RequestInvoking view;
    private final Session session;

    public RequestInvokingActivity(String section, RequestInvoking view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Request Invoking");
        Page.setDescription("Meet the different ways of invoking a request.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    @Override
    public void onPostButtonClick() {
        Uri uri = Showcase.CLIENT_FACTORY.getPostUri();
        session.req(uri).post(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPostText(result);
            }
        });
    }

    @Override
    public void onPutButtonClick() {
        Uri uri = Showcase.CLIENT_FACTORY.getUriBuilder().segment("put").build();
        session.req(uri).put(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPutText(result);
            }
        });
    }

    @Override
    public void onPatchButtonClick() {
        Uri uri = Showcase.CLIENT_FACTORY.getUriBuilder().segment("patch").build();
        session.req(uri).patch(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPatchText(result);
            }
        });
    }

    @Override
    public void onDeleteButtonClick() {
        Uri uri = Showcase.CLIENT_FACTORY.getUriBuilder().segment("delete").build();
        session.req(uri).delete(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setDeleteText(result);
            }
        });
    }

    @Override
    public void onHeadButtonClick() {
        Uri uri = Showcase.CLIENT_FACTORY.getUriBuilder().segment("headers").build();
        session.req(uri).head().onSuccess(new PayloadCallback<Headers>() {
            @Override
            public void execute(Headers result) {
                view.setHeadText(Util.formatHeaders(result));
            }
        });
    }

    @Override
    public void onOptionsButtonClick() {
        Uri uri = Showcase.CLIENT_FACTORY.getUriBuilder().segment("headers").build();
        session.req(uri).options().onSuccess(new PayloadCallback<Headers>() {
            @Override
            public void execute(Headers headers) {
                view.setOptionsText(Util.formatHeaders(headers));
            }
        });
    }
}
