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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;

/**
 * Parses a URI.
 */
public class UriParser {

    private Uri uri;
    private UrlCodec urlCodec;
    private String scheme;
    private String user;
    private String password;
    private String host;
    private int port = -1;
    private String[] segments;
    private Map<String, Buckets> matrixParams;
    private Buckets queryParams;
    private String fragment;
    private String uriString;

    private UriParser() {
        this.urlCodec = UrlCodec.getInstance();
    }

    public Uri getUri() {
        if (uri == null)
            uri = new Uri(scheme, user, password, host, port, segments, matrixParams, queryParams, fragment, uriString);
        return uri;
    }

    public static UriParser newInstance() {
        return new UriParser();
    }

    public UriParser parse(String uri) {
        if (uri == null || uri.isEmpty())
            throw new UriParseException("The uri argument cannot be null or empty");

        resetParser();

        uriString = uri;

        String parsedUri = uri;
        String query;
        int pos;

        // Extract fragment
        pos = parsedUri.indexOf('#');
        if (pos > -1) {
            // Check last char
            fragment = parsedUri.length() - pos == 1 ? null : urlCodec.decode(parsedUri.substring(pos + 1));
            // Remove parsed part from parsing uri
            parsedUri = parsedUri.substring(0, pos);
        }

        // Extract query
        pos = parsedUri.indexOf('?');
        if (pos > -1) {
            // Check last char
            query = parsedUri.length() - pos == 1 ? null : parsedUri.substring(pos + 1);
            parseQuery(query);
            // Remove parsed part from parsing uri
            parsedUri = parsedUri.substring(0, pos);
        }

        // Extract protocol
        if (parsedUri.length() > 2 && parsedUri.substring(0, 2).equals("//")) {
            // Relative-Scheme
            scheme = null;
            parsedUri = parsedUri.substring(2);
            // Extract "user:pass@host:port"
            parsedUri = parseAuthority(parsedUri);
        } else {
            pos = parsedUri.indexOf("://");
            if (pos > -1) {
                scheme = pos > 0 ? parsedUri.substring(0, pos) : null;
                parsedUri = parsedUri.substring(pos + 3);

                // Extract "user:pass@host:port"
                parsedUri = parseAuthority(parsedUri);
            }
        }

        // The left part must be the path
        final List<String> parsedSegments = new ArrayList<String>();
        final String[] rawSegments = parsedUri.split("/");
        for (String segment : rawSegments) {
            if (!segment.isEmpty()) {
                String[] matrixParts = segment.split(";");
                final String parsedSegment = urlCodec.decode(matrixParts[0]);
                parsedSegments.add(parsedSegment);
                if (matrixParts.length > 1) {
                    if (matrixParams == null) {
                        matrixParams = new HashMap<String, Buckets>();
                    }
                    final Buckets buckets = GWT.create(Buckets.class);
                    matrixParams.put(parsedSegment, buckets);
                    for (int i = 1; i < matrixParts.length; i++) {
                        String[] matrixElements = matrixParts[i].split("=");
                        if (matrixElements.length == 1) {
                            buckets.add(urlCodec.decode(matrixElements[0]), null);
                        } else {
                            buckets.add(urlCodec.decode(matrixElements[0]), urlCodec.decode(matrixElements[1]));
                        }
                    }
                }
            }
        }
        this.segments = parsedSegments.toArray(new String[parsedSegments.size()]);

        return this;
    }

    private String parseAuthority(String uri) {
        uri = parseUserInfo(uri);
        return parseHost(uri);
    }

    private String parseUserInfo(String uri) {
        // Extract username:password
        int pathDivider = uri.indexOf('/');
        int pos = uri.lastIndexOf('@', pathDivider > -1 ? pathDivider : uri.length() - 1);

        // authority@ must come before /path
        if (pos > -1 && (pathDivider == -1 || pos < pathDivider)) {
            String[] t = uri.substring(0, pos).split(":");
            user = !t[0].isEmpty() ? urlCodec.decode(t[0]) : null;
            password = t.length > 1 && !t[1].isEmpty() ? urlCodec.decode(t[1]) : null;
            uri = uri.substring(pos + 1);
        } else {
            user = null;
            password = null;
        }

        return uri;
    }

    private String parseHost(String uri) {
        // Extract host:port
        int pos = uri.indexOf('/');

        if (pos == -1) pos = uri.length();

        String[] authority = uri.substring(0, pos).split(":");
        host = !authority[0].isEmpty() ? authority[0] : null;
        port = authority.length > 1 && !authority[1].isEmpty() ? Integer.parseInt(authority[1]) : -1;

        return pos == uri.length() ? "/" : uri.substring(pos);
    }

    private void parseQuery(String query) {
        // throw out the funky business - "?"[name"="value"&"]+
        query = query.replaceAll("/&+/g", "&").replaceAll("/^\\?*&*|&+$/g", "");

        if (query.isEmpty())
            return;

        queryParams = GWT.create(Buckets.class);
        String[] p, pairs = query.split("&");
        String name, value;
        for (final String pair : pairs) {
            p = pair.split("=");
            name = urlCodec.decodeQueryString(p[0]);
            // no "=" is null according to http://dvcs.w3.org/hg/url/raw-file/tip/Overview.html#collect-url-parameters
            value = p.length > 1 && !p[1].isEmpty() ? urlCodec.decodeQueryString(p[1]) : null;
            queryParams.add(name, value);
        }
    }

    private void resetParser() {
        uri = null;
        scheme = null;
        user = null;
        password = null;
        host = null;
        port = -1;
        segments = null;
        matrixParams = null;
        queryParams = null;
        fragment = null;
        uriString = null;
    }
}
