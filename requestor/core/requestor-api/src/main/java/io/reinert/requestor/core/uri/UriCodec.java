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
package io.reinert.requestor.core.uri;

/**
 *  Utility class for encoding and decoding URL parts.
 *
 *  @author Danilo Reinert
 */
public abstract class UriCodec {

    public static UriCodec INSTANCE = null;

    public static UriCodec getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetUriCodec();
        }
        return INSTANCE;
    }

    /**
     * Returns a string where all URL escape sequences have been converted back to
     * their original character representations.
     *
     * @param encodedURL string containing encoded URL encoded sequences
     * @return string with no encoded URL encoded sequences
     *
     * @throws NullPointerException if encodedURL is <code>null</code>
     */
    public abstract String decode(String encodedURL);
    /**
     * Returns a string where all URL component escape sequences have been
     * converted back to their original character representations.
     *
     * @param encodedURLComponent string containing encoded URL component
     *          sequences
     * @return string with no encoded URL component encoded sequences
     *
     * @throws NullPointerException if encodedURLComponent is <code>null</code>
     */
    public abstract String decodePathSegment(String encodedURLComponent);

    /**
     * Returns a string where all URL component escape sequences have been
     * converted back to their original character representations.
     * <p>
     * Note: this method will convert the space character escape short form, '+',
     * into a space. It should therefore only be used for query-string parts.
     *
     * @param encodedURLComponent string containing encoded URL component
     *          sequences
     * @return string with no encoded URL component encoded sequences
     *
     * @throws NullPointerException if encodedURLComponent is <code>null</code>
     */
    public abstract String decodeQueryString(String encodedURLComponent);
    /**
     * Returns a string where all characters that are not valid for a complete URL
     * have been escaped. The escaping of a character is done by converting it
     * into its UTF-8 encoding and then encoding each of the resulting bytes as a
     * %xx hexadecimal escape sequence.
     *
     * <p></p>
     * The following character sets are <em>not</em> escaped by this method:
     * <ul>
     * <li>ASCII digits or letters</li>
     * <li>ASCII punctuation characters:
     *
     * <pre>
     * - _ . ! ~ * ' ( )
     * </pre>
     *
     * </li>
     * <li>URL component delimiter characters:
     *
     * <pre>
     * ; / ? : &amp; = + $ , #
     * </pre>
     *
     * </li>
     * </ul>
     *
     * @param decodedURL a string containing URL characters that may require
     *        encoding
     * @return a string with all invalid URL characters escaped
     *
     * @throws NullPointerException if decodedURL is <code>null</code>
     */
    public abstract String encode(String decodedURL);

    /**
     * Returns a string where all characters that are not valid for a URL
     * component have been escaped. The escaping of a character is done by
     * converting it into its UTF-8 encoding and then encoding each of the
     * resulting bytes as a %xx hexadecimal escape sequence.
     *
     * <p></p>
     * The following character sets are <em>not</em> escaped by this method:
     * <ul>
     * <li>ASCII digits or letters</li>
     * <li>ASCII punctuation characters:
     *
     * <pre>- _ . ! ~ * ' ( )</pre>
     * </li>
     * </ul>
     *
     * <p></p>
     * Notice that this method <em>does</em> encode the URL component delimiter
     * characters:<blockquote>
     *
     * <pre>
     * ; / ? : &amp; = + $ , #
     * </pre>
     *
     * </blockquote>
     *
     * @param decodedURLComponent a string containing invalid URL characters
     * @return a string with all invalid URL characters escaped
     *
     * @throws NullPointerException if decodedURLComponent is <code>null</code>
     */
    public abstract String encodePathSegment(String decodedURLComponent);

    /**
     * Returns a string where all characters that are not valid for a URL
     * component have been escaped. The escaping of a character is done by
     * converting it into its UTF-8 encoding and then encoding each of the
     * resulting bytes as a %xx hexadecimal escape sequence.
     * <p>
     * Note: this method will convert any the space character into its escape
     * short form, '+' rather than %20. It should therefore only be used for
     * query-string parts.
     *
     * <p></p>
     * The following character sets are <em>not</em> escaped by this method:
     * <ul>
     * <li>ASCII digits or letters</li>
     * <li>ASCII punctuation characters:
     *
     * <pre>- _ . ! ~ * ' ( )</pre>
     * </li>
     * </ul>
     *
     * <p></p>
     * Notice that this method <em>does</em> encode the URL component delimiter
     * characters:<blockquote>
     *
     * <pre>
     * ; / ? : &amp; = + $ , #
     * </pre>
     *
     * </blockquote>
     *
     * @param decodedURLComponent a string containing invalid URL characters
     * @return a string with all invalid URL characters escaped
     *
     * @throws NullPointerException if decodedURLComponent is <code>null</code>
     */
    public abstract String encodeQueryString(String decodedURLComponent);
}
