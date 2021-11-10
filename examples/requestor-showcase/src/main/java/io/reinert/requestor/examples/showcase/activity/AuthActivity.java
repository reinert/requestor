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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.core.PreparedRequest;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.auth.BasicAuth;
import io.reinert.requestor.core.auth.DigestAuth;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.examples.showcase.ui.Auth;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.oauth2.OAuth2ByHeader;
import io.reinert.requestor.oauth2.OAuth2ByQueryParam;

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
    public void onBasicButtonClick(String user, String password) {
        session.req("http://httpbin.org/basic-auth/" + user + "/" + password)
                .auth(new BasicAuth(user, password))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setBasicText(result);
                    }
                })
                .onFail(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        GWT.log("Authentication failed.");
                    }
                });
    }

    @Override
    public void onDigestButtonClick(String user, String password, String qop) {
        session.req("http://httpbin.org/digest-auth/" + qop + '/' + user + '/' + password)
                .auth(new DigestAuth(user, password, true))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setDigestText(result);
                    }
                })
                .onFail(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        GWT.log("Authentication failed.");
                    }
                });
    }

    @Override
    public void onCustomButtonClick(String key) {
        session.req("http://httpbin.org/headers")
                .auth(new MyAuth(key))
                .get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setCustomText(result);
                    }
                });
    }

    @Override
    public void onGoogleButtonClick() {
        final String profilePictureEndpoint = "https://www.googleapis.com/plus/v1/people/me";
        final String authUrl = "https://accounts.google.com/o/oauth2/auth";
        final String appClientId = "60734886159-99bmoevf41sott6sa2cijltc85orhc18.apps.googleusercontent.com";
        final String scope = "https://www.googleapis.com/auth/plus.login";
        session.req(profilePictureEndpoint)
                .auth(new OAuth2ByHeader(authUrl, appClientId, scope))
                .get(JavaScriptObject.class)
                .onSuccess(new PayloadCallback<JavaScriptObject>() {
                    @Override
                    public void execute(JavaScriptObject result) {
                        final JavaScriptObject image = getObject(result, "image");
                        final String imageUrl = getString(image, "url");
                        view.addImage(imageUrl);
                    }
                });
    }

    @Override
    public void onFacebookButtonClick() {
        final String profilePictureEndpoint = "https://graph.facebook.com/v2.3/me/picture?redirect=false";
        final String authUrl = "https://www.facebook.com/dialog/oauth";
        final String appClientId = "366496696889929";
        final String scope = "public_profile";
        session.req(profilePictureEndpoint)
                .auth(new OAuth2ByQueryParam(authUrl, appClientId, scope))
                .get(JavaScriptObject.class)
                .onSuccess(new PayloadCallback<JavaScriptObject>() {
                    @Override
                    public void execute(JavaScriptObject result) {
                        final JavaScriptObject data = getObject(result, "data");
                        final String imageUrl = getString(data, "url");
                        view.addImage(imageUrl);
                    }
                });
    }

    @Override
    public void onWindowsButtonClick() {
        final String profilePictureEndpoint = "https://apis.live.net/v5.0/me";
        final String authUrl = "https://login.live.com/oauth20_authorize.srf";
        final String appClientId = "000000004015498F";
        final String scope = "wl.basic";
        session.req(profilePictureEndpoint)
                .auth(new OAuth2ByQueryParam(authUrl, appClientId, scope))
                .get(JavaScriptObject.class)
                .onSuccess(new PayloadCallback<JavaScriptObject>() {
                    @Override
                    public void execute(JavaScriptObject result) {
                        final String userId = getObject(result, "id");
                        final String imageUrl = "https://apis.live.net/v5.0/" + userId + "/picture";
                        view.addImage(imageUrl);
                    }
                });
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Authentication & Authorization");
        Page.setDescription("See how to authenticate/authorize requests in practice.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(JavaScriptObject jso, String property) {
        return (T) getObjectNative(jso, property);
    }

    private static native Object getObjectNative(JavaScriptObject jso, String property) /*-{
        return jso[property];
    }-*/;

    private static native String getString(JavaScriptObject jso, String property) /*-{
        return jso[property];
    }-*/;
}
