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

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Showcase implements EntryPoint {

//    private final Place defaultPlace = new HomePlace();
//    private TwitterShell appContainer;

    @Override
    public void onModuleLoad() {
        // Create ClientFactory using deferred binding so we can replace with different
        // impls in gwt.xml
//        SampleClientFactory clientFactory = GWT.create(SampleClientFactory.class);
//        EventBus eventBus = clientFactory.getEventBus();
//        PlaceController placeController = clientFactory.getPlaceController();
//        appContainer = new TwitterShell(eventBus, clientFactory.getUserName());
//
//        // Start ActivityManager for the main widget with our ActivityMapper
//        ActivityMapper activityMapper = new SampleActivityMapper(clientFactory);
//        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
//        activityManager.setDisplay(appContainer);
//
//        // Start PlaceHistoryHandler with our PlaceHistoryMapper
//        PlaceHistoryMapper historyMapper = GWT.create(SamplePlaceHistoryMapper.class);
//        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
//        historyHandler.register(placeController, eventBus, defaultPlace);
//
//        RootPanel.get().add(appContainer);
//
//        // Goes to place represented on URL or default place
//        historyHandler.handleCurrentHistory();
    }
}
