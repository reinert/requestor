/*
 * Copyright 2014-2021 Danilo Reinert
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
package io.reinert.requestor.gwt.xhr;

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.core.ProgressEvent;

final class JsProgressEvent extends JavaScriptObject implements ProgressEvent {

    protected JsProgressEvent() {
    }

    public native boolean lengthComputable() /*-{
        return this.lengthComputable || false;
    }-*/;

    /**
     * Returns the loaded amount of the request.
     * If this property doesn't exist, then 0 is returned.
     *
     * @return The loaded amount if available, 0 otherwise
     */
    public native double loaded() /*-{
        return this.loaded || 0.0;
    }-*/;

    /**
     * Returns the total amount of the request.
     * If this property doesn't exist, then 0 is returned.
     *
     * @return The total amount if available, 0 otherwise
     */
    public native double total() /*-{
        return this.total || 0.0;
    }-*/;
}
