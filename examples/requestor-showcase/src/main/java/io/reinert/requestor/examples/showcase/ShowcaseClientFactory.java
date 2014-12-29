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

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import io.reinert.requestor.Requestor;
import io.reinert.requestor.examples.showcase.ui.Authentication;
import io.reinert.requestor.examples.showcase.ui.BinaryData;
import io.reinert.requestor.examples.showcase.ui.BuildingRequests;
import io.reinert.requestor.examples.showcase.ui.Form;
import io.reinert.requestor.examples.showcase.ui.GettingStarted;
import io.reinert.requestor.examples.showcase.ui.Home;
import io.reinert.requestor.examples.showcase.ui.Requesting;
import io.reinert.requestor.examples.showcase.ui.SendingRequests;

public interface ShowcaseClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    Requestor getRequestor();
    Home getHome();
    GettingStarted getGettingStarted();
    Requesting getRequesting();
    Form getForm();
    BuildingRequests getBuildingRequests();
    SendingRequests getSendingRequests();
    Authentication getAuthentication();
    BinaryData getBinaryData();
}
