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
package io.reinert.requestor.form;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.FormElement;

final class FormDataOverlay extends JavaScriptObject {

    protected FormDataOverlay() {
    }

    public static native FormDataOverlay create() /*-{
        return new FormData();
    }-*/;

    public static native FormDataOverlay create(FormElement formElement) /*-{
        return new FormData(formElement);
    }-*/;

    public native void append(String name, String value) /*-{
        this.append(name, value);
    }-*/;

    public native void append(String name, JavaScriptObject file, String fileName) /*-{
        this.append(name, file, fileName);
    }-*/;
}
