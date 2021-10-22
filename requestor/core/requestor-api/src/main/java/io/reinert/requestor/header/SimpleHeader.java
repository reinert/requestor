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
package io.reinert.requestor.header;

/**
 * Simple HTTP header with name and value.
 *
 * @author Danilo Reinert
 */
public class SimpleHeader extends Header {

    private final String name;
    private final Value value;

    public SimpleHeader(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    public SimpleHeader(String name, String value) {
        this.name = name;
        this.value = Value.of(value);
    }

    public Value getRawValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value.toString();
    }
}
