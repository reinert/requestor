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
package io.reinert.requestor.gwt;

import io.reinert.requestor.core.Base64Codec;
import io.reinert.requestor.core.Requestor;
import io.reinert.requestor.core.auth.DigestAuth;

/**
 * This class provides a static initializer for Requestor's deferred bindings for GWT environment.
 *
 * @author Danilo Reinert
 */
public class RequestorGwt {

    /**
     * Initializes static lazy bindings to enable the proper usage of Requestor in GWT environment.
     * <p></p>
     * Call this method in a static block in the app's EntryPoint.
     */
    public static void init() {
        if (!Requestor.isInitialized()) {
            Requestor.init(
                    new Base64Codec() {
                        public native String decode(String encoded, String charset) /*-{
                            return $wnd.atob(encoded);
                        }-*/;

                        @Override
                        public String decode(byte[] encoded, String toCharset) {
                            throw new UnsupportedOperationException("Cannot base64 decode from byte[]. " +
                                    "Byte array is not supported in requestor-gwt.");
                        }

                        public native String encode(String text, String charset) /*-{
                            return $wnd.btoa(text);
                        }-*/;
                    },
                    new GwtUriCodec()
            );

            DigestAuth.setHashFunction("md5", new DigestAuth.HashFunction() {
                public String hash(String input) {
                    return MD5.hash(input);
                }
            });
        }
    }
}
