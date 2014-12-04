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
package io.reinert.requestor.uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;

import org.turbogwt.core.collections.LightMap;

/**
 * Default implementation of {@link io.reinert.requestor.uri.UriBuilder}.
 *
 * @author Danilo Reinert
 */
public class UriBuilderImpl extends UriBuilder {

    private MultivaluedParamComposition strategy = MultivaluedParamComposition.REPEATED_PARAM;
    private String scheme;
    private String user;
    private String password;
    private String host;
    private Integer port;
    private JsArrayString segments;
    private String fragment;
    private Map<String, Object[]> queryParams;
    private Map<String, Map<String, Object[]>> matrixParams;

    @Override
    public UriBuilder multivaluedParamComposition(MultivaluedParamComposition strategy)
            throws IllegalArgumentException {
        assertNotNull(strategy, "MultivaluedParamComposition strategy cannot be null.");
        this.strategy = strategy;
        return this;
    }

    @Override
    public UriBuilder scheme(String scheme) throws IllegalArgumentException {
        // TODO: check scheme validity
        this.scheme = scheme;
        return this;
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
    public UriBuilder host(String host) throws IllegalArgumentException {
        // TODO: check host validity
        this.host = host;
        return this;
    }

    @Override
    public UriBuilder port(int port) throws IllegalArgumentException {
        if (port > -1) {
            this.port = port;
        } else {
            this.port = null;
        }
        return this;
    }

    @Override
    public UriBuilder path(String path) {
        assertNotNull(path, "Path cannot be null.");
        if (!path.isEmpty()) {
            String[] splittedSegments = path.split("/");
            ensureSegments();
            for (String segment : splittedSegments) {
                if (!segment.isEmpty())
                    this.segments.push(segment);
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
            this.segments.push(segment);
        }
        return this;
    }

    @Override
    public UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNullOrEmpty(name, "Parameter name cannot be null or empty.", false);
        assertNotNull(values, "Parameter values cannot be null.");

        if (matrixParams == null) {
            matrixParams = GWT.create(LightMap.class);
        }

        // TODO: validate this assertion
        assertNotNull(segments, "There is no segment added to the URI. " +
                "There must be at least one segment added in order to bind matrix parameters");

        String segment = segments.get(segments.length() - 1);

        Map<String, Object[]> segmentParams = matrixParams.get(segment);
        if (segmentParams == null) {
            segmentParams = GWT.create(LightMap.class);
            matrixParams.put(segment, segmentParams);
        }
        // TODO: instead of setting the array, incrementally add to an existing one?
        segmentParams.put(name, values);

        return this;
    }

    @Override
    public UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNull(name, "Parameter name cannot be null.");
        assertNotNull(values, "Parameter values cannot be null.");
        if (queryParams == null)
            queryParams = GWT.create(LightMap.class);
        queryParams.put(name, values);
        return this;
    }

    @Override
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    public Uri build(Object... values) {
        final String encScheme = encodePart(scheme);
        final String encUser = encodePart(user);
        final String encPassword = encodePart(password);
        final String encHost = encodePart(host);

        List<String> templateParams = new ArrayList<String>();

        StringBuilder pathBuilder = new StringBuilder();
        if (segments != null) {
            for (int i = 0; i < segments.length(); i++) {
                final String segment = segments.get(i);
                final String parsed = parsePart(values, templateParams, segment);
                pathBuilder.append(URL.encodePathSegment(parsed));

                // Check if there are matrix params for this segment
                if (matrixParams != null) {
                    Map<String, Object[]> segmentParams = matrixParams.get(segment);
                    if (segmentParams != null) {
                        pathBuilder.append(";");
                        Set<String> params = segmentParams.keySet();
                        for (String param : params) {
                            pathBuilder.append(strategy.asUriPart(";", param, segmentParams.get(param))).append(';');
                        }
                        pathBuilder.deleteCharAt(pathBuilder.length() - 1);
                    }
                }
                pathBuilder.append('/');
            }
            pathBuilder.deleteCharAt(pathBuilder.length() - 1);
        }
        final String encPath = pathBuilder.toString();

        StringBuilder queryBuilder = null;
        if (queryParams != null) {
            queryBuilder = new StringBuilder();
            Set<String> params = queryParams.keySet();
            for (String param : params) {
                queryBuilder.append(strategy.asUriPart("&", param, queryParams.get(param))).append('&');
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        }
        final String encQuery = queryBuilder != null ? queryBuilder.toString() : null;

        final String encFragment = fragment != null ? encodePart(parsePart(values, templateParams, fragment)) : null;

        return new Uri(encScheme, encUser, encPassword, encHost, port, encPath, encQuery, encFragment);
    }

    private String encodePart(String segment) {
        return segment != null ? URL.encodePathSegment(segment) : null;
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
                        throw new UriBuilderException("The supplied values are not enough to replace the existing " +
                                "template params");
                    // Add template param
                    i = templateParams.size();
                    templateParams.add(param);
                }
                final String value = values[i].toString();
                segment = segment.substring(0, cursor) + value + segment.substring(closingBracket + 1);
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
        if (this.segments == null)
            this.segments = (JsArrayString) JsArrayString.createArray();
    }
}
