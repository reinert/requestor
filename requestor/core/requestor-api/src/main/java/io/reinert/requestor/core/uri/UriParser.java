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
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Parses a URI.
 *
 * @author Danilo Reinert
 */
public class UriParser {

    private UriImpl uri;
    private String scheme;
    private String user;
    private String password;
    private String host;
    private int port = -1;
    private List<String> segments;
    private LinkedHashMap<String, LinkedHashMap<String, Uri.Param>> matrixParams;
    private LinkedHashMap<String, Uri.Param> queryParams;
    private String fragment;
    private String uriString;

    private UriParser() {
    }

    public UriImpl getUri() {
        if (uri == null) {
            uri = new UriImpl(scheme, user, password, host, port, segments, matrixParams, queryParams, fragment,
                    uriString);
        }

        return uri;
    }

    public static UriParser newInstance() {
        return new UriParser();
    }

    public UriParser parse(String uri) {
        if (uri == null || uri.length() == 0) {
            throw new UriParseException("The uri argument cannot be null or empty");
        }

        resetParser();

        uriString = uri;

        final UriCodec uriCodec = UriCodec.getInstance();

        String parsedUri = uri;
        String query;
        int pos;

        // Extract fragment
        pos = parsedUri.indexOf('#');
        if (pos > -1) {
            // Check last char
            fragment = parsedUri.length() - pos == 1 ? null : uriCodec.decode(parsedUri.substring(pos + 1));
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
            if (segment.length() != 0) {
                String[] matrixParts = segment.split(";");
                final String parsedSegment = uriCodec.decode(matrixParts[0]);
                parsedSegments.add(parsedSegment);
                if (matrixParts.length > 1) {
                    if (matrixParams == null) {
                        matrixParams = new LinkedHashMap<String, LinkedHashMap<String, Uri.Param>>();
                    }
                    final LinkedHashMap<String, Uri.Param> segmentParams = new LinkedHashMap<String, Uri.Param>();
                    matrixParams.put(parsedSegment, segmentParams);
                    for (int i = 1; i < matrixParts.length; i++) {
                        String[] matrixElements = matrixParts[i].split("=");
                        String decodedName = uriCodec.decode(matrixElements[0]);
                        Uri.Param param = segmentParams.get(decodedName);
                        if (param == null) {
                            segmentParams.put(decodedName, Uri.Param.matrix(decodedName, matrixElements.length == 1 ?
                                    "" : uriCodec.decode(matrixElements[1])));
                        } else {
                            Object[] valuesArray = param.getValues().toArray(new Object[param.getValues().size() + 1]);
                            valuesArray[valuesArray.length - 1] = matrixElements.length == 1 ?
                                    "" : uriCodec.decode(matrixElements[1]);
                            segmentParams.put(decodedName, Uri.Param.matrix(decodedName, valuesArray));
                        }
                    }
                }
            }
        }
        this.segments = parsedSegments;

        return this;
    }

    private String parseAuthority(String uri) {
        return parseHost(parseUserInfo(uri));
    }

    private String parseUserInfo(String uri) {
        final UriCodec uriCodec = UriCodec.getInstance();

        // Extract username:password
        int pathDivider = uri.indexOf('/');
        int pos = uri.lastIndexOf('@', pathDivider > -1 ? pathDivider : uri.length() - 1);

        // authority@ must come before /path
        if (pos > -1 && (pathDivider == -1 || pos < pathDivider)) {
            String[] t = uri.substring(0, pos).split(":");
            user = t[0].length() != 0 ? uriCodec.decode(t[0]) : null;
            password = t.length > 1 && t[1].length() != 0 ? uriCodec.decode(t[1]) : null;
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
        host = authority[0].length() != 0 ? authority[0] : null;
        port = authority.length > 1 && authority[1].length() != 0 ? Integer.parseInt(authority[1]) : -1;

        return pos == uri.length() ? "/" : uri.substring(pos);
    }

    private void parseQuery(String query) {
        final UriCodec uriCodec = UriCodec.getInstance();

        // throw out the funky business - "?"[name"="value"&"]+
        query = query.replaceAll("/&+/g", "&").replaceAll("/^\\?*&*|&+$/g", "");

        if (query.length() == 0)
            return;

        queryParams = new LinkedHashMap<String, Uri.Param>();
        String[] pairs = query.split("&");
        for (final String pair : pairs) {
            String[] p = pair.split("=");
            String decodedName = uriCodec.decodeQueryString(p[0]);
            Uri.Param param = queryParams.get(decodedName);
            if (param == null) {
                // no "=" is null according http://dvcs.w3.org/hg/url/raw-file/tip/Overview.html#collect-url-parameters
                queryParams.put(decodedName, Uri.Param.query(decodedName, p.length == 1 ?
                        "" : uriCodec.decodeQueryString(p[1])));
            } else {
                Object[] valuesArray = param.getValues().toArray(new Object[param.getValues().size() + 1]);
                valuesArray[valuesArray.length - 1] = p.length == 1 ?
                        "" : uriCodec.decodeQueryString(p[1]);
                queryParams.put(decodedName, Uri.Param.query(decodedName, valuesArray));
            }
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
