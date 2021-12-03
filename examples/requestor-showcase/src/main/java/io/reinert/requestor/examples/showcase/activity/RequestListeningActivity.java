/*
 * Copyright 2021 Danilo Reinert
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

import io.reinert.requestor.core.Session;
import io.reinert.requestor.examples.showcase.ui.RequestListening;
import io.reinert.requestor.examples.showcase.util.Page;

public class RequestListeningActivity extends ShowcaseActivity implements RequestListening.Handler {

    private final RequestListening view;
    private final Session session;

    public RequestListeningActivity(String section, RequestListening view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setHandler(this);
        Page.setTitle("Request Listening");
        Page.setDescription("Set callbacks for specific request events.");
        panel.setWidget(view);
        scrollToSection();
    }

    @Override
    public void onStop() {
        view.setHandler(null);
    }
}
