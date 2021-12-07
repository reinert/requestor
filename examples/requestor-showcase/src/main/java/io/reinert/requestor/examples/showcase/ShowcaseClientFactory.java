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
package io.reinert.requestor.examples.showcase;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import io.reinert.requestor.core.Session;
import io.reinert.requestor.core.uri.Uri;
import io.reinert.requestor.core.uri.UriBuilder;
import io.reinert.requestor.examples.showcase.ui.Auth;
import io.reinert.requestor.examples.showcase.ui.BinaryData;
import io.reinert.requestor.examples.showcase.ui.Filters;
import io.reinert.requestor.examples.showcase.ui.FluentRequestApi;
import io.reinert.requestor.examples.showcase.ui.Form;
import io.reinert.requestor.examples.showcase.ui.GettingStarted;
import io.reinert.requestor.examples.showcase.ui.Home;
import io.reinert.requestor.examples.showcase.ui.Interceptors;
import io.reinert.requestor.examples.showcase.ui.RequestBuilding;
import io.reinert.requestor.examples.showcase.ui.RequestInvoking;
import io.reinert.requestor.examples.showcase.ui.RequestListening;
import io.reinert.requestor.examples.showcase.ui.Serialization;
import io.reinert.requestor.gwt.GwtSession;

public class ShowcaseClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private Session session;
    private Home home;
    private GettingStarted gettingStarted;
    private FluentRequestApi requesting;
    private RequestBuilding buildingRequests;
    private RequestInvoking sendingRequests;
    private RequestListening requestListening;
    private Form form;
    private BinaryData binaryData;
    private Auth auth;
    private Filters filters;
    private Interceptors interceptors;
    private Serialization serialization;

    public EventBus getEventBus() {
        return eventBus;
    }

    public PlaceController getPlaceController() {
        return placeController;
    }

    public Session getSession() {
        if (session == null) {
            session = new GwtSession(new ShowcaseDeferredFactory());
            session.setMediaType(null); // Avoid auto-setting Accept and Content-Type headers to application/json
        }
        return session;
    }

    public Home getHome() {
        if (home == null)
            home = new Home();
        return home;
    }

    public GettingStarted getGettingStarted() {
        if (gettingStarted == null)
            gettingStarted = new GettingStarted();
        return gettingStarted;
    }

    public FluentRequestApi getFluentRequestApi() {
        if (requesting == null)
            requesting = new FluentRequestApi();
        return requesting;
    }

    public RequestBuilding getBuildingRequests() {
        if (buildingRequests == null)
            buildingRequests = new RequestBuilding();
        return buildingRequests;
    }

    public RequestInvoking getRequestInvoking() {
        if (sendingRequests == null)
            sendingRequests = new RequestInvoking();
        return sendingRequests;
    }

    public RequestListening getRequestListening() {
        if (requestListening == null)
            requestListening = new RequestListening();
        return requestListening;
    }

    public BinaryData getBinaryData() {
        if (binaryData == null)
            binaryData = new BinaryData();
        return binaryData;
    }

    public Auth getAuth() {
        if (auth == null)
            auth = new Auth();
        return auth;
    }

    public Form getForm() {
        if (form == null)
            form = new Form();
        return form;
    }

    public Serialization getSerialization() {
        if (serialization == null)
            serialization = new Serialization();
        return serialization;
    }

    public Filters getFilters() {
        if (filters == null)
            filters = new Filters();
        return filters;
    }

    public Interceptors getInterceptors() {
        if (interceptors == null)
            interceptors = new Interceptors();
        return interceptors;
    }

    public UriBuilder getUriBuilder() {
        return UriBuilder.newInstance().scheme("https").host("requestor-server.herokuapp.com");
    }

    public Uri getPostUri() {
        return getUriBuilder().segment("post").build();
    }

    public Uri getAnythingUri() {
        return getUriBuilder().segment("anything").build();
    }

    public Uri getBooksUri() {
        return getUriBuilder().segment("books").build();
    }
}
