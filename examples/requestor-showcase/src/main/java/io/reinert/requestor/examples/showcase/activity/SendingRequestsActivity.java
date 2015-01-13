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

import io.reinert.requestor.Requestor;
import io.reinert.requestor.examples.showcase.ui.SendingRequests;
import io.reinert.requestor.examples.showcase.util.Page;

public class SendingRequestsActivity extends ShowcaseActivity {

    private final SendingRequests view;
    private final Requestor requestor;

    public SendingRequestsActivity(String section, SendingRequests view, Requestor requestor) {
        super(section);
        this.view = view;
        this.requestor = requestor;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        Page.setTitle("Sending Requests");
        Page.setDescription("Know the options for sending a request.");
        panel.setWidget(view);
        scrollToSection();
    }
}
