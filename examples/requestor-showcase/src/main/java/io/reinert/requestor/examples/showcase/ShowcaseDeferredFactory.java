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

import io.reinert.gdeferred.FailCallback;
import io.reinert.requestor.DeferredFactory;
import io.reinert.requestor.RequestPermissionException;
import io.reinert.requestor.deferred.Deferred;
import io.reinert.requestor.gdeferred.GDeferredRequest;

/**
 * Factory that creates a deferred with a preset fail callback.
 */
class ShowcaseDeferredFactory implements DeferredFactory {

    @Override
    public <T> Deferred<T> getDeferred() {
        final GDeferredRequest<T> deferred = new GDeferredRequest<T>();
        deferred.fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                GWT.log("The following exception occurred while requesting.", result);
                if (result instanceof RequestPermissionException) {
                    Window.alert("The XHR could not be opened due to security reasons. "
                            + "If you are using IE9- or Opera Mini probably it's because the poor support for CORS of "
                            + "your browser. See browser's console log for more details.");
                } else {
                    Window.alert("The request has failed with the following message: \"" + result.getMessage()
                            + "\". See browser's console log for more details.");
                }
            }
        });
        return deferred;
    }
}
