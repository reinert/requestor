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
package io.reinert.requestor.examples.showcase.util;

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.header.Header;

public class Util {

    public static String formatHeaders(Header... headers) {
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            sb.append(header.getName()).append(": ").append(header.getValue()).append('\n');
        }
        return sb.toString();
    }

    public static String formatHeaders(Iterable<Header> headers) {
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            sb.append(header.getName()).append(": ").append(header.getValue()).append('\n');
        }
        return sb.toString();
    }

    public static native String formatJson(JavaScriptObject o) /*-{
        return $wnd.JSON.stringify(o, null, 2);
    }-*/;

    public static native void log(JavaScriptObject o) /*-{
        $wnd.console.log(o);
    }-*/;
}
