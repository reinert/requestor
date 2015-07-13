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
import com.google.web.bindery.event.shared.HandlerRegistration;

import io.reinert.gdeferred.AlwaysCallback;
import io.reinert.gdeferred.Promise;
import io.reinert.requestor.Request;
import io.reinert.requestor.RequestFilter;
import io.reinert.requestor.RequestFilterContext;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.Response;
import io.reinert.requestor.ResponseFilter;
import io.reinert.requestor.ResponseFilterContext;
import io.reinert.requestor.examples.showcase.ui.Filters;
import io.reinert.requestor.examples.showcase.util.Page;
import io.reinert.requestor.examples.showcase.util.Util;
import io.reinert.requestor.gdeferred.DoneCallback;

public class FiltersActivity extends ShowcaseActivity implements Filters.Handler {

    private final Filters view;
    private final Requestor requestor;

    public FiltersActivity(String section, Filters view, Requestor requestor) {
        super(section);
        this.view = view;
        this.requestor = requestor;
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
        final HandlerRegistration requestFilterRegistration = requestor.register(new RequestFilter() {
            @Override
            public void filter(RequestFilterContext request) {
                request.setHeader("A-Request-Filter-Header", "It Works!");
            }
        });

        // Perform the request
        requestor.req("http://httpbin.org/headers").get(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(String result) {
                        view.setRequestFilterText(result);
                    }
                })
                .always(new AlwaysCallback<String, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, String resolved, Throwable rejected) {
                        requestFilterRegistration.removeHandler(); // cancel filter registration
                    }
                });
    }

    @Override
    public void onResponseFilterButtonClick() {
        // Add the filter and hold the registration
        final HandlerRegistration responseFilterRegistration = requestor.register(new ResponseFilter() {
            @Override
            public void filter(Request request, ResponseFilterContext response) {
                response.setHeader("A-Response-Filter-Header", "It Works!");
            }
        });

        // Perform the response
        requestor.req("http://httpbin.org/headers").get(String.class)
                .done(new DoneCallback<String>() {
                    @Override
                    public void onDone(Response<String> response) {
                        view.setResponseFilterText(Util.formatHeaders(response.getHeaders()), response.getPayload());
                    }
                })
                .always(new AlwaysCallback<String, Throwable>() {
                    @Override
                    public void onAlways(Promise.State state, String resolved, Throwable rejected) {
                        responseFilterRegistration.removeHandler(); // cancel filter registration
                    }
                });
    }
}
