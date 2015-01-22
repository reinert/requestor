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
package io.reinert.requestor.auth;

import com.google.gwt.core.client.ScriptInjector;

/**
 * MD5 native wrapper.
 */
public class MD5 {

    private static boolean injected;

    public static String hash(String str) {
        if (!injected) inject();
        return hashNative(str);
    }

    private static native String hashNative(String str) /*-{
        return $wnd.md5hash(str);
    }-*/;

    private static void inject() {
        ScriptInjector.fromUrl("/md5.js")
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
    }
}
