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

import io.reinert.gdeferred.AlwaysCallback;
import io.reinert.gdeferred.Promise;
import io.reinert.requestor.Payload;
import io.reinert.requestor.Request;
import io.reinert.requestor.RequestInterceptor;
import io.reinert.requestor.RequestInterceptorContext;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.ResponseInterceptor;
import io.reinert.requestor.ResponseInterceptorContext;
import io.reinert.requestor.examples.showcase.ui.Interceptors;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.impl.gdeferred.DoneCallback;

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
            public void intercept(RequestInterceptorContext context) {
                final String json = context.getPayload().isString();
                if (json != null) {
                    context.setPayload(Payload.fromText(")]}',\\n" + json));  // add )]}',\n to the beginning of JSONs
                }
            }
        });

        // Perform the request
        JavaScriptObject json = getMessageJson("Requestor is awesome!");
        requestor.req("http://httpbin.org/post").payload(json).post(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setRequestInterceptorText(result);
                    }
                })
                .always(new AlwaysCallback<String, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, String resolved, Throwable rejected) {
                        registration.removeHandler(); // cancel interceptor registration
                    }
                });
    }

    @Override
    public void onResponseInterceptorButtonClick() {
        // Add the interceptor and hold the registration
        final HandlerRegistration registration = requestor.register(new ResponseInterceptor() {
            @Override
            public void intercept(Request request, ResponseInterceptorContext context) {
                final String json = context.getPayload().isString();
                if (json != null) {
                    context.setPayload(Payload.fromText(json.substring(6))); // remove first 6 chars )]}',\n
                }
            }
        });

        // Perform the response
        requestor.req("http://www.mocky.io/v2/54a3ec74fd145c6c0195e912").get(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String response) {
                        view.setResponseInterceptorText(response);
                    }
                })
                .always(new AlwaysCallback<String, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, String resolved, Throwable rejected) {
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
