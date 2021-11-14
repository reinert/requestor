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

import io.reinert.requestor.core.Headers;
import io.reinert.requestor.core.Request;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestInvoker;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.auth.BasicAuth;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
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
    public void onRequestButtonClick() {
        RequestInvoker reqInvoker = session.req("https://httpbin.org/post")
                .timeout(10000)
                .delay(50)
                .contentType("application/json")
                .accept("text/plain")
                .header("ETag", "33a64df5")
                .auth(new BasicAuth("username", "password"))
                .payload("Hello World!");

        Request<String> request = reqInvoker.post(String.class);

        request.onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String body) {
                view.setResponseText(body);
            }
        }).onFail(new ResponseCallback() {
            @Override
            public void execute(Response res) {
                view.setResponseText("Response was unsuccessful due to: " + res.getStatus());
            }
        }).onAbort(new ExceptionCallback() {
            @Override
            public void execute(RequestException e) {
                view.setResponseText("Request was interrupted due to: " + e.getMessage());
            }
        });
    }

    @Override
    public void onPostButtonClick() {
        session.req("https://httpbin.org/post").post(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPostText(result);
            }
        });
    }

    @Override
    public void onPutButtonClick() {
        session.req("https://httpbin.org/put").put(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPutText(result);
            }
        });
    }

    @Override
    public void onPatchButtonClick() {
        session.req("https://httpbin.org/patch").patch(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setPatchText(result);
            }
        });
    }

    @Override
    public void onDeleteButtonClick() {
        session.req("https://httpbin.org/delete").delete(String.class).onSuccess(new PayloadCallback<String>() {
            @Override
            public void execute(String result) {
                view.setDeleteText(result);
            }
        });
    }

    @Override
    public void onHeadButtonClick() {
        session.req("https://httpbin.org/headers").head().onSuccess(new PayloadCallback<Headers>() {
            @Override
            public void execute(Headers result) {
                view.setHeadText(Util.formatHeaders(result));
            }
        });
    }

    @Override
    public void onOptionsButtonClick() {
        session.req("https://httpbin.org/anything").options(Headers.class).onSuccess(new PayloadCallback<Headers>() {
            @Override
            public void execute(Headers result) {
                view.setOptionsText(Util.formatHeaders(result));
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
