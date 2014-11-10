/*
 * Copyright 2014 Danilo Reinert
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
 * Simple header with optional parameters.
 *
 * According to <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">
 *     http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7</a>:
 * media-type     = type "/" subtype *( ";" parameter )
 * type           = token
 * subtype        = token
 *
 * @author Danilo Reinert
 */
public class SimpleHeaderWithParameter extends SimpleHeader {

    public SimpleHeaderWithParameter(String name, String value) {
        super(name, value);
    }

    public SimpleHeaderWithParameter(String name, String value, Param... params) {
        super(name, value + paramsToString(params));
    }

    /**
     * Parameter of a header value.
     */
    public static class Param {

        final String key;
        final String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "; " + key + '=' + value;
        }
    }

    private static String paramsToString(Param... params) {
        String result = "";
        for (Param param : params) {
            result += param.toString();
        }
        return result;
    }
}
