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

import com.google.gwt.place.shared.Place;

import io.reinert.requestor.examples.showcase.place.HomePlace;

/**
 * Menu options of Requestor Showcase.
 */
public enum MenuOption implements HasToken, HasPlace {

    HOME("Requestor", Tokens.HOME_TOKEN, new HomePlace()),
    GETTING_STARTED("Getting Started", Tokens.GETTING_STARTED_TOKEN, null),
    FORM("Form", Tokens.FORM_TOKEN, null),
    AUTHENTICATION("Authentication", Tokens.AUTHENTICATION_TOKEN, null),
    STREAM("Stream", Tokens.STREAM_TOKEN, null),
    FILTERS("Filters", Tokens.FILTERS_TOKEN, null),
    INTERCEPTORS("Interceptors", Tokens.INTERCEPTORS_TOKEN, null);

    public static class Tokens {
        public static final String HOME_TOKEN = "home";
        public static final String GETTING_STARTED_TOKEN = "getting-started";
        public static final String FORM_TOKEN = "form";
        public static final String AUTHENTICATION_TOKEN = "authentication";
        public static final String STREAM_TOKEN = "stream";
        public static final String FILTERS_TOKEN = "filters";
        public static final String INTERCEPTORS_TOKEN = "interceptors";
    }

    public static MenuOption of(String token) {
        if (token.equals(Tokens.GETTING_STARTED_TOKEN)) {
            return GETTING_STARTED;
        } else if (token.equals(Tokens.FORM_TOKEN)) {
            return FORM;
        } else if (token.equals(Tokens.AUTHENTICATION_TOKEN)) {
            return AUTHENTICATION;
        } else if (token.equals(Tokens.STREAM_TOKEN)) {
            return STREAM;
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
    private final Place place;

    private MenuOption(String label, String token, Place place) {
        this.label = label;
        this.token = token;
        this.place = place;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public Place getPlace() {
        return place;
    }

    @Override
    public String getToken() {
        return token;
    }
}
