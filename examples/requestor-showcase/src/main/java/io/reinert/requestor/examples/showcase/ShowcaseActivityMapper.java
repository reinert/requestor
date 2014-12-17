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

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import io.reinert.requestor.examples.showcase.activity.GettingStartedActivity;
import io.reinert.requestor.examples.showcase.activity.HomeActivity;
import io.reinert.requestor.examples.showcase.place.GettingStartedPlace;
import io.reinert.requestor.examples.showcase.place.HomePlace;

public class ShowcaseActivityMapper implements ActivityMapper {

    private final ShowcaseClientFactory clientFactory;

    public ShowcaseActivityMapper(ShowcaseClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof HomePlace)
            return new HomeActivity(clientFactory.getHome());
        if (place instanceof GettingStartedPlace)
            return new GettingStartedActivity(clientFactory.getGettingStarted());
        return null;
    }
}
