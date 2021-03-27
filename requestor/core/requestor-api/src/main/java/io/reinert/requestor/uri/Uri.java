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
package io.reinert.requestor.uri;

/**
 * Represents a URI.
 *
 * @author Danilo Reinert
 */
public abstract class Uri {

    private static UriParser PARSER;

    public static Uri copy(Uri uri) {
        return create(uri.toString());
    }

    public static Uri create(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("Uri cannot be null nor empty.");
        }

        return new UriProxy(uri);
    }

    static UriParser getParser() {
        if (PARSER == null) {
            PARSER = UriParser.newInstance();
        }

        return PARSER;
    }

    public abstract String getScheme();

    public abstract String getUser();

    public abstract String getPassword();

    public abstract String getHost();

    public abstract int getPort();

    public abstract String getPath();

    public abstract String[] getSegments();

    public abstract String[] getMatrixParams(String segment);

    public abstract String[] getMatrixValues(String segment, String param);

    public abstract String getFirstMatrixValue(String segment, String param);

    public abstract String getQuery();

    public abstract String[] getQueryParams();

    public abstract String[] getQueryValues(String param);

    public abstract String getFirstQueryValue(String param);

    public abstract String getFragment();
}
