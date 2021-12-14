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

import java.util.Collection;
import java.util.List;

/**
 * <p>A fast and lightweight Uri implementation from a stringified uri.</p>
 *
 *
 * <p>{@link Uri#create(String)} creates a instance of this proxy at first because most often the only required method
 * will be #toString, which is already available.</p>
 *
 *
 * <p>Whenever a part method is requested, this proxy instantiates a UriImpl and delegates the execution to the full
 * impl.</p>
 *
 * @author Danilo Reinert
 */
class UriProxy extends Uri {

    private final String uriString;
    private UriImpl impl;

    public UriProxy(String uriString) {
        if (uriString == null || uriString.length() == 0)
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
    public List<String> getSegments() {
        return ensureImpl().getSegments();
    }

    @Override
    public Collection<Param> getMatrixParams(String segment) {
        return ensureImpl().getMatrixParams(segment);
    }

    @Override
    public Param getMatrixParam(String segment, String paramName) {
        return ensureImpl().getMatrixParam(segment, paramName);
    }

    @Override
    public String getQuery() {
        return ensureImpl().getQuery();
    }

    @Override
    public Collection<Param> getQueryParams() {
        return ensureImpl().getQueryParams();
    }

    @Override
    public Param getQueryParam(String paramName) {
        return ensureImpl().getQueryParam(paramName);
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
            final UriParser parser = UriParser.newInstance();
            parser.parse(uriString);
            impl = parser.getUri();
        }
        return impl;
    }
}
