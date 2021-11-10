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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import io.reinert.requestor.core.Deferred;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.examples.showcase.ui.loading.event.HideLoadingEvent;
import io.reinert.requestor.examples.showcase.ui.loading.event.ShowLoadingEvent;
import io.reinert.requestor.gwt.GwtDeferred;

/**
 * Factory that creates a deferred with a preset fail callback.
 */
class ShowcaseDeferredFactory implements Deferred.Factory {

    @Override
    public <T> Deferred<T> newDeferred() {
        final GwtDeferred<T> deferred = new GwtDeferred<T>();

        // Show loading widget
        Showcase.CLIENT_FACTORY.getEventBus().fireEventFromSource(new ShowLoadingEvent(), deferred);

        deferred.fail(new ResponseCallback() {
            @Override
            public void execute(Response response) {
                if (Status.UNAUTHORIZED.is(response.getStatusCode())) {
                    Window.alert("The XHR could not be opened due to security reasons. "
                            + "If you are using IE9- or Opera Mini probably it's because the poor support for CORS of "
                            + "your browser. See browser's console log for more details.");
                } else {
                    Window.alert("The request has failed with the following message: \""
                            + response.getStatus().getReasonPhrase()
                            + "\". See browser's network for more details.");
                }
            }
        }).load(new ResponseCallback() {
            public void execute(Response response) {
                // Hide loading widget
                Showcase.CLIENT_FACTORY.getEventBus().fireEventFromSource(new HideLoadingEvent(), deferred);
            }
        }).abort(new ExceptionCallback() {
            public void execute(RequestException exception) {
                // Hide loading widget
                Showcase.CLIENT_FACTORY.getEventBus().fireEventFromSource(new HideLoadingEvent(), deferred);

                GWT.log("The following exception aborted the request.", exception);
            }
        });

        return deferred;
    }
}
