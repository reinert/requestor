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
package io.reinert.requestor.uri;

class UrlCodecMock extends UrlCodec {

    @Override
    public String decode(String encodedURL) {
        return encodedURL;
    }

    @Override
    public String decodePathSegment(String encodedURLComponent) {
        return encodedURLComponent;
    }

    @Override
    public String decodeQueryString(String encodedURLComponent) {
        return encodedURLComponent;
    }

    @Override
    public String encode(String decodedURL) {
        return decodedURL;
    }

    @Override
    public String encodePathSegment(String decodedURLComponent) {
        return decodedURLComponent;
    }

    @Override
    public String encodeQueryString(String decodedURLComponent) {
        return decodedURLComponent;
    }
}
