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
package io.reinert.requestor.core;

import io.reinert.requestor.core.uri.Uri;

/**
 * Represents a hypermedia link according to <a href="https://tools.ietf.org/html/rfc5988">RFC 5988</a>.
 *
 * @author Danilo Reinert
 */
public interface Link {

    /**
     * Returns the value associated with the link anchor param, or null if this param is not specified.
     *
     * @return  value of anchor parameter or null
     */
    String getAnchor();

    /**
     * Returns the value associated with the link hreflang param, or null if this param is not specified.
     *
     * @return  value of hreflang parameter or null
     */
    String getHrefLang();

    /**
     * Returns the value associated with the link media param, or null if this param is not specified.
     *
     * @return  value of media parameter or null
     */
    String getMedia();

    /**
     * Returns the value associated with the link rel param, or null if this param is not specified.
     *
     * @return  relation types as string or null
     */
    String getRel();

    /**
     * Returns the value associated with the link rev param, or null if this param is not specified.
     *
     * @return  value of rev parameter or null
     */
    String getRev();

    /**
     * Returns the value associated with the link title param, or null if this param is not specified.
     *
     * @return  value of title parameter or null
     */
    String getTitle();

    /**
     * Returns the value associated with the link type param, or null if this param is not specified.
     *
     * @return  value of type parameter or null
     */
    String getType();

    /**
     * Returns the underlying URI associated with this link.
     *
     * @return  underlying URI as string
     */
    Uri getUri();
}
