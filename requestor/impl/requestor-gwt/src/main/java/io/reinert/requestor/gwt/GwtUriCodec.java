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
package io.reinert.requestor.gwt;

import com.google.gwt.http.client.URL;

import io.reinert.requestor.core.uri.UriCodec;

/**
 * Default UriCodec impl delegating to {@link URL} class.
 */
class GwtUriCodec extends UriCodec {

    @Override
    public String decode(String encodedURL) {
        return URL.decode(encodedURL);
    }

    @Override
    public String decodePathSegment(String encodedURLComponent) {
        return URL.decodePathSegment(encodedURLComponent);
    }

    @Override
    public String decodeQueryString(String encodedURLComponent) {
        return URL.decodeQueryString(encodedURLComponent);
    }

    @Override
    public String encode(String decodedURL) {
        return URL.encode(decodedURL);
    }

    @Override
    public String encodePathSegment(String decodedURLComponent) {
        return URL.encodePathSegment(decodedURLComponent);
    }

    @Override
    public String encodeQueryString(String decodedURLComponent) {
        return URL.encodeQueryString(decodedURLComponent);
    }
}
