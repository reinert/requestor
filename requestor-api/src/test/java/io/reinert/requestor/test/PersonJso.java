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
package io.reinert.requestor.test;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay implementation of {@link Person}.
 *
 * @author Danilo Reinert
 */
public final class PersonJso extends JavaScriptObject {

    protected PersonJso() {
    }

    public static PersonJso create(int id, String name, double weight, Date birthday) {
        return create(id, name, weight, birthday.getTime());
    }

    private static native PersonJso create(int id, String name, double weight, double birthday) /*-{
        return {id: id, name: name, weight: weight, birthday: birthday};
    }-*/;

    public native int getId() /*-{
        return this.id;
    }-*/;

    public native String getName() /*-{
        return this.name;
    }-*/;

    public native double getWeight() /*-{
        return this.weight;
    }-*/;

    public native Date getBirthday() /*-{
        return this.birthday;
    }-*/;
}
