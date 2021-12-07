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
package io.reinert.requestor.core.uri;

/**
 * A fast and lightweight Uri implementation from a stringified uri.
 * <p>
 *
 * {@link Uri#create(String)} creates a instance of this proxy at first because most often the only required method
 * will be #toString, which is already available.
 * <p>
 *
 * Whenever a part method is requested, this proxy instantiates a UriImpl and delegates the execution to the full impl.
 *
 * @author Danilo Reinert
 */
class UriProxy extends Uri {

    private final String uriString;
    private UriImpl impl;

    public UriProxy(String uriString) {
        if (uriString == null || uriString.isEmpty())
            throw new IllegalArgumentException("Uri string cannot be null nor empty.");
        // TODO: validate?
        this.uriString = uriString;
    }

    @Override
    public String getScheme() {
        return ensureImpl().getScheme();
    }

    @Override
    public String getUser() {
        return ensureImpl().getUser();
    }

    @Override
    public String getPassword() {
        return ensureImpl().getPassword();
    }

    @Override
    public String getHost() {
        return ensureImpl().getHost();
    }

    @Override
    public int getPort() {
        return ensureImpl().getPort();
    }

    @Override
    public String getPath() {
        return ensureImpl().getPath();
    }

    @Override
    public String[] getSegments() {
        return ensureImpl().getSegments();
    }

    @Override
    public String[] getMatrixParams(String segment) {
        return ensureImpl().getMatrixParams(segment);
    }

    @Override
    public String[] getMatrixValues(String segment, String param) {
        return ensureImpl().getMatrixValues(segment, param);
    }

    @Override
    public String getFirstMatrixValue(String segment, String param) {
        return ensureImpl().getFirstMatrixValue(segment, param);
    }

    @Override
    public String getQuery() {
        return ensureImpl().getQuery();
    }

    @Override
    public String[] getQueryParams() {
        return ensureImpl().getQueryParams();
    }

    @Override
    public String[] getQueryValues(String param) {
        return ensureImpl().getQueryValues(param);
    }

    @Override
    public String getFirstQueryValue(String param) {
        return ensureImpl().getFirstQueryValue(param);
    }

    @Override
    public String getFragment() {
        return ensureImpl().getFragment();
    }

    @Override
    public String toString() {
        return uriString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UriProxy))
            return false;

        final UriProxy uriProxy = (UriProxy) o;

        return uriString.equals(uriProxy.uriString);
    }

    @Override
    public int hashCode() {
        return uriString.hashCode();
    }

    public UriImpl ensureImpl() {
        if (impl == null) {
            final UriParser parser = getParser();
            parser.parse(uriString);
            impl = parser.getUri();
        }
        return impl;
    }
}
