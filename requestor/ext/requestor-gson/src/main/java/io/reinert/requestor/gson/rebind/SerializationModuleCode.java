/*
 * Copyright 2022 Danilo Reinert
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
package io.reinert.requestor.gson.rebind;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import com.squareup.javapoet.CodeBlock;

import io.reinert.requestor.core.TypeProvider;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.gson.GsonSingletonProvider;
import io.reinert.requestor.gson.rebind.codegen.TypeAssembler;

class SerializationModuleCode {

    private final SerializationModuleSchema schema;
    private final Iterable<SerializerAssembler> generatedSerializer;

    SerializationModuleCode(SerializationModuleSchema schema,
                            Iterable<SerializerAssembler> generatedSerializer) {
        this.schema = schema;
        this.generatedSerializer = generatedSerializer;
    }

    CodeBlock serializerListField() {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("$T.<$T<?>>asList(\n", Arrays.class, Serializer.class)
                .indent().indent();
        Iterator<SerializerAssembler> it = generatedSerializer.iterator();
        while (it.hasNext()) {
            TypeAssembler next = it.next();
            String sep = it.hasNext() ? ",\n" : "\n";
            builder.add("new $T()" + sep, next.className());
        }
        return builder.unindent().unindent().add(")").build();
    }

    CodeBlock typeProvidersListField() {
        return CodeBlock.builder()
                .add("$T.<$T<?>>singletonList($T.getProvider())",
                        Collections.class,
                        TypeProvider.class,
                        GsonSingletonProvider.class)
                .build();
    }

    CodeBlock getSerializersListMethod() {
        return CodeBlock.builder().addStatement("return $N", schema.serializerListField.spec()).build();
    }

    CodeBlock getTypeProvidersListMethod() {
        return CodeBlock.builder().addStatement("return $N", schema.typeProvidersListField.spec()).build();
    }
}
