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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link io.reinert.requestor.core.uri.UriBuilder}.
 *
 * @author Danilo Reinert
 */
public class UriBuilderImpl extends UriBuilder {

    private String scheme;
    private String user;
    private String password;
    private String host;
    private int port;
    private List<String> segments;
    private String fragment;
    private Buckets queryParams;
    private Map<String, Buckets> matrixParams;

    @Override
    public UriBuilder scheme(String scheme) throws IllegalArgumentException {
        // TODO: check scheme validity
        this.scheme = scheme;

        return this;
    }

    @Override
    public UriBuilder clone() {
        final UriBuilderImpl uriBuilder = new UriBuilderImpl();
        uriBuilder.scheme = scheme;
        uriBuilder.user = user;
        uriBuilder.password = password;
        uriBuilder.host = host;
        uriBuilder.port = port;
        if (segments != null) {
            uriBuilder.segments = new ArrayList<String>(segments);
        }
        uriBuilder.fragment = fragment;
        if (queryParams != null) {
            uriBuilder.queryParams = queryParams.copy();
        }
        if (matrixParams != null) {
            uriBuilder.matrixParams = new HashMap<String, Buckets>();
            for (String key : matrixParams.keySet()) {
                uriBuilder.matrixParams.put(key, matrixParams.get(key).copy());
            }
        }

        return uriBuilder;
    }

    @Override
    public UriBuilder user(String user) {
        if (user == null) {
            this.user = null;
            this.password = null;
        } else {
            this.user = user;
        }

        return this;
    }

    @Override
    public UriBuilder password(String password) {
        this.password = password;

        return this;
    }

    @Override
    public UriBuilder host(String host) {
        // TODO: check host validity
        this.host = host;

        return this;
    }

    @Override
    public UriBuilder port(int port) {
        this.port = port < 0 ? -1 : port;

        return this;
    }

    @Override
    public UriBuilder path(String path) {
        assertNotNull(path, "Path cannot be null.");

        // FIXME: must overwrite existing path and associate matrix parameters
        if (!path.isEmpty()) {
            ensureSegments();

            for (String segment : path.split("/")) {
                if (!segment.isEmpty()) this.segments.add(segment);
            }
        }

        return this;
    }

    @Override
    public UriBuilder segment(Object... segments) throws IllegalArgumentException {
        assertNotNull(segments, "Segments cannot be null.");

        ensureSegments();

        for (Object o : segments) {
            String segment = o.toString();
            assertNotNullOrEmpty(segment, "Segment cannot be null or empty.", false);
            this.segments.add(segment);
        }

        return this;
    }

    @Override
    public UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNullOrEmpty(name, "Parameter name cannot be null or empty.", false);
        assertNotNull(values, "Parameter values cannot be null.");

        if (matrixParams == null) {
            matrixParams = new HashMap<String, Buckets>();
        }

        // At least one segment must exist
        assertNotNull(segments, "There is no segment added to the URI. " +
                "There must be at least one segment added in order to bind matrix parameters");

        String segment = segments.get(segments.size() - 1);

        Buckets segmentParams = matrixParams.get(segment);
        if (segmentParams == null) {
            segmentParams = Buckets.Factory.newBuckets();
            matrixParams.put(segment, segmentParams);
        }
        for (Object value : values) {
            segmentParams.add(name, value != null ? value.toString() : null);
        }

        return this;
    }

    @Override
    public UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNull(name, "Parameter name cannot be null.");
        assertNotNull(values, "Parameter values cannot be null.");

        if (queryParams == null) queryParams = Buckets.Factory.newBuckets();
        for (Object value : values) {
            queryParams.add(name, value != null ? value.toString() : null);
        }

        return this;
    }

    @Override
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;

        return this;
    }

    @Override
    public UriBuilder uri(Uri uri) throws IllegalArgumentException {
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null.");
        }

        final String mScheme = uri.getScheme();
        if (mScheme != null) scheme(mScheme);

        final String mUser = uri.getUser();
        if (mUser != null) user(mUser);

        final String mPassword = uri.getPassword();
        if (mPassword != null) password(mPassword);

        final String mHost = uri.getHost();
        if (mHost != null) host(mHost);

        final String[] mSegments = uri.getSegments();
        if (mSegments != null) {
            this.segments = null;
            this.matrixParams = null;
            for (String segment : mSegments) {
                segment(segment);
                // Check matrix params for this segment
                final String[] mMatrixParams = uri.getMatrixParams(segment);
                if (mMatrixParams != null) {
                    for (String param : mMatrixParams) {
                        matrixParam(param, (Object[]) uri.getMatrixValues(segment, param));
                    }
                }
            }
        }

        final int mPort = uri.getPort();
        port(mPort);

        final String[] mQueryParams = uri.getQueryParams();
        if (mQueryParams != null) {
            this.queryParams = null;
            for (String param : mQueryParams) {
                queryParam(param, (Object[]) uri.getQueryValues(param));
            }
        }

        final String mFragment = uri.getFragment();
        if (mFragment != null) fragment(mFragment);

        return this;
    }

    @Override
    public Uri build(Object... templateValues) {
        List<String> templateParams = new ArrayList<String>();

        if (segments != null) {
            for (int i = 0; i < segments.size(); i++) {
                final String segment = segments.get(i);
                final String parsed = parsePart(templateValues, templateParams, segment);

                // Replace the template segment for the parsed one if necessary
                if (matrixParams != null && matrixParams.containsKey(segment) && segment.contains("{")) {
                    final Buckets segmentMatrixParams = matrixParams.remove(segment);
                    matrixParams.put(parsed, segmentMatrixParams);
                }

                segments.set(i, parsed);
            }
        }

        final String parsedFrag = fragment != null ? parsePart(templateValues, templateParams, fragment) : null;
        final String[] pathSegments = segments != null ? segments.toArray(new String[segments.size()]) : null;

        return new UriImpl(scheme, user, password, host, port, pathSegments, matrixParams, queryParams, parsedFrag);
    }

    @Override
    public Uri build(Map<String, ?> values) {
        if (segments != null) {
            for (int i = 0; i < segments.size(); i++) {
                final String segment = segments.get(i);
                final String parsed = parsePart(values, segment);

                // Replace the template segment for the parsed one if necessary
                if (matrixParams != null && matrixParams.containsKey(segment) && segment.contains("{")) {
                    final Buckets segmentMatrixParams = matrixParams.remove(segment);
                    matrixParams.put(parsed, segmentMatrixParams);
                }

                segments.set(i, parsed);
            }
        }

        final String parsedFrag = parsePart(values, fragment);
        final String[] pathSegments = segments != null ? segments.toArray(new String[segments.size()]) : null;

        return new UriImpl(scheme, user, password, host, port, pathSegments, matrixParams, queryParams, parsedFrag);
    }

    private String parsePart(Object[] values, List<String> templateParams, String segment) {
        int cursor = segment.indexOf("{");
        while (cursor > -1) {
            int closingBracket = segment.indexOf("}", cursor);
            if (closingBracket > -1) {
                final String param = segment.substring(cursor + 1, closingBracket);
                int i = templateParams.indexOf(param);
                if (i == -1) {
                    // Check if has more template values
                    if (values.length < templateParams.size() + 1)
                        throw new IllegalArgumentException("Uri could no be built: The supplied values are not enough "
                                + "to replace the existing template params.");
                    // Add template param
                    i = templateParams.size();
                    templateParams.add(param);
                }
                final Object o = values[i];
                if (o == null)
                    throw new IllegalArgumentException("Uri could not be built: Null values are not allowed.");
                final String value = o.toString();
                segment = segment.substring(0, cursor) + value + segment.substring(closingBracket + 1);
                cursor = segment.indexOf("{", closingBracket + 1);
            } else {
                cursor = -1;
            }
        }

        return segment;
    }

    private String parsePart(Map<String, ?> templateValues, String segment) {
        int cursor = segment.indexOf("{");
        while (cursor > -1) {
            int closingBracket = segment.indexOf("}", cursor);
            if (closingBracket > -1) {
                final String param = segment.substring(cursor + 1, closingBracket);
                final Object value = templateValues.get(param);

                if (value == null)
                    throw new IllegalArgumentException("Uri could no be built: The template param '" + param + "' " +
                            "could not be resolved.");

                segment = segment.substring(0, cursor) + value.toString() + segment.substring(closingBracket + 1);
                cursor = segment.indexOf("{", closingBracket + 1);
            } else {
                cursor = -1;
            }
        }

        return segment;
    }

    /**
     * Assert that the value is not null.
     *
     * @param value   the value
     * @param message the message to include with any exceptions
     *
     * @throws IllegalArgumentException if value is null
     */
    private void assertNotNull(Object value, String message) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the value is not null or empty.
     *
     * @param value   the value
     * @param message the message to include with any exceptions
     * @param isState if true, throw a state exception instead
     *
     * @throws IllegalArgumentException if value is null
     * @throws IllegalStateException    if value is null and isState is true
     */
    private void assertNotNullOrEmpty(String value, String message, boolean isState) throws IllegalArgumentException {
        if (value == null || value.length() == 0) {
            if (isState) {
                throw new IllegalStateException(message);
            } else {
                throw new IllegalArgumentException(message);
            }
        }
    }

    private void ensureSegments() {
        if (this.segments == null) {
            this.segments = new ArrayList<String>();
        }
    }
}
