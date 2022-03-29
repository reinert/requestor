/*
 * Copyright 2015-2022 Danilo Reinert
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
package io.reinert.requestor.java.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import io.reinert.requestor.core.uri.UriCodec;

class JavaNetUriCodec extends UriCodec {

    @Override
    public String decode(String encodedURL, String charset) {
        try {
            return URLDecoder.decode(encodedURL, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decodePathSegment(String encodedURLComponent, String charset) {
        try {
            return URLDecoder.decode(encodedURLComponent, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decodeQueryString(String encodedURLComponent, String charset) {
        try {
            return URLDecoder.decode(encodedURLComponent, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encode(String decodedURL, String charset) {
        try {
            return URLEncoder.encode(decodedURL, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encodePathSegment(String decodedURLComponent, String charset) {
        try {
            return URLEncoder.encode(decodedURLComponent, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encodeQueryString(String decodedURLComponent, String charset) {
        try {
            return URLEncoder.encode(decodedURLComponent, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
