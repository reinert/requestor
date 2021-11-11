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
package io.reinert.requestor.gwt.xhr;

import io.reinert.requestor.core.RequestException;
import io.reinert.requestor.core.RequestOptions;

/**
 * Exception thrown when attempts to make a request to a URL which violates the <a
 * href="http://en.wikipedia.org/wiki/Same_origin_policy">Same-Origin Security Policy</a>.
 */
public class RequestPermissionException extends RequestException {

    /**
     * URL which caused this exception to be thrown.
     */
    private final String url;

    /**
     * Constructs an instance of this class for the given URL.
     *
     * @param url the URL which cannot be accessed
     */
    public RequestPermissionException(RequestOptions requestOptions, String url) {
        super(requestOptions, "Could not open the XHR: The URL " + url + " is invalid or violates the same-origin" +
                " security restriction. Please check your browser's security settings for the related URL.");
        this.url = url;
    }

    /**
     * Returns the URL which we cannot access.
     *
     * @return the URL which we cannot access.
     */
    public String getURL() {
        return url;
    }
}
