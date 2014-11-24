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

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerializationContext;

import org.turbogwt.core.collections.JsArrayList;
import org.turbogwt.core.util.Overlays;

/**
 * Serializer/Deserializer of Overlay types.
 *
 * @author Danilo Reinert
 */
public class OverlaySerdes implements Serdes<JavaScriptObject> {

    public static boolean USE_SAFE_EVAL = true;

    private static OverlaySerdes INSTANCE = new OverlaySerdes();

    public static OverlaySerdes getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<JavaScriptObject> handledType() {
        return JavaScriptObject.class;
    }

    @Override
    public String[] mediaType() {
        return JsonSerdes.MEDIA_TYPE_PATTERNS;
    }

    @Override
    public JavaScriptObject deserialize(String response, DeserializationContext context) {
        return eval(response);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Collection<JavaScriptObject>> C deserialize(Class<C> collectionType, String response,
                                                                  DeserializationContext context) {
        JsArray<JavaScriptObject> jsArray = eval(response);
        if (collectionType.equals(List.class) || collectionType.equals(Collection.class)
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
    public String serialize(JavaScriptObject t, SerializationContext context) {
        return Overlays.stringify(t);
    }

    @Override
    public String serialize(Collection<JavaScriptObject> c, SerializationContext context) {
        if (c instanceof JsArrayList)
            return Overlays.stringify(((JsArrayList<JavaScriptObject>) c).asJsArray());

        @SuppressWarnings("unchecked")
        JsArray<JavaScriptObject> jsArray = (JsArray<JavaScriptObject>) JsArray.createArray();
        for (JavaScriptObject t : c) {
            jsArray.push(t);
        }

        return Overlays.stringify(jsArray);
    }

    private <T extends JavaScriptObject> T eval(String response) {
        return USE_SAFE_EVAL ? JsonUtils.<T>safeEval(response) : JsonUtils.<T>unsafeEval(response);
    }
}
