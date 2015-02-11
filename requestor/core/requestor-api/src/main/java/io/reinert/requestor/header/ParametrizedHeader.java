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
public class ParametrizedHeader extends SimpleHeader {

    public ParametrizedHeader(String name, String value, String p1, String v1, String p2, String v2,
                              String p3, String v3) {
        super(name, value + paramsToString(Param.of(p1, v1), Param.of(p2, v2), Param.of(p3, v3)));
    }

    public ParametrizedHeader(String name, String value, String p1, String v1, String p2, String v2) {
        super(name, value + paramsToString(Param.of(p1, v1), Param.of(p2, v2)));
    }

    public ParametrizedHeader(String name, String value, String p1, String v1) {
        super(name, value + paramsToString(Param.of(p1, v1)));
    }

    public ParametrizedHeader(String name, String value, Param... params) {
        super(name, value + paramsToString(params));
    }

    public ParametrizedHeader(String name, String value) {
        super(name, value);
    }

    /**
     * Parameter of a header value.
     */
    public static class Param {

        final String key;
        final String value;

        private Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public static Param of(String key, String value) {
            return new Param(key, value);
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
