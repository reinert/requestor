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
package com.google.gwt.http.client;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * Workaround to enable full use of {@link com.google.gwt.http.client.Request} by Requestor.
 *
 * This class exposes both constructor and fireOnResponseReceived method that are originally package-private.
 *
 * @author Danilo Reinert
 */
public class RequestorRequest extends Request {

    /**
     * Constructs an instance of the Request object.
     *
     * @param xmlHttpRequest JavaScript XmlHttpRequest object instance
     * @param timeoutMillis number of milliseconds to wait for a response
     * @param callback callback interface to use for notification
     *
     * @throws IllegalArgumentException if timeoutMillis &lt; 0
     * @throws NullPointerException if xmlHttpRequest, or callback are null
     */
    public RequestorRequest(XMLHttpRequest xmlHttpRequest, int timeoutMillis, RequestCallback callback) {
        super(xmlHttpRequest, timeoutMillis, callback);
    }

    /*
     * Method called when the JavaScript XmlHttpRequest object's readyState
     * reaches 4 (LOADED).
     */
    public void fireOnResponseReceived(RequestCallback callback) {
        super.fireOnResponseReceived(callback);
    }
}
