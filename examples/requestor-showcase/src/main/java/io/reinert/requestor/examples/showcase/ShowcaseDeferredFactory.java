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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import io.reinert.requestor.core.DeferredPool;
import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.Response;
import io.reinert.requestor.core.SerializedRequest;
import io.reinert.requestor.core.Status;
import io.reinert.requestor.core.callback.ExceptionCallback;
import io.reinert.requestor.core.callback.ResponseCallback;
import io.reinert.requestor.core.deferred.DeferredPollingRequest;
import io.reinert.requestor.examples.showcase.ui.loading.event.HideLoadingEvent;
import io.reinert.requestor.examples.showcase.ui.loading.event.ShowLoadingEvent;

/**
 * Factory that creates a deferred with a preset fail callback.
 *
 * @author Danilo Reinert
 */
class ShowcaseDeferredFactory implements DeferredPool.Factory {

    @Override
    public <T> DeferredPool<T> create(SerializedRequest request) {
        final DeferredPollingRequest<T> deferredPool = new DeferredPollingRequest<T>(request);

        if (!request.exists("hidden", true)) {
            // Show loading widget
            Showcase.CLIENT_FACTORY.getEventBus().fireEventFromSource(new ShowLoadingEvent(), deferredPool);

            // Register callbacks to hide the loading widget
            deferredPool.onFail(new ResponseCallback() {
                public void execute(Response response) {
                    if (Status.UNAUTHORIZED.is(response.getStatusCode())) {
                        Window.alert("The XHR could not be opened due to security reasons. "
                                + "If you are using IE9- or Opera Mini probably it's because the poor support for" +
                                " CORS of your browser. See browser's console log for more details.");
                    } else {
                        Window.alert("The request has failed with the following status code: \""
                                + response.getStatus().getStatusCode()
                                + "\". See browser's network for more details.");
                    }
                }
            }).onLoad(new ResponseCallback() {
                public void execute(Response response) {
                    // Hide loading widget
                    Showcase.CLIENT_FACTORY.getEventBus().fireEventFromSource(new HideLoadingEvent(), deferredPool);
                }
            }).onError(new ExceptionCallback() {
                public void execute(RequestException exception) {
                    // Hide loading widget
                    Showcase.CLIENT_FACTORY.getEventBus().fireEventFromSource(new HideLoadingEvent(), deferredPool);

                    GWT.log("The following error has occurred while requesting.", exception);

                    Window.alert("The request has failed. See browser's console for more details.");
                }
            });
        }

        return deferredPool;
    }
}
