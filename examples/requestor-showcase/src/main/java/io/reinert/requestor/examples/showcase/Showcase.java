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
package io.reinert.requestor.examples.showcase;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import io.reinert.requestor.examples.showcase.place.HomePlace;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Showcase implements EntryPoint {

    public static final ShowcaseClientFactory SHOWCASE_CLIENT_FACTORY = GWT.create(ShowcaseClientFactory.class);

    private final Place defaultPlace = HomePlace.INSTANCE;

    @Override
    public void onModuleLoad() {
        // Populate the menu
        final Element menu = Document.get().getElementById("menu-list");
        for (MenuOption o : MenuOption.values()) {
            if (o != MenuOption.HOME) {
                if (o.isGroup()) {
                    AnchorElement a = Document.get().createAnchorElement();
                    a.getStyle().setCursor(Style.Cursor.POINTER);
                    a.setClassName("dropdown-toggle");
                    a.setAttribute("role", "button");
                    a.setAttribute("data-toggle", "dropdown");
                    a.setInnerHTML(o.getLabel() + " <span class=\"caret\"></span>");

                    UListElement ul = Document.get().createULElement();
                    ul.setClassName("dropdown-menu");
                    ul.setAttribute("role", "menu");
                    ul.setId(getMenuGroupId(o));

                    LIElement li = Document.get().createLIElement();
                    li.setClassName("dropdown");
                    li.appendChild(a);
                    li.appendChild(ul);

                    menu.appendChild(li);
                } else {
                    AnchorElement a = Document.get().createAnchorElement();
                    a.setInnerText(o.getLabel());
                    a.setHref("#" + o.getToken());

                    LIElement li = Document.get().createLIElement();
                    li.appendChild(a);

                    if (o.hasParent()) {
                        MenuOption parent = o.getParent();
                        UListElement ul = (UListElement) Document.get().getElementById(getMenuGroupId(parent));
                        ul.appendChild(li);
                    } else {
                        menu.appendChild(li);
                    }
                }
            }
        }

        final SimplePanel container = new SimplePanel();
        container.setStyleName("container requestor-showcase-container");
        RootPanel.get().add(container);

        // Main Factory (Dependency Injector)
        ShowcaseClientFactory clientFactory = SHOWCASE_CLIENT_FACTORY;
        EventBus eventBus = clientFactory.getEventBus();
        PlaceController placeController = clientFactory.getPlaceController();

        // Activity-Place binding
        ActivityMapper activityMapper = new ShowcaseActivityMapper();
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(container);

        // Place-History binding
        PlaceHistoryMapper historyMapper = new ShowcasePlaceHistoryMapper();
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        // Goes to place represented on URL or default place
        historyHandler.handleCurrentHistory();
    }

    private String getMenuGroupId(MenuOption o) {
        return o.getLabel().toLowerCase().replace(" ", "-");
    }
}
