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
package io.reinert.requestor.core.header;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HTTP Header.
 *
 * @author Danilo Reinert
 */
public abstract class Header {

    /**
     * Factory method useful to create headers from original response header.
     * <p></p>
     * It should be used by {@link io.reinert.requestor.core.RequestDispatcher} impls when receiving responses.
     *
     * @param name  The original header name
     * @param value The original header value
     *
     * @return  The parsed header
     */
    public static Header fromRawHeader(String name, String value) {
        name = name.toUpperCase();

        if ("ACCEPT".equals(name))
            return new AcceptHeader(parseHeaderValueAsElements(value));

        if ("CONTENT-TYPE".equals(name))
            return new ContentTypeHeader(value);

        if ("LINK".equals(name))
            return new LinkHeader(parseHeaderValueAsElements(value));

        return new SimpleHeader(name, value);
    }

    static List<Element> parseHeaderValueAsElements(String headerValue) {
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

    public abstract String getName();

    public abstract String getValue();

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
