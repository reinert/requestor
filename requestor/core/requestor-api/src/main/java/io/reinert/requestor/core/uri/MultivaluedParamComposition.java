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
package io.reinert.requestor.core.uri;

/**
 * Class that defines how multiple values will be appended to the URI along with its param name.
 *
 * @author Danilo Reinert
 */
public abstract class MultivaluedParamComposition {

    public static final MultivaluedParamComposition REPEATED_PARAM = new RepeatedParamStrategy();
    public static final MultivaluedParamComposition COMMA_SEPARATED_VALUE = new CommaSeparatedValueStrategy();

    private static final UriCodec uriCodec = UriCodec.getInstance();

    /**
     * Construct URI part from gives values.
     *
     * @param separator the separator of parameters from current URI part
     * @param name      the parameter name
     * @param values    the parameter value(s), each object will be converted to a {@code String} using its {@code
     *                  toString()} method.
     *
     * @return URI part
     */
    public abstract String asUriPart(String separator, String name, String... values);

    /**
     * Assert that the value is not null or empty.
     *
     * @param value   the value
     * @param message the message to include with any exceptions
     *
     * @throws IllegalArgumentException if value is null
     */
    protected void assertNotNullOrEmpty(String value, String message) throws IllegalArgumentException {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * This class formats multiple parameters repeating the parameter name.
     */
    public static class RepeatedParamStrategy extends MultivaluedParamComposition {

        /**
         * Construct encoded URI part from gives values.
         *
         * @param separator the separator of parameters from current URI part
         * @param name      the parameter name
         * @param values    the parameter value(s), each object will be converted to a {@code String} using its {@code
         *                  toString()} method.
         *
         * @return encoded URI part
         */
        @Override
        public String asUriPart(String separator, String name, String... values) {
            assertNotNullOrEmpty(name, "Parameter name cannot be null or empty.");
            String uriPart = "";
            String sep = "";
            for (String value : values) {
                assertNotNullOrEmpty(value, "Parameter value of *" + name
                        + "* null or empty. You must inform a valid value");

                uriPart += sep + uriCodec.encodeQueryString(name) + "=" + uriCodec.encodeQueryString(value);
                sep = separator;
            }
            return uriPart;
        }
    }

    /**
     * This class formats multiple parameters joining multiple values separated by comma.
     */
    public static class CommaSeparatedValueStrategy extends MultivaluedParamComposition {

        /**
         * Construct encoded URI part from gives values.
         *
         * @param separator the separator of parameters from current URI part
         * @param name      the parameter name
         * @param values    the parameter value(s), each object will be converted to a {@code String} using its {@code
         *                  toString()} method.
         *
         * @return encoded URI part
         */
        @Override
        public String asUriPart(String separator, String name, String... values) {
            assertNotNullOrEmpty(name, "Parameter name cannot be null or empty.");
            String uriPart = uriCodec.encodeQueryString(name) + "=";
            String sep = "";
            for (String value : values) {
                assertNotNullOrEmpty(value, "Parameter value of *" + name
                        + "* null or empty. You must inform a valid value");

                uriPart += sep + uriCodec.encodeQueryString(value);
                sep = ",";
            }
            return uriPart;
        }
    }
}
