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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link io.reinert.requestor.core.uri.UriBuilder}.
 *
 * @author Danilo Reinert
 */
class UriBuilderImpl extends UriBuilder {

    private String scheme;
    private String user;
    private String password;
    private String host;
    private int port;
    private List<String> segments;
    private String fragment;
    private LinkedHashMap<String, Uri.Param> queryParams;
    private LinkedHashMap<String, LinkedHashMap<String, Uri.Param>> matrixParams;

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
            uriBuilder.queryParams = new LinkedHashMap<String, Uri.Param>(uriBuilder.queryParams);
        }
        if (matrixParams != null) {
            uriBuilder.matrixParams = new LinkedHashMap<String, LinkedHashMap<String, Uri.Param>>();
            for (String key : matrixParams.keySet()) {
                uriBuilder.matrixParams.put(key, new LinkedHashMap<String, Uri.Param>(matrixParams.get(key)));
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
        if (path.length() != 0) {
            ensureSegments();

            for (String segment : path.split("/")) {
                if (segment.length() != 0) this.segments.add(segment);
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
            matrixParams = new LinkedHashMap<String, LinkedHashMap<String, Uri.Param>>();
        }

        // At least one segment must exist
        assertNotNull(segments, "There is no segment added to the URI. " +
                "There must be at least one segment added in order to bind matrix parameters");

        String segment = segments.get(segments.size() - 1);

        LinkedHashMap<String, Uri.Param> segmentParams = matrixParams.get(segment);
        if (segmentParams == null) {
            segmentParams = new LinkedHashMap<String, Uri.Param>();
            matrixParams.put(segment, segmentParams);
        }
        segmentParams.put(name, Uri.Param.matrix(name, values));

        return this;
    }

    @Override
    public UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNull(name, "Parameter name cannot be null.");
        assertNotNull(values, "Parameter values cannot be null.");

        if (queryParams == null) {
            queryParams = new LinkedHashMap<String, Uri.Param>();
        }

        queryParams.put(name, Uri.Param.query(name, values));

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

        final List<String> mSegments = uri.getSegments();
        if (mSegments != null) {
            this.segments = null;
            this.matrixParams = null;
            for (String segment : mSegments) {
                segment(segment);
                // Check matrix params for this segment
                final Collection<Uri.Param> mMatrixParams = uri.getMatrixParams(segment);
                if (mMatrixParams != null) {
                    for (Uri.Param param : mMatrixParams) {
                        matrixParam(param.getName(), param.getValues());
                    }
                }
            }
        }

        final int mPort = uri.getPort();
        port(mPort);

        final Collection<Uri.Param> mQueryParams = uri.getQueryParams();
        if (mQueryParams != null) {
            this.queryParams = null;
            for (Uri.Param param : mQueryParams) {
                queryParam(param.getName(), param.getValues());
            }
        }

        final String mFragment = uri.getFragment();
        if (mFragment != null) fragment(mFragment);

        return this;
    }

    @Override
    public Uri build(Object... templateValues) {
        final List<String> templateParams = new ArrayList<String>();
        final List<String> parsedSegments = new ArrayList<String>();
        final LinkedHashMap<String, LinkedHashMap<String, Uri.Param>> parsedMatrixParams =
                matrixParams != null && templateValues.length > 0 ?
                new LinkedHashMap<String, LinkedHashMap<String, Uri.Param>>() : null;

        if (segments != null) {
            for (final String segment : segments) {
                final String parsed = parsePart(templateValues, templateParams, segment);

                // Replace the template segment for the parsed one if necessary
                if (parsedMatrixParams != null && matrixParams.containsKey(segment)) {
                    parsedMatrixParams.put(parsed, matrixParams.get(segment));
                }

                parsedSegments.add(parsed);
            }
        }

        final String parsedFrag = fragment != null ? parsePart(templateValues, templateParams, fragment) : null;

        return new UriImpl(scheme, user, password, host, port, parsedSegments,
                parsedMatrixParams != null ? parsedMatrixParams : matrixParams, queryParams, parsedFrag);
    }

    @Override
    public Uri build(Map<String, ?> values) {
        final List<String> parsedSegments = new ArrayList<String>();
        final LinkedHashMap<String, LinkedHashMap<String, Uri.Param>> parsedMatrixParams =
                matrixParams != null && values != null && values.size() > 0 ?
                        new LinkedHashMap<String, LinkedHashMap<String, Uri.Param>>() : null;

        if (segments != null) {
            for (final String segment : segments) {
                final String parsed = parsePart(values, segment);

                // Replace the template segment for the parsed one if necessary
                if (parsedMatrixParams != null && matrixParams.containsKey(segment)) {
                    parsedMatrixParams.put(parsed, matrixParams.get(segment));
                }

                parsedSegments.add(parsed);
            }
        }

        final String parsedFrag = parsePart(values, fragment);

        return new UriImpl(scheme, user, password, host, port, parsedSegments,
                parsedMatrixParams != null ? parsedMatrixParams : matrixParams, queryParams, parsedFrag);
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
