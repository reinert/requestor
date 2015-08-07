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
package io.reinert.requestor;

import io.reinert.requestor.uri.Uri;

/**
 * Represents a hypermedia link according to <a href="https://tools.ietf.org/html/rfc5988">RFC 5988</a>.
 *
 * @author Danilo Reinert
 */
public interface Link {

    String getAnchor();

    String getHrefLang();

    String getMedia();

    String getRel();

    String getRev();

    String getTitle();

    String getType();

    Uri getUri();
}
