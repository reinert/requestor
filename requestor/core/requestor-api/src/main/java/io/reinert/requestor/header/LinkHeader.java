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
package io.reinert.requestor.header;

/**
 * The HTTP Link header.
 * <p/>
 * It's a multivalued header that provides access to the attributes related by official spec.
 *
 * @author Danilo Reinert
 */
public class LinkHeader extends MultivaluedHeader {

    public static class Link extends Element.SimpleElement {

        private final String uri;
        private String rel;
        private String title;
        private String rev;
        private String hrefLang;
        private String anchor;
        private String media;
        private String type;

        Link(Element element) {
            super(element.getElement(), element.getParams());

            final String rawUri = element.getElement();
            if (rawUri.charAt(0) == '<') {
                uri = rawUri.substring(1, rawUri.length() - 1);
            } else {
                uri = rawUri;
            }

            for (Param param : element.getParams()) {
                if (param instanceof Param.KeyValueParam) {
                    Param.KeyValueParam kvParam = (Param.KeyValueParam) param;
                    final String key = kvParam.getKey().toLowerCase();
                    final String value = kvParam.getValue();
                    if ("rel".equals(key)) {
                        rel = value;
                    } else if ("title".equals(key) && title == null) {
                        title = value;
                    } else if ("title*".equals(key)) {
                        title = value;
                    } else if ("anchor".equals(key)) {
                        anchor = value;
                    } else if ("media".equals(key)) {
                        media = value;
                    } else if ("type".equals(key)) {
                        type = value;
                    } else if ("hrefLang".equals(key)) {
                        hrefLang = value;
                    } else if ("rev".equals(key)) {
                        rev = value;
                    }
                }
            }
        }

        public String getUri() {
            return uri;
        }

        public String getRel() {
            return rel;
        }

        public String getTitle() {
            return title;
        }

        public String getRev() {
            return rev;
        }

        public String getHrefLang() {
            return hrefLang;
        }

        public String getAnchor() {
            return anchor;
        }

        public String getMedia() {
            return media;
        }

        public String getType() {
            return type;
        }
    }

    public LinkHeader(Element... elements) {
        super("Link", decorate(elements));
    }

    public Link[] getLinks() {
        return (Link[]) getElements();
    }

    private static Element[] decorate(Element[] elements) {
        Link[] links = new Link[elements.length];
        for (int i = 0; i < elements.length; i++) {
            links[i] = new Link(elements[i]);
        }
        return links;
    }

}
