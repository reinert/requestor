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
package io.reinert.requestor.gwt.payload;

import com.google.gwt.core.client.JavaScriptObject;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.gwt.ResponseType;

/**
 * Represents an HTTP payload.
 * It envelopes a String or a JavaScriptObject.
 *
 * @author Danilo Reinert
 */
public class SerializedJsPayload extends SerializedPayload {

    private final JavaScriptObject javaScriptObject;
    private final ResponseType responseType;

    protected SerializedJsPayload(String string, ResponseType responseType) {
        super(string);
        this.javaScriptObject = null;
        this.responseType = responseType;
    }

    protected SerializedJsPayload(JavaScriptObject javaScriptObject, ResponseType responseType) {
        super(null);
        this.javaScriptObject = javaScriptObject;
        this.responseType = responseType;
    }

    public static SerializedJsPayload fromText(String text) {
        return new SerializedJsPayload(text, ResponseType.TEXT);
    }

    public static SerializedJsPayload fromBlob(JavaScriptObject blob) {
        return new SerializedJsPayload(blob, ResponseType.BLOB);
    }

    public static SerializedJsPayload fromDocument(JavaScriptObject document) {
        return new SerializedJsPayload(document, ResponseType.DOCUMENT);
    }

    public static SerializedJsPayload fromJson(JavaScriptObject json) {
        return new SerializedJsPayload(json, ResponseType.JSON);
    }

    public static SerializedJsPayload fromFormData(JavaScriptObject formData) {
        return new SerializedJsPayload(formData, ResponseType.DEFAULT);
    }

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return (getString() == null || getString().isEmpty()) && javaScriptObject == null;
    }

    public boolean isString() {
        return getString() != null;
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

    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public String toString() {
        return javaScriptObject != null ? stringify(javaScriptObject) : getString();
    }

    private static native String stringify(JavaScriptObject jso) /*-{
        return JSON.stringify(jso);
    }-*/;
}
