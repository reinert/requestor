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
import io.reinert.requestor.Requestor;
import io.reinert.requestor.examples.showcase.ui.Requesting;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.examples.showcase.util.Util;
import io.reinert.requestor.impl.gdeferred.RequestDoneCallback;

public class RequestingActivity extends ShowcaseActivity implements Requesting.Handler {

    private final Requesting view;
    private final Requestor requestor;

    public RequestingActivity(String section, Requesting requesting, Requestor requestor) {
        super(section);
        this.view = requesting;
        this.requestor = requestor;
    }

    @Override
    public void onGetIpButtonClick() {
        Promise<String> promise = (Promise<String>) requestor.req("http://httpbin.org/ip").get(String.class);
        promise.done(new RequestDoneCallback<String>() {
            @Override
            public void onDone(String result) {
                view.setIpText(result);
            }
        });
    }

    @Override
    public void onPostButtonClick() {
        requestor.req("http://httpbin.org/post").post(String.class).done(new RequestDoneCallback<String>() {
            @Override
            public void onDone(String result) {
                view.setPostText(result);
            }
        });
    }

    @Override
    public void onPutButtonClick() {
        requestor.req("http://httpbin.org/put").put(String.class).done(new RequestDoneCallback<String>() {
            @Override
            public void onDone(String result) {
                view.setPutText(result);
            }
        });
    }

    @Override
    public void onDeleteButtonClick() {
        requestor.req("http://httpbin.org/delete").delete(String.class).done(new RequestDoneCallback<String>() {
            @Override
            public void onDone(String result) {
                view.setDeleteText(result);
            }
        });
    }

    @Override
    public void onHeadButtonClick() {
        requestor.req("http://httpbin.org/headers").head().done(new RequestDoneCallback<Headers>() {
            @Override
            public void onDone(Headers result) {
                view.setHeadText(Util.formatHeaders(result));
            }
        });
    }

    @Override
    public void onOptionsButtonClick() {
        requestor.req("http://httpbin.org/get").options(Headers.class).done(new RequestDoneCallback<Headers>() {
            @Override
            public void onDone(Headers result) {
                view.setOptionsText(Util.formatHeaders(result));
            }
        });
    }

    @Override
    public void onPatchButtonClick() {
        requestor.req("http://httpbin.org/patch").patch(String.class).done(new RequestDoneCallback<String>() {
            @Override
            public void onDone(String result) {
                view.setPatchText(result);
            }
        });
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Requesting");
        Page.setDescription("A quick intro on how to request with Requestor.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }
}
