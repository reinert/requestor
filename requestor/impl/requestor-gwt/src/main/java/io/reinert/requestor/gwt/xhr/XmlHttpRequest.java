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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;

class XmlHttpRequest extends com.google.gwt.xhr.client.XMLHttpRequest {

    private static final Logger logger = Logger.getLogger("io.reinert.requestor.core.XmlHttpRequest");

    public static class Upload extends JavaScriptObject {

        protected Upload() {
        }

        public final native void setOnProgress(ProgressHandler handler) /*-{
            if ("onprogress" in this) {
                this.onprogress = $entry(function(e) {
                    // CHECKSTYLE:OFF
                    handler.@io.reinert.requestor.gwt.xhr.ProgressHandler::onProgress(Lio/reinert/requestor/core/ProgressEvent;)(e);
                    // CHECKSTYLE:ON
                });
            }
        }-*/;
    }

    protected XmlHttpRequest() {
    }

    /**
     * Clears the {@link ProgressHandler}.
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

    public final void setOnProgress(ProgressHandler handler) {
        if (!setOnProgressNative(handler)) {
            logger.log(Level.SEVERE, "Set OnProgress failed: XHR onprogress handler not supported by the browser.");
        }
    }

    public final native boolean setOnProgressNative(ProgressHandler handler) /*-{
        if ("onprogress" in this) {
            this.onprogress = $entry(function(e) {
                // CHECKSTYLE:OFF
                handler.@io.reinert.requestor.gwt.xhr.ProgressHandler::onProgress(Lio/reinert/requestor/core/ProgressEvent;)(e);
                // CHECKSTYLE:ON
            });
            return true;
        }
        return false;
    }-*/;

    public final void setUploadOnProgress(ProgressHandler handler) {
        final Upload upload = getUpload();
        if (upload != null) {
            upload.setOnProgress(handler);
        } else {
            logger.log(Level.SEVERE, "Set UploadOnProgress failed: XHR upload property not supported by the browser.");
        }
    }

    public final native Upload getUpload() /*-{
        return this.upload || null;
    }-*/;

    public final void setOnError(ProgressHandler handler) {
        if (!setOnErrorNative(handler)) {
            logger.log(Level.SEVERE, "Set onError failed: XHR onerror handler not supported by the browser.");
        }
    }

    public final native boolean setOnErrorNative(ProgressHandler handler) /*-{
        if ("onerror" in this) {
            this.onerror = $entry(function(e) {
                // CHECKSTYLE:OFF
                handler.@io.reinert.requestor.gwt.xhr.ProgressHandler::onProgress(Lio/reinert/requestor/core/ProgressEvent;)(e);
                // CHECKSTYLE:ON
            });
            return true;
        }
        return false;
    }-*/;

    public final void setOnAbort(ProgressHandler handler) {
        if (!setOnErrorNative(handler)) {
            logger.log(Level.SEVERE, "Set onError failed: XHR onerror handler not supported by the browser.");
        }
    }

    public final native boolean setOnAbortNative(ProgressHandler handler) /*-{
        if ("onabort" in this) {
            this.onerror = $entry(function(e) {
                // CHECKSTYLE:OFF
                handler.@io.reinert.requestor.gwt.xhr.ProgressHandler::onProgress(Lio/reinert/requestor/core/ProgressEvent;)(e);
                // CHECKSTYLE:ON
            });
            return true;
        }
        return false;
    }-*/;
}
