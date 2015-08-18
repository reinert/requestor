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
package io.reinert.requestor.ext.gwtjackson;

import java.util.ArrayList;
import java.util.Collections;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import io.reinert.requestor.ext.gwtjackson.codegen.TypeAssembler;
import io.reinert.requestor.serialization.Serdes;

class SerializationModuleCode {

    private final SerializationModuleSchema schema;
    private final Iterable<JsonObjectSerdesAssembler> generatedSerdes;

    SerializationModuleCode(SerializationModuleSchema schema, Iterable<JsonObjectSerdesAssembler> generatedSerdes) {
        this.schema = schema;
        this.generatedSerdes = generatedSerdes;
    }

    CodeBlock serdesListField() {
        final TypeName arrayListTypeName = ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                ParameterizedTypeName.get(ClassName.get(Serdes.class),
                        WildcardTypeName.subtypeOf(ClassName.OBJECT)));
        return CodeBlock.builder().add("new $T()", arrayListTypeName).build();
    }

    CodeBlock providersListField() {
        return CodeBlock.builder().add("$T.emptyList()", Collections.class).build();
    }

    CodeBlock constructor() {
        final CodeBlock.Builder builder = CodeBlock.builder();
        for (TypeAssembler type : generatedSerdes) {
            builder.addStatement("$N.add(new $T())", schema.serdesListField.spec(), type.className());
        }
        return builder.build();
    }

    CodeBlock getSerdesListMethod() {
        return CodeBlock.builder().addStatement("return $N", schema.serdesListField.spec()).build();
    }

    CodeBlock getProvidersListMethod() {
        return CodeBlock.builder().addStatement("return $N", schema.providersListField.spec()).build();
    }
}
