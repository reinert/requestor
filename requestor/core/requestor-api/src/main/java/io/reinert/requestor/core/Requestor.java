/*
 * Copyright 2021-2022 Danilo Reinert
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

import io.reinert.requestor.core.uri.UriCodec;

/**
 * This class provides a static initializer for Requestor's deferred bindings.
 *
 * @author Danilo Reinert
 */
public class Requestor {

    private static boolean initialized = false;

    /**
     * <p>Initializes static lazy bindings to enable the proper usage of Requestor.</p>
     *
     * <p>Call this method in a static block in the app's entry point.</p>
     */
    public static void init(Base64Codec base64Codec, UriCodec uriCodec) {
        if (initialized) throw new IllegalStateException("Requestor is already initialized.");

        if (base64Codec == null) throw new IllegalArgumentException("Base64Codec cannot be null");
        if (uriCodec == null) throw new IllegalArgumentException("UriCodec cannot be null");

        Base64Codec.setInstance(base64Codec);
        UriCodec.setInstance(uriCodec);

        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
