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

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import io.reinert.requestor.Session;
import io.reinert.requestor.examples.showcase.ui.Auth;
import io.reinert.requestor.examples.showcase.ui.BinaryData;
import io.reinert.requestor.examples.showcase.ui.BuildingRequests;
import io.reinert.requestor.examples.showcase.ui.Filters;
import io.reinert.requestor.examples.showcase.ui.Form;
import io.reinert.requestor.examples.showcase.ui.GettingStarted;
import io.reinert.requestor.examples.showcase.ui.Home;
import io.reinert.requestor.examples.showcase.ui.Interceptors;
import io.reinert.requestor.examples.showcase.ui.Requesting;
import io.reinert.requestor.examples.showcase.ui.SendingRequests;
import io.reinert.requestor.examples.showcase.ui.Serialization;

public class ShowcaseClientFactoryImpl implements ShowcaseClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private Session session;
    private Home home;
    private GettingStarted gettingStarted;
    private Requesting requesting;
    private BuildingRequests buildingRequests;
    private SendingRequests sendingRequests;
    private Form form;
    private BinaryData binaryData;
    private Auth auth;
    private Filters filters;
    private Interceptors interceptors;
    private Serialization serialization;

    public ShowcaseClientFactoryImpl() {
        initRequestor();
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Home getHome() {
        if (home == null)
            home = new Home();
        return home;
    }

    @Override
    public GettingStarted getGettingStarted() {
        if (gettingStarted == null)
            gettingStarted = new GettingStarted();
        return gettingStarted;
    }

    @Override
    public Requesting getRequesting() {
        if (requesting == null)
            requesting = new Requesting();
        return requesting;
    }

    @Override
    public BuildingRequests getBuildingRequests() {
        if (buildingRequests == null)
            buildingRequests = new BuildingRequests();
        return buildingRequests;
    }

    @Override
    public SendingRequests getSendingRequests() {
        if (sendingRequests == null)
            sendingRequests = new SendingRequests();
        return sendingRequests;
    }

    @Override
    public BinaryData getBinaryData() {
        if (binaryData == null)
            binaryData = new BinaryData();
        return binaryData;
    }

    @Override
    public Auth getAuth() {
        if (auth == null)
            auth = new Auth();
        return auth;
    }

    @Override
    public Form getForm() {
        if (form == null)
            form = new Form();
        return form;
    }

    @Override
    public Serialization getSerialization() {
        if (serialization == null)
            serialization = new Serialization();
        return serialization;
    }

    @Override
    public Filters getFilters() {
        if (filters == null)
            filters = new Filters();
        return filters;
    }

    @Override
    public Interceptors getInterceptors() {
        if (interceptors == null)
            interceptors = new Interceptors();
        return interceptors;
    }

    private void initRequestor() {
        session = Session.newInstance();
        session.setMediaType(null); // Avoid auto-setting Accept and Content-Type headers to application/json
    }
}
