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
package io.reinert.requestor.gwt.serialization;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This class provides useful methods for retrieving data from a JSON.
 *
 * @author Danilo Reinert
 */
public final class JsonRecordReader extends JavaScriptObject {

    protected JsonRecordReader() {
    }

    public native Boolean readBoolean(String property) /*-{
        return this[property] != null ? @java.lang.Boolean::valueOf(Z)(this[property]) : null;
    }-*/;

    public native boolean readBooleanPrimitive(String property) /*-{
        return this[property];
    }-*/;

    public native Double readDouble(String property) /*-{
        return this[property] != null  ? @java.lang.Double::valueOf(D)(this[property]) : null;
    }-*/;

    public native double readDoublePrimitive(String property) /*-{
        return this[property];
    }-*/;

    public native int readIntPrimitive(String property) /*-{
        return this[property];
    }-*/;

    public native Integer readInteger(String property) /*-{
        return this[property] != null  ? @java.lang.Integer::valueOf(I)(this[property]) : null;
    }-*/;

    public native Long readLong(String property) /*-{
        return this[property] != null  ? @java.lang.Long::valueOf(Ljava/lang/String;)(this[property]+'') : null;
    }-*/;

    public native JavaScriptObject readObject(String property) /*-{
        return this[property];
    }-*/;

    public native String readString(String property) /*-{
        return this[property];
    }-*/;
}
