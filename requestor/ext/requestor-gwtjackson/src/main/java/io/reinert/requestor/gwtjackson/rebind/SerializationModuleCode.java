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
package io.reinert.requestor.gwtjackson.rebind;

import java.util.ArrayList;
import java.util.Collections;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.gwtjackson.rebind.codegen.TypeAssembler;

class SerializationModuleCode {

    private final SerializationModuleSchema schema;
    private final Iterable<JsonObjectSerializerAssembler> generatedSerializer;

    SerializationModuleCode(SerializationModuleSchema schema,
                            Iterable<JsonObjectSerializerAssembler> generatedSerializer) {
        this.schema = schema;
        this.generatedSerializer = generatedSerializer;
    }

    CodeBlock serializerListField() {
        final TypeName arrayListTypeName = ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                ParameterizedTypeName.get(ClassName.get(Serializer.class),
                        WildcardTypeName.subtypeOf(ClassName.OBJECT)));
        return CodeBlock.builder().add("new $T()", arrayListTypeName).build();
    }

    CodeBlock typeProvidersListField() {
        return CodeBlock.builder().add("$T.emptyList()", Collections.class).build();
    }

    CodeBlock constructor() {
        final CodeBlock.Builder builder = CodeBlock.builder();
        for (TypeAssembler type : generatedSerializer) {
            builder.addStatement("$N.add(new $T())", schema.serializerListField.spec(), type.className());
        }
        return builder.build();
    }

    CodeBlock getSerializersListMethod() {
        return CodeBlock.builder().addStatement("return $N", schema.serializerListField.spec()).build();
    }

    CodeBlock getTypeProvidersListMethod() {
        return CodeBlock.builder().addStatement("return $N", schema.typeProvidersListField.spec()).build();
    }
}
