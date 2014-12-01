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
package io.reinert.requestor;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents an HTTP payload.
 * It envelopes a String or a JavaScriptObject.
 *
 * @author Danilo Reinert
 */
public class Payload {

    private String string;
    private JavaScriptObject javaScriptObject;

    public Payload(String string) {
        this.string = string;
    }

    public Payload(JavaScriptObject javaScriptObject) {
        this.javaScriptObject = javaScriptObject;
    }

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return string == null && javaScriptObject == null;
    }

    /**
     * Returns the string value if this payload is of String type.
     *
     * @return The payload as String
     */
    public String isString() {
        return string;
    }

    /**
     * Returns the javascript value if this payload is of JavaScriptObject type.
     *
     * @return The payload as JavaScriptObject
     */
    public JavaScriptObject isJavaScriptObject() {
        return javaScriptObject;
    }

    @Override
    public String toString() {
        return javaScriptObject != null ? stringify(javaScriptObject) : string;
    }

    private native String stringify(JavaScriptObject jso) /*-{
        return JSON.stringify(jso);
    }-*/;
}
