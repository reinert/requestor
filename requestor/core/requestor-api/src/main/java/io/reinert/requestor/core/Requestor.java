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
package io.reinert.requestor.core;

import io.reinert.requestor.core.auth.BasicAuth;
import io.reinert.requestor.core.uri.Buckets;
import io.reinert.requestor.core.uri.UriCodec;

/**
 * This class provides a static initializer for Requestor's deferred bindings.
 *
 * @author Danilo Reinert
 */
public class Requestor {

    private static boolean initPending = true;

    /**
     * Initializes static lazy bindings to proper usage of Requestor.
     * <p></p>
     * Call this method in a static block in the app's entry point.
     */
    public static void init(BasicAuth.Base64 base64, UriCodec uriCodec, Buckets.Factory bucketsFactory) {
        if (base64 == null) throw new IllegalArgumentException("BasicAuth.Base64 cannot be null");
        if (uriCodec == null) throw new IllegalArgumentException("UriCodec cannot be null");
        if (bucketsFactory == null) throw new IllegalArgumentException("Buckets.Factory cannot be null");

        doInit(base64, uriCodec, bucketsFactory);
    }

    public static void init(BasicAuth.Base64 base64, UriCodec uriCodec) {
        if (base64 == null) throw new IllegalArgumentException("BasicAuth.Base64 cannot be null");
        if (uriCodec == null) throw new IllegalArgumentException("UriCodec cannot be null");

        doInit(base64, uriCodec, null);
    }

    public static void init(BasicAuth.Base64 base64) {
        if (base64 == null) throw new IllegalArgumentException("BasicAuth.Base64 cannot be null");

        doInit(base64, null, null);
    }

    private static void doInit(BasicAuth.Base64 base64, UriCodec uriCodec, Buckets.Factory bucketsFactory) {
        if (initPending) {
            if (base64 != null) BasicAuth.BASE64 = base64;

            if (uriCodec != null) UriCodec.INSTANCE = uriCodec;

            if (bucketsFactory != null) Buckets.Factory.INSTANCE = bucketsFactory;

            initPending = false;
        }
    }

    public static boolean isInitialized() {
        return !initPending;
    }
}
