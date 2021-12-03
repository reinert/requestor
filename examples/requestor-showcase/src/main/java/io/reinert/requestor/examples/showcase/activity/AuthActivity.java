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

import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.auth.BasicAuth;
import io.reinert.requestor.core.auth.BearerAuth;
import io.reinert.requestor.core.auth.DigestAuth;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.examples.showcase.ui.Auth;
import io.reinert.requestor.examples.showcase.util.Page;

public class AuthActivity extends ShowcaseActivity implements Auth.Handler {

    private static class MyAuth implements io.reinert.requestor.core.Auth {

        private final String key;

        private MyAuth(String key) {
            this.key = key;
        }

        @Override
        public void auth(PreparedRequest preparedRequest) {
            preparedRequest.setHeader("Authorization", "MyAuth " + key);

            // Mandatory to have the request actually sent.
            // Call it after putting all necessary auth info in the request.
            preparedRequest.send();
        }
    }

    private final Auth view;
    private final Session session;

    public AuthActivity(String section, Auth view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Authentication");
        Page.setDescription("Check the available methods to authenticate requests.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    @Override
    public void onBasicButtonClick(String user, String password) {
        session.req("https://httpbin.org/basic-auth/" + user + "/" + password)
                .auth(new BasicAuth(user, password))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setBasicText(result);
                    }
                });
    }

    @Override
    public void onBearerButtonClick(String token) {
        session.req("https://httpbin.org/bearer")
                .auth(new BearerAuth(token))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setBearerText(result);
                    }
                });
    }

    @Override
    public void onDigestButtonClick(String user, String password, String qop) {
        session.req("https://requestor-server.herokuapp.com/digest-auth/" + qop + '/' + user + '/' + password)
                .auth(new DigestAuth(user, password, "md5", true))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setDigestText(result);
                    }
                });
    }

    @Override
    public void onCustomButtonClick(String key) {
        session.req("https://httpbin.org/headers")
                .auth(new MyAuth(key))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setCustomText(result);
                    }
                });
    }
}
