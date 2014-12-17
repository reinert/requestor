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
package io.reinert.requestor.examples.showcase;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import io.reinert.requestor.examples.showcase.place.HomePlace;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Showcase implements EntryPoint {

    private final Place defaultPlace = new HomePlace();

    @Override
    public void onModuleLoad() {

        final AcceptsOneWidget container = new RootSimplePanel();

        final Element menu = Document.get().getElementById("menu-list");
        for (MenuOption o : MenuOption.values()) {
            if (o != MenuOption.HOME) {
                AnchorElement a = Document.get().createAnchorElement();
                a.setInnerText(o.getLabel());
                a.setHref("#" + o.getToken());

                LIElement li = Document.get().createLIElement();
                li.appendChild(a);

                menu.appendChild(li);
            }
        }

        // Create ClientFactory using deferred binding so we can replace with different
        // impls in gwt.xml
        ShowcaseClientFactory clientFactory = GWT.create(ShowcaseClientFactory.class);
        EventBus eventBus = clientFactory.getEventBus();
        PlaceController placeController = clientFactory.getPlaceController();

        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityMapper activityMapper = new ShowcaseActivityMapper(clientFactory);
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(container);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        PlaceHistoryMapper historyMapper = GWT.create(ShowcasePlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        // Goes to place represented on URL or default place
        historyHandler.handleCurrentHistory();

//        GWT.log(clientFactory.getRequestor().getDefaultMediaType());
//        final RequestInvoker request = clientFactory.getRequestor().request("http://httpbin.org/ip");
//        GWT.log(request.getAccept());
//        GWT.log(request.getContentType());
//        request.get(JavaScriptObject.class).done(new DoneCallback<JavaScriptObject>() {
//            @Override
//            public void onDone(JavaScriptObject result) {
//                GWT.log(JsonSerdes.stringify(result));
//            }
//        });
    }

    static class RootWidget extends Composite implements AcceptsOneWidget {

        final RootPanel rootPanel = RootPanel.get("page-container");

        public RootWidget() {
            initWidget(rootPanel);
        }

        @Override
        public void setWidget(IsWidget w) {
            rootPanel.clear();
            rootPanel.add(w);
        }
    }

    static class RootSimplePanel extends SimplePanel {

        public RootSimplePanel() {
            super(Document.get().getElementById("page-container"));
        }
    }
}
