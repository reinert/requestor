/*
 * Copyright 2022 Danilo Reinert
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

import java.util.Arrays;

/**
 * The HTTP Content-Encoding header.
 *
 * @author Danilo Reinert
 */
public class ContentEncodingHeader extends SimpleHeader {

    public static final String HEADER_NAME = "Content-Encoding";

    public ContentEncodingHeader(String value, Param... params) {
        super(HEADER_NAME, Element.of(value, Arrays.asList(params)));
    }

    public ContentEncodingHeader(String value) {
        super(HEADER_NAME, value);
    }
}
