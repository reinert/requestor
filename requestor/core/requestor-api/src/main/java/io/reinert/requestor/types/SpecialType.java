/*
 * Copyright 2015 Danilo Reinert
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
package io.reinert.requestor.types;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents a javascript type that XMLHttpRequest handles without serialization.
 * An SpecialType escapes the serialization when processing.
 *
 * @author Danilo Reinert
 */
public abstract class SpecialType {

    private final JavaScriptObject jso;

    protected SpecialType(JavaScriptObject jso) {
        this.jso = jso;
    }

    @SuppressWarnings("unchecked")
    public <T extends JavaScriptObject> T as() {
        return (T) jso;
    }
}
