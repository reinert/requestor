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
package io.reinert.requestor.core.header;

import io.reinert.requestor.core.Link;
import io.reinert.requestor.core.uri.Uri;

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

    private final Uri uri;
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
            uri = Uri.create(rawUri.substring(1, rawUri.length() - 1));
        } else {
            uri = Uri.create(rawUri);
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

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public String getRel() {
        return rel;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getRev() {
        return rev;
    }

    @Override
    public String getHrefLang() {
        return hrefLang;
    }

    @Override
    public String getAnchor() {
        return anchor;
    }

    @Override
    public String getMedia() {
        return media;
    }

    @Override
    public String getType() {
        return type;
    }
}
