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

import io.reinert.requestor.Link;

/**
 * Link header element.
 * <p>
 * It parses a single link value from a link header and fills the {@link Link} properties accordingly.
 *
 * @author Danilo Reinert
 */
public class LinkElement extends Element.SimpleElement implements Link {

    protected static final String REL = "rel";
    protected static final String TITLE = "title";
    protected static final String ANCHOR = "anchor";
    protected static final String MEDIA = "media";
    protected static final String TYPE = "type";
    protected static final String HREF_LANG = "hrefLang";
    protected static final String REV = "rev";

    private final String uri;
    private String rel;
    private String title;
    private String rev;
    private String hrefLang;
    private String anchor;
    private String media;
    private String type;

    protected LinkElement(Element element) {
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
    @Override
    public String getUri() {
        return uri;
    }

    /**
     * Returns the value associated with the link rel param, or null if this param is not specified.
     *
     * @return  relation types as string or null
     */
    @Override
    public String getRel() {
        return rel;
    }

    /**
     * Returns the value associated with the link title param, or null if this param is not specified.
     *
     * @return  value of title parameter or null
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Returns the value associated with the link rev param, or null if this param is not specified.
     *
     * @return  value of rev parameter or null
     */
    @Override
    public String getRev() {
        return rev;
    }

    /**
     * Returns the value associated with the link hreflang param, or null if this param is not specified.
     *
     * @return  value of hreflang parameter or null
     */
    @Override
    public String getHrefLang() {
        return hrefLang;
    }

    /**
     * Returns the value associated with the link anchor param, or null if this param is not specified.
     *
     * @return  value of anchor parameter or null
     */
    @Override
    public String getAnchor() {
        return anchor;
    }

    /**
     * Returns the value associated with the link media param, or null if this param is not specified.
     *
     * @return  value of media parameter or null
     */
    @Override
    public String getMedia() {
        return media;
    }

    /**
     * Returns the value associated with the link type param, or null if this param is not specified.
     *
     * @return  value of type parameter or null
     */
    @Override
    public String getType() {
        return type;
    }
}
