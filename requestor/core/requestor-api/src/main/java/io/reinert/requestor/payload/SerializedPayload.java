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
package io.reinert.requestor.payload;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents an HTTP payload.
 * It envelopes a String or a JavaScriptObject.
 *
 * @author Danilo Reinert
 */
public class SerializedPayload {

    private String string;
    private JavaScriptObject javaScriptObject;

    protected SerializedPayload(String string) {
        this.string = string;
    }

    protected SerializedPayload(JavaScriptObject javaScriptObject) {
        this.javaScriptObject = javaScriptObject;
    }

    public static SerializedPayload fromText(String text) {
        return new SerializedPayload(text);
    }

    public static SerializedPayload fromBlob(JavaScriptObject blob) {
        return new SerializedPayload(blob);
    }

    public static SerializedPayload fromDocument(JavaScriptObject document) {
        return new SerializedPayload(document);
    }

    public static SerializedPayload fromJson(JavaScriptObject json) {
        return new SerializedPayload(json);
    }

    public static SerializedPayload fromFormData(JavaScriptObject formData) {
        return new SerializedPayload(formData);
    }

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return (string == null || string.isEmpty()) && javaScriptObject == null;
    }

    public boolean isString() {
        return string != null;
    }

    /**
     * Returns the string value if this payload is of String type.
     *
     * @return The payload as String
     */
    public String getString() {
        return string;
    }

    public boolean isObject() {
        return javaScriptObject != null;
    }

    /**
     * Returns the javascript value if this payload is of JavaScriptObject type.
     *
     * @return The payload as JavaScriptObject
     */
    @SuppressWarnings("unchecked")
    public <J extends JavaScriptObject> J getObject() {
        return (J) javaScriptObject;
    }

    @Override
    public String toString() {
        return javaScriptObject != null ? stringify(javaScriptObject) : string;
    }

    private static native String stringify(JavaScriptObject jso) /*-{
        return JSON.stringify(jso);
    }-*/;
}
