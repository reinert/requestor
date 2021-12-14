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
package io.reinert.requestor.core.uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a URI.
 *
 * @author Danilo Reinert
 */
public abstract class Uri {

    public static class Param {
        private final boolean isQuery;
        private final char separator;
        private final String name;
        private final List<String> values;

        private Param(boolean isQuery, String name, Object... values) {
            this.isQuery = isQuery;
            this.separator = isQuery ? '&' : ';';
            this.name = name;

            switch (values.length) {
                case 0:
                    this.values = Collections.emptyList();
                    break;
                case 1:
                    this.values = Collections.singletonList(values[0].toString());
                    break;
                default:
                    List<String> valuesStr = new ArrayList<String>(values.length);
                    for (Object v : values) valuesStr.add(v.toString());
                    this.values = Collections.unmodifiableList(valuesStr);
            }
        }

        static Param matrix(String name, Object... values) {
            return new Param(false, name, values);
        }

        static Param query(String name, Object... values) {
            return new Param(true, name, values);
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return values.size() == 0 ? "" : values.get(0);
        }

        public List<String> getValues() {
            return values;
        }

        protected char getSeparator() {
            return separator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Param param = (Param) o;

            if (isQuery != param.isQuery) return false;
            if (!name.equals(param.name)) return false;
            return values.equals(param.values);
        }

        @Override
        public int hashCode() {
            int result = (isQuery ? 1 : 0);
            result = 31 * result + name.hashCode();
            result = 31 * result + values.hashCode();
            return result;
        }

        @Override
        public String toString() {
            final UriCodec uriCodec = UriCodec.getInstance();
            final String encodedName = isQuery ? uriCodec.encodeQueryString(name) : uriCodec.encodePathSegment(name);
            final StringBuilder sb = new StringBuilder();
            for (String value : values) {
                sb.append(encodedName)
                        .append('=')
                        .append(isQuery ? uriCodec.encodeQueryString(value) : uriCodec.encodePathSegment(value))
                        .append(separator);
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    public static Uri copy(Uri uri) {
        return create(uri.toString());
    }

    public static Uri create(String uri) {
        if (uri == null || uri.length() == 0) {
            throw new IllegalArgumentException("Uri cannot be null nor empty.");
        }

        return new UriProxy(uri);
    }

    static UriParser getParser() {
        return UriParser.newInstance();
    }

    public abstract String getScheme();

    public abstract String getUser();

    public abstract String getPassword();

    public abstract String getHost();

    public abstract int getPort();

    public abstract String getPath();

    public abstract List<String> getSegments();

    public abstract Collection<Param> getMatrixParams(String segment);

    public abstract Uri.Param getMatrixParam(String segment, String paramName);

    public abstract String getQuery();

    public abstract Collection<Uri.Param> getQueryParams();

    public abstract Uri.Param getQueryParam(String paramName);

    public abstract String getFragment();
}
