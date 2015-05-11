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
package io.reinert.requestor.turbogwt;

import io.reinert.requestor.Provider;
import io.reinert.requestor.Requestor;
import io.reinert.requestor.RequestorInitializer;
import io.reinert.requestor.serialization.json.JsonBooleanSerdes;
import io.reinert.requestor.serialization.json.JsonNumberSerdes;
import io.reinert.requestor.serialization.json.JsonStringSerdes;
import io.reinert.requestor.serialization.misc.TextSerdes;
import io.reinert.requestor.serialization.misc.VoidSerdes;
import io.reinert.requestor.turbogwt.serialization.TurboOverlaySerdes;

import org.turbogwt.core.collections.JsArrayList;

/**
 * Initializer that configures the Requestor for Turbo GWT.
 *
 * @author Danilo Reinert
 */
public class RequestorInitializerTurboGwt implements RequestorInitializer {

    @Override
    public void configure(Requestor requestor) {
        requestor.addSerdes(TurboOverlaySerdes.getInstance());
        requestor.addSerdes(JsonStringSerdes.getInstance());
        requestor.addSerdes(JsonNumberSerdes.getInstance());
        requestor.addSerdes(JsonBooleanSerdes.getInstance());
        requestor.addSerdes(VoidSerdes.getInstance());
        requestor.addSerdes(TextSerdes.getInstance());
        requestor.setDefaultMediaType("application/json");
        requestor.bindProvider(JsArrayList.class, new Provider<JsArrayList>() {

            @Override
            public Class<JsArrayList> getType() {
                return JsArrayList.class;
            }

            @Override
            public JsArrayList get() {
                return new JsArrayList();
            }
        });
    }
}
