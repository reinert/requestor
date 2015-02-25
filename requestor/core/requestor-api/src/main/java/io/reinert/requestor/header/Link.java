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
 * Represents a hypermedia link according to <a href="https://tools.ietf.org/html/rfc5988">RFC 5988</a>.
 *
 * @author Danilo Reinert
 */
public class Link extends Element.SimpleElement {

    public static final String REL = "rel";
    public static final String TITLE = "title";
    public static final String ANCHOR = "anchor";
    public static final String MEDIA = "media";
    public static final String TYPE = "type";
    public static final String HREF_LANG = "hrefLang";
    public static final String REV = "rev";

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
                if (REL.equals(key)) {
                    rel = value;
                } else if (TITLE.equals(key) && title == null) {
                    title = value;
                } else if ("title*".equals(key)) { // 'title*' has preference over 'title' according to the rfc (5.4)
                    title = value;
                } else if (ANCHOR.equals(key)) {
                    anchor = value;
                } else if (MEDIA.equals(key)) {
                    media = value;
                } else if (TYPE.equals(key)) {
                    type = value;
                } else if (HREF_LANG.equals(key)) {
                    hrefLang = value;
                } else if (REV.equals(key)) {
                    rev = value;
                }
            }
        }
    }

    /**
     * Returns the underlying URI associated with this link.
     *
     * @return  underlying URI as string
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the value associated with the link rel param, or null if this param is not specified.
     *
     * @return  relation types as string or null
     */
    public String getRel() {
        return rel;
    }

    /**
     * Returns the value associated with the link title param, or null if this param is not specified.
     *
     * @return  value of title parameter or null
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the value associated with the link rev param, or null if this param is not specified.
     *
     * @return  value of rev parameter or null
     */
    public String getRev() {
        return rev;
    }

    /**
     * Returns the value associated with the link hreflang param, or null if this param is not specified.
     *
     * @return  value of hreflang parameter or null
     */
    public String getHrefLang() {
        return hrefLang;
    }

    /**
     * Returns the value associated with the link anchor param, or null if this param is not specified.
     *
     * @return  value of anchor parameter or null
     */
    public String getAnchor() {
        return anchor;
    }

    /**
     * Returns the value associated with the link media param, or null if this param is not specified.
     *
     * @return  value of media parameter or null
     */
    public String getMedia() {
        return media;
    }

    /**
     * Returns the value associated with the link type param, or null if this param is not specified.
     *
     * @return  value of type parameter or null
     */
    public String getType() {
        return type;
    }
}
