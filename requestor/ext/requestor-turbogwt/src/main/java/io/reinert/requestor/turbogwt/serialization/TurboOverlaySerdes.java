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
package io.reinert.requestor.turbogwt.serialization;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.json.OverlaySerdes;

import org.turbogwt.core.collections.JsArrayList;

/**
 * Serializer/Deserializer of Overlay types.
 *
 * @author Danilo Reinert
 */
public class TurboOverlaySerdes extends OverlaySerdes {

    private static TurboOverlaySerdes INSTANCE = new TurboOverlaySerdes();

    public static TurboOverlaySerdes getInstance() {
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Collection<JavaScriptObject>> C deserialize(Class<C> collectionType, String response,
                                                                  DeserializationContext context) {
        JsArray<JavaScriptObject> jsArray = eval(response);
        if (collectionType.equals(List.class)
                || collectionType.equals(Collection.class)
                || collectionType.equals(JsArrayList.class)) {
            return (C) new JsArrayList(jsArray);
        } else {
            C col = context.getInstance(collectionType);
            for (int i = 0; i < jsArray.length(); i++) {
                JavaScriptObject t = jsArray.get(i);
                col.add(t);
            }
            return col;
        }
    }

    @Override
    public String serialize(Collection<JavaScriptObject> c, SerializationContext context) {
        if (c instanceof JsArrayList)
            return stringify(((JsArrayList<JavaScriptObject>) c).asJsArray());
        return super.serialize(c, context);
    }
}
