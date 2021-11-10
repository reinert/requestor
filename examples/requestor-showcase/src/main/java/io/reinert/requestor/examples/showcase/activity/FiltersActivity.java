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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import io.reinert.requestor.core.Registration;
import io.reinert.requestor.core.RequestFilter;
import io.reinert.requestor.core.RequestInProcess;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.ResponseFilter;
import io.reinert.requestor.core.ResponseInProcess;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.examples.showcase.ui.Filters;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.examples.showcase.util.Util;

public class FiltersActivity extends ShowcaseActivity implements Filters.Handler {

    private final Filters view;
    private final Session session;

    public FiltersActivity(String section, Filters view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Filters");
        Page.setDescription("Explore the power of managing your requests by applying filters.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }

    @Override
    public void onRequestFilterButtonClick() {
        // Add the filter and hold the registration
        final Registration requestFilterRegistration = session.register(new RequestFilter() {
            @Override
            public void filter(RequestInProcess request) {
                request.setHeader("A-Request-Filter-Header", "It Works!");
                request.proceed();
            }
        });

        // Perform the request
        session.req("http://httpbin.org/headers").get(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    @Override
                    public void execute(String result) {
                        view.setRequestFilterText(result);
                    }
                })
                .onLoad(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        requestFilterRegistration.cancel(); // cancel filter registration
                    }
                });
    }

    @Override
    public void onResponseFilterButtonClick() {
        // Add the filter and hold the registration
        final Registration responseFilterRegistration = session.register(new ResponseFilter() {
            @Override
            public void filter(ResponseInProcess response) {
                response.setHeader("A-Response-Filter-Header", "It Works!");
                response.proceed();
            }
        });

        // Perform the response
        session.req("http://httpbin.org/headers").get(String.class)
                .onStatus(200, new ResponseCallback() {
                    public void execute(Response response) {
                        view.setResponseFilterText(
                                Util.formatHeaders(response.getHeaders()),
                                (String) response.getPayload()
                        );
                    }
                })
                .onLoad(new ResponseCallback() {
                    @Override
                    public void execute(Response response) {
                        responseFilterRegistration.cancel(); // cancel filter registration
                    }
                });
    }
}
