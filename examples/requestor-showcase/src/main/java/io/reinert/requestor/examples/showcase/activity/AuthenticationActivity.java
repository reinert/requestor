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
package io.reinert.requestor.examples.showcase.activity;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.FailCallback;
import io.reinert.requestor.RequestOrder;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.auth.BasicAuth;
import io.reinert.requestor.auth.DigestAuth;
import io.reinert.requestor.examples.showcase.ui.Authentication;
import io.reinert.requestor.examples.showcase.util.Page;

public class AuthenticationActivity extends ShowcaseActivity implements Authentication.Handler {

    private static class MyAuth implements io.reinert.requestor.auth.Authentication {

        private final String key;

        private MyAuth(String key) {
            this.key = key;
        }

        @Override
        public void authenticate(RequestOrder requestOrder) {
            requestOrder.setHeader("Authentication", "MyAuth " + key);

            // Mandatory to have the request actually sent.
            // Call it after putting all necessary auth info in the request.
            requestOrder.send();
        }
    }

    private final Authentication view;
    private final Requestor requestor;

    public AuthenticationActivity(String section, Authentication view, Requestor requestor) {
        super(section);
        this.view = view;
        this.requestor = requestor;
    }

    @Override
    public void onBasicButtonClick(String user, String password) {
        requestor.req("http://httpbin.org/basic-auth/" + user + "/" + password)
                .auth(new BasicAuth(user, password))
                .get(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setBasicText(result);
                    }
                })
                .fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        GWT.log("Authentication failed.", result);
                    }
                });
    }

    @Override
    public void onDigestButtonClick(String user, String password, String qop) {
        requestor.req("http://httpbin.org/digest-auth/" + qop + '/' + user + '/' + password)
                .auth(new DigestAuth(user, password, true))
                .get(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setDigestText(result);
                    }
                })
                .fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        GWT.log("Authentication failed.", result);
                    }
                });
    }

    @Override
    public void onCustomButtonClick(String key) {
        requestor.req("http://httpbin.org/headers")
                .auth(new MyAuth(key))
                .get(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setCustomText(result);
                    }
                });
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Authentication");
        Page.setDescription("See how to authenticate requests in practice.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }
}
