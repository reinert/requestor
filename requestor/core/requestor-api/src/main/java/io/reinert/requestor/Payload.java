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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.client.ArrayBufferNative;

import io.reinert.requestor.io.ArrayBufferInputStream;

/**
 * Represents an HTTP payload.
 * It envelopes a String or a JavaScriptObject.
 *
 * @author Danilo Reinert
 */
public class Payload {

    private String string;
    private JavaScriptObject javaScriptObject;
    private byte[] bytes;

    private Payload(String string) {
        this.string = string;
        if (string != null) {
            try {
                this.bytes = string.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Could not convert string to utf8 bytes.");
            }
        }
    }

    private Payload(JavaScriptObject javaScriptObject) {
        this.javaScriptObject = javaScriptObject;
    }

    private Payload(JavaScriptObject javaScriptObject, byte[] bytes) {
        this.javaScriptObject = javaScriptObject;
        this.bytes = bytes;
    }

    public static Payload fromText(String text) {
        return new Payload(text);
    }

    public static Payload fromArrayBuffer(JavaScriptObject arrayBuffer) {
        byte[] bytes =  getBytesFromArrayBuffer((ArrayBufferNative) arrayBuffer);
        return new Payload(arrayBuffer, bytes);
    }

    public static Payload fromBlob(JavaScriptObject blob) {
        Payload payload = new Payload(blob);
        return new Payload(blob);
    }

    public static void fromBlob(JavaScriptObject blob, Callback<Payload, Throwable> callback) {
        Payload payload = new Payload(blob);
        readBlobAsArrayBuffer(blob, payload, callback);
    }

    public static Payload fromDocument(JavaScriptObject document) {
        return new Payload(document);
    }

    public static Payload fromJson(JavaScriptObject json) {
        return new Payload(json);
    }

    public static Payload fromFormData(JavaScriptObject formData) {
        return new Payload(formData);
    }

    /**
     * Returns true if this payload is empty.
     *
     * @return true if this payload is empty
     */
    public boolean isEmpty() {
        return (string == null || string.isEmpty()) && javaScriptObject == null;
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

    public byte[] getBytes() {
        if (bytes == null) {
            throw new RuntimeException("bytes are not available yet.");
        }

        return bytes;
    }

    public boolean isBytesAvailable() {
        return bytes != null;
    }

    private static byte[] getBytesFromArrayBuffer(ArrayBufferNative arrayBuffer) {
        final byte[] bytes;
        try {
            ArrayBufferInputStream is = new ArrayBufferInputStream(arrayBuffer);
            bytes = new byte[is.available()];
            is.read(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert array buffer to utf8 bytes.", e);
        }
        return bytes;
    }

    private static native String stringify(JavaScriptObject jso) /*-{
        return JSON.stringify(jso);
    }-*/;

    private static native void readBlobAsArrayBuffer(JavaScriptObject blob, Payload payload,
                                                     Callback<Payload, Throwable> callback) /*-{
        var reader = new FileReader();
        reader.addEventListener("load", function() {
            payload.@Payload::bytes = @Payload::getBytesFromArrayBuffer(*)(reader.result);
            callback.@com.google.gwt.core.client.Callback::onSuccess(*)(payload);
        });
        reader.addEventListener("error", function() {
            callback.@com.google.gwt.core.client.Callback::onFailure(*)(
                new RuntimeException("Error while reading blob as array buffer."));
        });
        reader.readAsArrayBuffer(blob);
    }-*/;
}
