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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.core.Registration;
import io.reinert.requestor.core.RequestInterceptor;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.ResponseInterceptor;
import io.reinert.requestor.core.SerializedRequestInProcess;
import io.reinert.requestor.core.SerializedResponseInProcess;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.examples.showcase.ui.Interceptors;
import io.reinert.requestor.examples.showcase.util.Page;

public class InterceptorsActivity extends ShowcaseActivity implements Interceptors.Handler {

    private final Interceptors view;
    private final Session session;

    public InterceptorsActivity(String section, Interceptors view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Interceptors");
        Page.setDescription("Transform incoming and outgoing payloads.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    @Override
    public void onRequestInterceptorButtonClick() {
        // Add the interceptor and hold the registration
        final Registration registration = session.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                final String json = request.getSerializedPayload().asText();
                if (json != null) {
                    // add ")]}',\n" to the beginning of JSONs
                    request.setSerializedPayload(new SerializedPayload(")]}',\\n" + json));
                }
                request.proceed();
            }
        });

        // Perform the request
        JavaScriptObject json = getMessageJson("Session is awesome!");
        session.req("http://httpbin.org/post").payload(json).post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setRequestInterceptorText(result);
                    }
                })
                .onLoad(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        registration.cancel(); // cancel interceptor registration
                    }
                });
    }

    @Override
    public void onResponseInterceptorButtonClick() {
        // Add the interceptor and hold the registration
        final Registration registration = session.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                final String json = response.getSerializedPayload().asText();
                if (json != null) {
                    // remove first 6 chars )]}',\n
                    response.setSerializedPayload(new SerializedPayload(json.substring(6)));
                }
                response.proceed();
            }
        });

        // Perform the response
        session.req("http://www.mocky.io/v2/54a3ec74fd145c6c0195e912").get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String response) {
                        view.setResponseInterceptorText(response);
                    }
                })
                .onLoad(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        registration.cancel(); // cancel interceptor registration
                    }
                });
    }

    private JavaScriptObject getMessageJson(String message) {
        JavaScriptObject json = JavaScriptObject.createObject();
        setString(json, "message", message);
        return json;
    }

    private static native void setString(JavaScriptObject jso, String property, String value) /*-{
        jso[property] = value;
    }-*/;
}
