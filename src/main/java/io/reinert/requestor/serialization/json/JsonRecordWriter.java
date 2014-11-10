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
package io.reinert.requestor.serialization.json;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This class provides useful methods for building a JSON.
 *
 * @author Danilo Reinert
 */
public final class JsonRecordWriter extends JavaScriptObject {

    protected JsonRecordWriter() {
    }

    static JsonRecordWriter create() {
        return (JsonRecordWriter) JavaScriptObject.createObject();
    }

    public native JsonRecordWriter writeBoolean(String property, boolean value) /*-{
        this[property] = value;
        return this;
    }-*/;

    public native JsonRecordWriter writeDouble(String property, double value) /*-{
        this[property] = value;
        return this;
    }-*/;

    public native JsonRecordWriter writeInt(String property, int value) /*-{
        this[property] = value;
        return this;
    }-*/;

    public native JsonRecordWriter writeNull(String property) /*-{
        this[property] = null;
        return this;
    }-*/;

    public native JsonRecordWriter writeObject(String property, JavaScriptObject value) /*-{
        this[property] = value;
        return this;
    }-*/;

    public native JsonRecordWriter writeString(String property, String value) /*-{
        this[property] = value;
        return this;
    }-*/;
}
