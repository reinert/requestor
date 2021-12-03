/*
 * Copyright 2014 Danilo Reinert
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
package io.reinert.requestor.examples.showcase.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import io.reinert.requestor.examples.showcase.util.HighlightJs;

public class GettingStarted extends Composite {

    interface GettingStartedUiBinder extends UiBinder<HTMLPanel, GettingStarted> { }

    private static GettingStartedUiBinder uiBinder = GWT.create(GettingStartedUiBinder.class);

    @UiField Element mavenInstallation, gwtModule, getRequestorInstance, makeRequest;

    public GettingStarted() {
        initWidget(uiBinder.createAndBindUi(this));
        HighlightJs.highlightBlock(mavenInstallation, gwtModule, getRequestorInstance, makeRequest);
    }
}
