/*
 * Copyright 2014-2021 Danilo Reinert
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

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;

/**
 * Serializer of Overlay types.
 *
 * @author Danilo Reinert
 */
public class OverlaySerializer implements Serializer<JavaScriptObject> {

    public static boolean USE_SAFE_EVAL = true;

    @Override
    public Class<JavaScriptObject> handledType() {
        return JavaScriptObject.class;
    }

    @Override
    public String[] mediaType() {
        return JsonSerializer.MEDIA_TYPE_PATTERNS;
    }

    @Override
    public JavaScriptObject deserialize(SerializedPayload payload, DeserializationContext context) {
        return eval(payload.asText());
    }

    @Override
    public <C extends Collection<JavaScriptObject>> C deserialize(Class<C> collectionType, SerializedPayload payload,
                                                                  DeserializationContext context) {
        JsArray<JavaScriptObject> jsArray = eval(payload.asText());
        C col = context.getInstance(collectionType);
        for (int i = 0; i < jsArray.length(); i++) {
            JavaScriptObject t = jsArray.get(i);
            col.add(t);
        }
        return col;
    }

    @Override
    public SerializedPayload serialize(JavaScriptObject t, SerializationContext context) {
        return new SerializedPayload(stringify(t));
    }

    @Override
    public SerializedPayload serialize(Collection<JavaScriptObject> c, SerializationContext context) {
        StringBuilder sb = new StringBuilder("[");
        for (JavaScriptObject t : c) {
            sb.append(stringify(t)).append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        return new SerializedPayload(sb.toString());
    }

    protected <T extends JavaScriptObject> T eval(String response) {
        return USE_SAFE_EVAL ? JsonUtils.<T>safeEval(response) : JsonUtils.<T>unsafeEval(response);
    }

    protected native String stringify(JavaScriptObject jso) /*-{
        return JSON.stringify(jso);
    }-*/;
}
