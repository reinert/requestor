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

import java.util.Map;

/**
 * Full implementation for {@link Uri}.
 *
 * @see UriProxy
 *
 * @author Danilo Reinert
 */
class UriImpl extends Uri {

    private String scheme;
    private String user;
    private String password;
    private String host;
    private int port = -1;
    private String path;
    private String pathEncoded;
    private String[] pathSegments;
    private Map<String, Buckets> matrixParams;
    private String query;
    private String queryEncoded;
    private Buckets queryParams;
    private String fragment;
    private String uriString;

    UriImpl(String scheme, String user, String password, String host, int port, String[] pathSegments,
            Map<String, Buckets> matrixParams, Buckets queryParams, String fragment) {
        this(scheme, user, password, host, port, pathSegments, matrixParams, queryParams, fragment, null);
    }

    // Used only by UriParser which already has the uri stringified.
    UriImpl(String scheme, String user, String password, String host, int port, String[] pathSegments,
            Map<String, Buckets> matrixParams, Buckets queryParams, String fragment, String uriString) {
        // TODO: validate?
        this.scheme = scheme;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.pathSegments = pathSegments;
        this.matrixParams = matrixParams;
        buildPath();
        this.queryParams = queryParams;
        buildQuery();
        this.fragment = fragment;
        this.uriString = uriString;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String[] getSegments() {
        return pathSegments;
    }

    @Override
    public String[] getMatrixParams(String segment) {
        if (matrixParams == null) return null;
        final Buckets buckets = matrixParams.get(segment);
        return buckets != null ? buckets.getKeys() : null;
    }

    @Override
    public String[] getMatrixValues(String segment, String param) {
        final Buckets buckets = matrixParams.get(segment);
        return buckets != null ? buckets.get(param) : null;
    }

    @Override
    public String getFirstMatrixValue(String segment, String param) {
        final Buckets buckets = matrixParams.get(segment);
        final String[] values = buckets != null ? buckets.get(param) : null;
        return values != null ? values[0] : null;
    }

    @Override
    public String getQuery() {
        if (queryParams != null && query == null) buildQuery();
        return query;
    }

    @Override
    public String[] getQueryParams() {
        return queryParams != null ? queryParams.getKeys() : null;
    }

    @Override
    public String[] getQueryValues(String param) {
        return queryParams.get(param);
    }

    @Override
    public String getFirstQueryValue(String param) {
        final String[] values = queryParams.get(param);
        return values != null ? values[0] : null;
    }

    @Override
    public String getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        if (uriString == null) {
            final UriCodec uriCodec = UriCodec.getInstance();
            final StringBuilder uri = new StringBuilder();

            if (scheme != null) {
                uri.append(scheme).append("://");
            }
            if (user != null) {
                uri.append(uriCodec.encode(user));
                if (password != null) {
                    uri.append(':').append(uriCodec.encode(password));
                }
                uri.append('@');
            }

            if (host != null) {
                uri.append(host);
            }

            if (port > 0) {
                uri.append(':').append(port);
            }

            if (path != null) {
                uri.append(pathEncoded);
            }

            if (query != null && query.length() != 0) {
                uri.append('?').append(queryEncoded);
            }

            if (fragment != null) {
                uri.append('#').append(uriCodec.encode(fragment));
            }

            uriString = uri.toString();
        }

        return uriString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final UriImpl uri = (UriImpl) o;

        if (port != uri.port)
            return false;
        if (fragment != null ? !fragment.equals(uri.fragment) : uri.fragment != null)
            return false;
        if (host != null ? !host.equals(uri.host) : uri.host != null)
            return false;
        if (password != null ? !password.equals(uri.password) : uri.password != null)
            return false;
        if (path != null ? !path.equals(uri.path) : uri.path != null)
            return false;
        if (query != null ? !query.equals(uri.query) : uri.query != null)
            return false;
        if (scheme != null ? !scheme.equals(uri.scheme) : uri.scheme != null)
            return false;
        if (user != null ? !user.equals(uri.user) : uri.user != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (fragment != null ? fragment.hashCode() : 0);
        return result;
    }

    private void buildPath() {
        final StringBuilder pathBuilder = new StringBuilder("/");
        final StringBuilder pathEncodedBuilder = new StringBuilder("/");

        if (pathSegments != null && pathSegments.length > 0) {
            for (final String segment : pathSegments) {
                pathBuilder.append(segment);
                pathEncodedBuilder.append(UriCodec.getInstance().encodePathSegment(segment));

                // Check if there are matrix params for this segment
                appendMatrixParams(pathBuilder, pathEncodedBuilder, segment);

                pathBuilder.append('/');
                pathEncodedBuilder.append('/');
            }
            pathBuilder.deleteCharAt(pathBuilder.length() - 1);
            pathEncodedBuilder.deleteCharAt(pathEncodedBuilder.length() - 1);
        }
        path = pathBuilder.toString();
        pathEncoded = pathEncodedBuilder.toString();
    }

    private void appendMatrixParams(StringBuilder pathBuilder, StringBuilder pathEncodedBuilder, String segment) {
        if (matrixParams != null) {
            Buckets segmentParams = matrixParams.get(segment);
            if (segmentParams != null) {
                final UriCodec uriCodec = UriCodec.getInstance();
                String[] params = segmentParams.getKeys();
                for (String param : params) {
                    String[] values = segmentParams.get(param);
                    // Check if the param has values
                    if (values.length == 0) {
                        // Append only the param name without any value
                        pathBuilder.append(';').append(param);
                        pathEncodedBuilder.append(';').append(uriCodec.encodePathSegment(param));
                    } else {
                        // Append the param and its values
                        for (String value : values) {
                            pathBuilder.append(';').append(param);
                            pathEncodedBuilder.append(';').append(uriCodec.encodePathSegment(param));
                            if (value != null) {
                                pathBuilder.append('=').append(value);
                                pathEncodedBuilder.append('=').append(uriCodec.encodePathSegment(value));
                            }
                        }
                    }
                }
            }
        }
    }

    private void buildQuery() {
        final StringBuilder queryBuilder = new StringBuilder();
        final StringBuilder queryEncodedBuilder = new StringBuilder();

        if (queryParams != null && !queryParams.isEmpty()) {
            final UriCodec uriCodec = UriCodec.getInstance();
            String[] params = queryParams.getKeys();
            for (String param : params) {
                final String[] values = queryParams.get(param);
                // Check if the param has values
                if (values.length == 0) {
                    // Append only the param name without any value
                    queryBuilder.append(param).append('&');
                    queryEncodedBuilder.append(uriCodec.encodeQueryString(param)).append('&');
                } else {
                    // Append the param and its values
                    for (String value : values) {
                        queryBuilder.append(param);
                        queryEncodedBuilder.append(uriCodec.encodeQueryString(param));
                        if (value != null) {
                            queryBuilder.append('=').append(value);
                            queryEncodedBuilder.append('=').append(uriCodec.encodeQueryString(value));
                        }
                        queryBuilder.append('&');
                        queryEncodedBuilder.append('&');
                    }
                }
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryEncodedBuilder.deleteCharAt(queryEncodedBuilder.length() - 1);
            query = queryBuilder.toString();
            queryEncoded = queryEncodedBuilder.toString();
        }
    }
}
