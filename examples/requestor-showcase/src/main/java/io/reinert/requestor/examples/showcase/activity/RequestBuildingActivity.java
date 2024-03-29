/*
 * Copyright 2014-2021 Danilo Reinert
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
import io.reinert.requestor.examples.showcase.ui.RequestBuilding;
import io.reinert.requestor.examples.showcase.util.Page;

public class RequestBuildingActivity extends ShowcaseActivity {

    private final RequestBuilding view;
    private final Session session;

    public RequestBuildingActivity(String section, RequestBuilding view, Session session) {
        super(section);
        this.view = view;
        this.session = session;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        Page.setTitle("Request Building");
        Page.setDescription("Know the options to set up a request.");
        panel.setWidget(view);
        scrollToSection();
    }
}
