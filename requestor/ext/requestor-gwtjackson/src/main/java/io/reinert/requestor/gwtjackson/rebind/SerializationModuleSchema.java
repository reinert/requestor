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

import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import io.reinert.requestor.core.TypeProvider;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.gwtjackson.rebind.codegen.FieldAssembler;
import io.reinert.requestor.gwtjackson.rebind.codegen.MethodAssembler;

class SerializationModuleSchema {

    final Constructor constructor = new Constructor();
    final GetSerializersMethod getSerializersMethod = new GetSerializersMethod();
    final GetTypeProvidersMethod getTypeProvidersMethod = new GetTypeProvidersMethod();
    final SerializerListField serializerListField = new SerializerListField();
    final TypeProvidersListField typeProvidersListField = new TypeProvidersListField();

    private final TypeName serializerListTypeName = ParameterizedTypeName.get(ClassName.get(List.class),
            ParameterizedTypeName.get(ClassName.get(Serializer.class),
            WildcardTypeName.subtypeOf(ClassName.OBJECT)));
    private final TypeName typeProvidersListTypeName = ParameterizedTypeName.get(ClassName.get(List.class),
            ParameterizedTypeName.get(ClassName.get(TypeProvider.class),
                    WildcardTypeName.subtypeOf(ClassName.OBJECT)));

    class SerializerListField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(serializerListTypeName, "serializerList", Modifier.PRIVATE, Modifier.FINAL);
        }
    }

    class TypeProvidersListField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(typeProvidersListTypeName, "typeProvidersList", Modifier.PRIVATE,
                    Modifier.FINAL);
        }
    }

    class Constructor extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
        }
    }

    class GetSerializersMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("getSerializers")
                    .returns(serializerListTypeName)
                    .addModifiers(Modifier.PUBLIC);
        }
    }

    class GetTypeProvidersMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("getTypeProviders")
                    .returns(typeProvidersListTypeName)
                    .addModifiers(Modifier.PUBLIC);
        }
    }
}
