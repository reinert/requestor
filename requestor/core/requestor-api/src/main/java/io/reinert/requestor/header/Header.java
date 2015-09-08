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
package io.reinert.requestor.header;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a HTTP Header.
 */
public abstract class Header extends com.google.gwt.http.client.Header {

    /**
     * Factory method useful to create headers from original GWT {@link com.google.gwt.http.client.Response}.
     * <p/>
     * It should be used by {@link io.reinert.requestor.RequestDispatcher} impls when receiving responses.
     *
     * @param rawHeader  The original GWT header
     *
     * @return  The parsed header
     */
    public static Header fromRawHeader(com.google.gwt.http.client.Header rawHeader) {
        final String name = rawHeader.getName().toUpperCase();
        final String value = rawHeader.getValue();

        if ("CONTENT-TYPE".equals(name))
            return new ContentTypeHeader(value);

        if ("LINK".equals(name))
            return new LinkHeader(parseHeaderValue(value));

        return new SimpleHeader(name, value);
    }

    static List<Element> parseHeaderValue(String headerValue) {
        final List<Element> parsedElements = new ArrayList<Element>();
        final List<String> elements = splitEscapingQuotes(',', headerValue);

        for (String e : elements) {
            final Element.Builder builder = new Element.Builder();

            final List<String> params = splitEscapingQuotes(';', e);

            // Parse actual header value element
            final List<String> elementParts = splitEscapingQuotes('=', params.get(0));
            builder.key(elementParts.get(0));
            if (elementParts.size() > 1) builder.value(elementParts.get(1));

            // Parse params
            for (int i = 1; i < params.size(); i++) {
                final List<String> paramParts = splitEscapingQuotes('=', params.get(i));
                if (paramParts.size() > 1) {
                    String key = paramParts.get(0);
                    String value = paramParts.get(1);
                    boolean quoted = false;
                    if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                        value = value.substring(1, value.length() - 1);
                        quoted = true;
                    }
                    builder.addParam(Param.of(key, value, quoted));
                } else {
                    builder.addParam(Param.of(paramParts.get(0)));
                }
            }

            parsedElements.add(builder.build());
        }
        return parsedElements;
    }

    private static List<String> splitEscapingQuotes(char sep, String value) {
        final ArrayList<String> split = new ArrayList<String>();
        boolean quoteOpen = false;
        int startIndex = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"') {
                quoteOpen = !quoteOpen;
            } else if (c == sep && !quoteOpen) {
                split.add(value.substring(startIndex, i).trim());
                startIndex = i + 1;
            }
        }
        split.add(value.substring(startIndex, value.length()).trim());
        return split;
    }

    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }
}
