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

import com.google.gwt.place.shared.Place;

import io.reinert.requestor.examples.showcase.place.AuthPlace;
import io.reinert.requestor.examples.showcase.place.BinaryDataPlace;
import io.reinert.requestor.examples.showcase.place.BuildingRequestsPlace;
import io.reinert.requestor.examples.showcase.place.FiltersPlace;
import io.reinert.requestor.examples.showcase.place.FluentRequestApiPlace;
import io.reinert.requestor.examples.showcase.place.FormPlace;
import io.reinert.requestor.examples.showcase.place.GettingStartedPlace;
import io.reinert.requestor.examples.showcase.place.HomePlace;
import io.reinert.requestor.examples.showcase.place.InterceptorsPlace;
import io.reinert.requestor.examples.showcase.place.RequestInvokingPlace;
import io.reinert.requestor.examples.showcase.place.SerializationPlace;

/**
 * Menu options of Requestor Showcase.
 */
public enum MenuOption implements HasToken, HasPlace {

    HOME("Requestor", Tokens.HOME_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new HomePlace();
        }
    }),

    GETTING_STARTED("Getting Started", Tokens.GETTING_STARTED_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new GettingStartedPlace(section);
        }
    }),

    BASIC_USAGE("Basic Usage"),
    FLUENT_REQUEST_API("Fluent Requesting API", Tokens.FLUENT_REQUEST_API_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new FluentRequestApiPlace(section);
        }
    }, BASIC_USAGE),
    BUILDING_REQUESTS("Building Requests", Tokens.BUILDING_REQUESTS_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new BuildingRequestsPlace(section);
        }
    }, BASIC_USAGE),
    REQUEST_INVOKING("Request Invoking", Tokens.REQUEST_INVOKING_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new RequestInvokingPlace(section);
        }
    }, BASIC_USAGE),

    MANAGING_REQUESTS("Managing Requests"),
    SERIALIZATION("Serialization", Tokens.SERIALIZATION_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new SerializationPlace(section);
        }
    }, MANAGING_REQUESTS),
    FILTERS("Filters", Tokens.FILTERS_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new FiltersPlace(section);
        }
    }, MANAGING_REQUESTS),
    INTERCEPTORS("Interceptors", Tokens.INTERCEPTORS_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new InterceptorsPlace(section);
        }
    }, MANAGING_REQUESTS),

    FEATURES("Features"),
    FORM("Form Data", Tokens.FORM_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new FormPlace(section);
        }
    }, FEATURES),
    BINARY_DATA("Binary Data", Tokens.BINARY_DATA_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new BinaryDataPlace(section);
        }
    }, FEATURES),
    AUTHENTICATION("Authentication & Authorization", Tokens.AUTH_TOKEN, new HasPlace() {
        @Override
        public Place getPlace(String section) {
            return new AuthPlace(section);
        }
    }, FEATURES)
    ;

    public static class Tokens {
        public static final String HOME_TOKEN = "home";
        public static final String GETTING_STARTED_TOKEN = "getting-started";
        public static final String FLUENT_REQUEST_API_TOKEN = "fluent-requesting-api";
        public static final String BUILDING_REQUESTS_TOKEN = "building-requests";
        public static final String REQUEST_INVOKING_TOKEN = "request-invoking";
        public static final String SERIALIZATION_TOKEN = "serialization";
        public static final String FORM_TOKEN = "form-data";
        public static final String BINARY_DATA_TOKEN = "binary-data";
        public static final String AUTH_TOKEN = "auth";
        public static final String FILTERS_TOKEN = "filters";
        public static final String INTERCEPTORS_TOKEN = "interceptors";
    }

    public static MenuOption of(String token) {
        if (token.equals(Tokens.GETTING_STARTED_TOKEN)) {
            return GETTING_STARTED;
        } else if (token.equals(Tokens.FLUENT_REQUEST_API_TOKEN)) {
            return FLUENT_REQUEST_API;
        } else if (token.equals(Tokens.BUILDING_REQUESTS_TOKEN)) {
            return BUILDING_REQUESTS;
        } else if (token.equals(Tokens.REQUEST_INVOKING_TOKEN)) {
            return REQUEST_INVOKING;
        } else if (token.equals(Tokens.SERIALIZATION_TOKEN)) {
            return SERIALIZATION;
        } else if (token.equals(Tokens.FORM_TOKEN)) {
            return FORM;
        } else if (token.equals(Tokens.BINARY_DATA_TOKEN)) {
            return BINARY_DATA;
        } else if (token.equals(Tokens.AUTH_TOKEN)) {
            return AUTHENTICATION;
        } else if (token.equals(Tokens.FILTERS_TOKEN)) {
            return FILTERS;
        } else if (token.equals(Tokens.INTERCEPTORS_TOKEN)) {
            return INTERCEPTORS;
        } else {
            return HOME;
        }
    }

    private final String label;
    private final String token;
    private final HasPlace place;
    private final MenuOption parent;

    private MenuOption(String label) {
        this(label, null, null, null);
    }

    private MenuOption(String label, String token, HasPlace place) {
        this(label, token, place, null);
    }

    private MenuOption(String label, String token, HasPlace place, MenuOption parent) {
        this.label = label;
        this.token = token;
        this.place = place;
        this.parent = parent;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public Place getPlace(String section) {
        return place.getPlace(section);
    }

    @Override
    public String getToken() {
        return token;
    }

    public boolean isGroup() {
        return token == null;
    }

    public MenuOption getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }
}
