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
package io.reinert.requestor;

import com.google.gwt.core.client.JavaScriptObject;

class XMLHttpRequest extends com.google.gwt.xhr.client.XMLHttpRequest {

    public static class Upload extends JavaScriptObject {

        protected Upload() {
        }

        public final native void setOnProgress(ProgressHandler handler) /*-{
            if ("onprogress" in this) {
                this.onprogress = $entry(function(e) {
                    handler.@io.reinert.requestor.ProgressHandler::onProgress(Lio/reinert/requestor/ProgressEvent;)(e);
                });
            }
        }-*/;
    }

    protected XMLHttpRequest() {
    }

    /**
     * Clears the {@link com.google.gwt.xhr.client.ReadyStateChangeHandler}.
     * <p>
     * See <a href="http://www.w3.org/TR/XMLHttpRequest/#handler-xhr-onreadystatechange"
     * >http://www.w3.org/TR/XMLHttpRequest/#handler-xhr-onreadystatechange</a>.
     *
     * @see #clearOnReadyStateChange()
     */
    public final native void clearOnProgress() /*-{
        if ("onprogress" in self)
            self.onprogress = null;
        if ("upload" in self)
            self.upload.onprogress = null;
    }-*/;

    public final native JavaScriptObject getResponse() /*-{
        return this.response;
    }-*/;

    /**
     * Initiates a request with custom data. If there is no data, specify null.
     * <p>
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest#send()"
     * >https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest#send()</a>.
     *
     * @param requestData the data to be sent with the request
     */
    public final native void send(JavaScriptObject requestData) /*-{
        this.send(requestData);
    }-*/;

    public final native void setOnProgress(ProgressHandler handler) /*-{
        if ("onprogress" in this) {
            this.onprogress = $entry(function(e) {
                handler.@io.reinert.requestor.ProgressHandler::onProgress(Lio/reinert/requestor/ProgressEvent;)(e);
            });
        }
    }-*/;

    public final void setUploadOnProgress(ProgressHandler handler) {
        final Upload upload = getUpload();
        if (upload != null) {
            upload.setOnProgress(handler);
        }
    }

    public final native Upload getUpload() /*-{
        return this.upload;
    }-*/;
}
