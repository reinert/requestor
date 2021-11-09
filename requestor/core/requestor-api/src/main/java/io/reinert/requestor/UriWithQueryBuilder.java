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
package io.reinert.requestor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.reinert.requestor.uri.Uri;
import io.reinert.requestor.uri.UriCodec;

/**
 * Wraps an already built Uri and provides a query builder to enhance the uri's query.
 *
 * To be used in case there's a built Uri but it's query may be optionally enhanced.
 *
 * @author Danilo Reinert
 */
class UriWithQueryBuilder {

    private Uri uri;
    private Map<String, ArrayList<String>> queryParams;

    public UriWithQueryBuilder(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        ensureUriUpdated();
        return uri;
    }

    public void setQueryParam(String name, String... values) {
        if (name == null) {
            throw new IllegalArgumentException("Query param name cannot be null.");
        }

        if (queryParams == null) {
            queryParams = new HashMap<String, ArrayList<String>>();
        }

        ArrayList<String> valuesList = queryParams.get(name);
        if (valuesList == null) {
            valuesList = new ArrayList<String>();
            queryParams.put(name, valuesList);
        }

        Collections.addAll(valuesList, values);
    }

    private void ensureUriUpdated() {
        if (queryParams != null && !queryParams.isEmpty()) {
            final UriCodec codec = UriCodec.getInstance();
            String addQuery = "";
            String and = "";
            for (String paramName : queryParams.keySet()) {
                for (String paramValue : queryParams.get(paramName)) {
                    addQuery = and + codec.encodeQueryString(paramName) + '=' + codec.encodeQueryString(paramValue);
                    and = "&";
                }
            }
            String newUri = uri.toString();
            newUri += (newUri.contains("?") ? (newUri.endsWith("&") ? "" : "&") : "?") + addQuery;
            uri = Uri.create(newUri);
            queryParams.clear();
        }
    }
}
