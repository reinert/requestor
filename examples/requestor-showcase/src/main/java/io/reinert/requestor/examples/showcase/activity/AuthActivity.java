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

import io.reinert.gdeferred.DoneCallback;
import io.reinert.gdeferred.FailCallback;
import io.reinert.requestor.PreparedRequest;
import io.reinert.requestor.RequestDispatcher;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.auth.BasicAuth;
import io.reinert.requestor.auth.DigestAuth;
import io.reinert.requestor.auth.OAuth2ByHeader;
import io.reinert.requestor.auth.OAuth2ByQueryParam;
import io.reinert.requestor.examples.showcase.ui.Auth;
import io.reinert.requestor.examples.showcase.util.Page;

public class AuthActivity extends ShowcaseActivity implements Auth.Handler {

    private static class MyAuth implements io.reinert.requestor.auth.Auth {

        private final String key;

        private MyAuth(String key) {
            this.key = key;
        }

        @Override
        public void auth(PreparedRequest preparedRequest, RequestDispatcher dispatcher) {
            preparedRequest.setHeader("Authorization", "MyAuth " + key);

            // Mandatory to have the request actually sent.
            // Call it after putting all necessary auth info in the request.
            preparedRequest.send();
        }
    }

    private final Auth view;
    private final Requestor requestor;

    public AuthActivity(String section, Auth view, Requestor requestor) {
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
    public void onGoogleButtonClick() {
        final String profilePictureEndpoint = "https://www.googleapis.com/plus/v1/people/me";
        final String authUrl = "https://accounts.google.com/o/oauth2/auth";
        final String appClientId = "60734886159-99bmoevf41sott6sa2cijltc85orhc18.apps.googleusercontent.com";
        final String scope = "https://www.googleapis.com/auth/plus.login";
        requestor.req(profilePictureEndpoint)
                .auth(new OAuth2ByHeader(authUrl, appClientId, scope))
                .get(JavaScriptObject.class)
                .done(new DoneCallback<JavaScriptObject>() {
                    @Override
                    public void onDone(JavaScriptObject result) {
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
        requestor.req(profilePictureEndpoint)
                .auth(new OAuth2ByQueryParam(authUrl, appClientId, scope))
                .get(JavaScriptObject.class)
                .done(new DoneCallback<JavaScriptObject>() {
                    @Override
                    public void onDone(JavaScriptObject result) {
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
        requestor.req(profilePictureEndpoint)
                .auth(new OAuth2ByQueryParam(authUrl, appClientId, scope))
                .get(JavaScriptObject.class)
                .done(new DoneCallback<JavaScriptObject>() {
                    @Override
                    public void onDone(JavaScriptObject result) {
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
