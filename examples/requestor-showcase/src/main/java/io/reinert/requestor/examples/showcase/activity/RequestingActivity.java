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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.Headers;
import io.reinert.requestor.Promise;
import io.reinert.requestor.Session;
import io.reinert.requestor.callback.PayloadCallback;
import io.reinert.requestor.examples.showcase.ui.Requesting;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.examples.showcase.util.Util;

public class RequestingActivity extends ShowcaseActivity implements Requesting.Handler {

    private final Requesting view;
    private final Session session;

    public RequestingActivity(String section, Requesting requesting, Session session) {
        super(section);
        this.view = requesting;
        this.session = session;
    }

    @Override
    public void onGetIpButtonClick() {
        Promise<String> promise = (Promise<String>) session.req("http://httpbin.org/ip").get(String.class);
        promise.success(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setIpText(result);
            }
        });
    }

    @Override
    public void onPostButtonClick() {
        session.req("http://httpbin.org/post").post(String.class).success(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPostText(result);
            }
        });
    }

    @Override
    public void onPutButtonClick() {
        session.req("http://httpbin.org/put").put(String.class).success(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPutText(result);
            }
        });
    }

    @Override
    public void onDeleteButtonClick() {
        session.req("http://httpbin.org/delete").delete(String.class).success(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setDeleteText(result);
            }
        });
    }

    @Override
    public void onHeadButtonClick() {
        session.req("http://httpbin.org/headers").head().success(new PayloadCallback<Headers>() {
            @Override
            public void execute(Headers result) {
                view.setHeadText(Util.formatHeaders(result));
            }
        });
    }

    @Override
    public void onOptionsButtonClick() {
        session.req("http://httpbin.org/get").options(Headers.class).success(new PayloadCallback<Headers>() {
            @Override
            public void execute(Headers result) {
                view.setOptionsText(Util.formatHeaders(result));
            }
        });
    }

    @Override
    public void onPatchButtonClick() {
        session.req("http://httpbin.org/patch").patch(String.class).success(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPatchText(result);
            }
        });
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Requesting");
        Page.setDescription("A quick intro on how to request with Session.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }
}
