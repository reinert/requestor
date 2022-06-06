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

import io.reinert.requestor.core.DelaySequence;
import io.reinert.requestor.core.Registration;
import io.reinert.requestor.core.RequestEvent;
import io.reinert.requestor.core.RequestFilter;
import io.reinert.requestor.core.RequestInProcess;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.ResponseFilter;
import io.reinert.requestor.core.ResponseInProcess;
import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.auth.BasicAuth;
import io.reinert.requestor.core.callback.PayloadCallback;
import io.reinert.requestor.core.callback.PayloadResponseCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.examples.showcase.Showcase;
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
        Page.setDescription("Asynchronously manipulate deserialized requests and responses.");
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
        final Registration reg = session.register(new RequestFilter() {
            public void filter(RequestInProcess request) {
                if (!request.hasHeader("Custom-Header"))
                    request.setHeader("Custom-Header", "It Works!");

                if (request.getAuth() == null)
                    request.setAuth(new BasicAuth("user", "pwd"));

                if (request.getTimeout() == 0)
                    request.setTimeout(30000);

                if (request.isRetryEnabled())
                    request.setRetry(DelaySequence.fixed(1, 5, 30), RequestEvent.TIMEOUT, Status.TOO_MANY_REQUESTS);

                if (request.getPayload() == null) {
                    request.setContentType("text/plain");
                    request.setPayload("A text payload");
                }

                request.proceed();
            }
        });

        // Perform the request
        session.req(Showcase.CLIENT_FACTORY.getAnythingUri()).post(String.class)
                .onSuccess(new PayloadCallback<String>() {
                    public void execute(String result) {
                        view.setRequestFilterText(result);
                    }
                })
                .onLoad(new ResponseCallback() {
                    public void execute(Response response) {
                        // cancel filter registration on response received
                        reg.cancel();
                    }
                });
    }

    @Override
    public void onResponseFilterButtonClick() {
        // Add the filter and hold the registration
        final Registration reg = session.register(new ResponseFilter() {
            public void filter(ResponseInProcess response) {
                if (!response.hasHeader("Custom-Header"))
                    response.setHeader("Custom-Header", "Added after response was received");

                // Check if the caller requested to deserialize the payload as String
                if (response.getPayloadType().getType() == String.class) {
                    String payload = response.getPayload();
                    response.setPayload(payload + "\nWE JUST MODIFIED THE PAYLOAD!");
                }

                response.proceed();
            }
        });

        // Perform the request
        session.req(Showcase.CLIENT_FACTORY.getAnythingUri()).get(String.class)
                .onSuccess(new PayloadResponseCallback<String>() {
                    public void execute(String payload, Response res) {
                        view.setResponseBody(payload);
                        view.setResponseHeaders(Util.formatHeaders(res.getHeaders()));
                    }
                })
                .onLoad(new ResponseCallback() {
                    public void execute(Response response) {
                        reg.cancel(); // cancel filter registration
                    }
                });
    }
}
