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
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.requestor.RequestInterceptor;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.Response;
import io.reinert.requestor.ResponseInterceptor;
import io.reinert.requestor.SerializedRequestInProcess;
import io.reinert.requestor.SerializedResponseInProcess;
import io.reinert.requestor.callback.PayloadCallback;
import io.reinert.requestor.callback.ResponseCallback;
import io.reinert.requestor.examples.showcase.ui.Interceptors;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.payload.SerializedPayload;

public class InterceptorsActivity extends ShowcaseActivity implements Interceptors.Handler {

    private final Interceptors view;
    private final Requestor requestor;

    public InterceptorsActivity(String section, Interceptors view, Requestor requestor) {
        super(section);
        this.view = view;
        this.requestor = requestor;
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
        final HandlerRegistration registration = requestor.register(new RequestInterceptor() {
            @Override
            public void intercept(SerializedRequestInProcess request) {
                final String json = request.getSerializedPayload().getString();
                if (json != null) {
                    // add ")]}',\n" to the beginning of JSONs
                    request.setSerializedPayload(SerializedPayload.fromText(")]}',\\n" + json));
                }
                request.proceed();
            }
        });

        // Perform the request
        JavaScriptObject json = getMessageJson("Requestor is awesome!");
        requestor.req("http://httpbin.org/post").payload(json).post(String.class)
                .success(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setRequestInterceptorText(result);
                    }
                })
                .load(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        registration.removeHandler(); // cancel interceptor registration
                    }
                });
    }

    @Override
    public void onResponseInterceptorButtonClick() {
        // Add the interceptor and hold the registration
        final HandlerRegistration registration = requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(SerializedResponseInProcess response) {
                final String json = response.getSerializedPayload().getString();
                if (json != null) {
                    // remove first 6 chars )]}',\n
                    response.setSerializedPayload(SerializedPayload.fromText(json.substring(6)));
                }
                response.proceed();
            }
        });

        // Perform the response
        requestor.req("http://www.mocky.io/v2/54a3ec74fd145c6c0195e912").get(String.class)
                .success(new PayloadCallback<String>() {
                    @Override
                    public void execute(String response) {
                        view.setResponseInterceptorText(response);
                    }
                })
                .load(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        registration.removeHandler(); // cancel interceptor registration
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
