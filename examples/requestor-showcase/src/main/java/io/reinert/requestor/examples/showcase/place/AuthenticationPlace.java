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
package io.reinert.requestor.examples.showcase.place;

import com.google.gwt.activity.shared.Activity;

import io.reinert.requestor.examples.showcase.MenuOption;
import io.reinert.requestor.examples.showcase.Showcase;
import io.reinert.requestor.examples.showcase.activity.AuthenticationActivity;

public class AuthenticationPlace extends ShowcasePlace {

    public AuthenticationPlace(String section) {
        super(MenuOption.Tokens.AUTHENTICATION_TOKEN, section);
    }

    @Override
    public Activity getActivity() {
        return new AuthenticationActivity(getSection(), Showcase.CLIENT_FACTORY.getAuthentication(),
                Showcase.CLIENT_FACTORY.getRequestor());
    }
}
