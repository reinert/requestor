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

import java.util.Collection;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeVariableName;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.gson.rebind.codegen.MethodAssembler;
import io.reinert.requestor.gson.rebind.codegen.TypeInfo;
import io.reinert.requestor.gson.rebind.meta.requestor.SerializerMeta;

class SerializerSchema {

    final TypeInfo typeInfo;
    final Constructor constructor;
    final HandledTypeMethod handledTypeMethod;
    final MediaTypeMethod mediaTypeMethod;
    final DeserializeMethod deserializeMethod;
    final DeserializeCollectionMethod deserializeCollectionMethod;
    final SerializeMethod serializeMethod;
    final SerializeCollectionMethod serializeCollectionMethod;

    SerializerSchema(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
        constructor = new Constructor();
        handledTypeMethod = new HandledTypeMethod();
        mediaTypeMethod = new MediaTypeMethod();
        serializeMethod = new SerializeMethod();
        serializeCollectionMethod = new SerializeCollectionMethod();
        deserializeMethod = new DeserializeMethod();
        deserializeCollectionMethod = new DeserializeCollectionMethod();
    }

    class Constructor extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
        }
    }

    class HandledTypeMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("handledType")
                    .returns(ParameterizedTypeName.get(ClassName.get(Class.class), typeInfo.getClassName()))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class MediaTypeMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("mediaType")
                    .returns(ArrayTypeName.of(String.class))
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class SerializeMethod extends MethodAssembler {
        final ParameterSpec payload = ParameterSpec.builder(typeInfo.getClassName(), "payload").build();
        final ParameterSpec context = ParameterSpec.builder(SerializationContext.class, "ctx").build();

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(SerializerMeta.Method.SERIALIZE)
                    .returns(SerializedPayload.class)
                    .addParameter(payload)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class SerializeCollectionMethod extends MethodAssembler {
        final ParameterSpec payload = ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class),
                typeInfo.getClassName()), "payload").build();
        final ParameterSpec context = ParameterSpec.builder(SerializationContext.class, "ctx").build();

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(SerializerMeta.Method.SERIALIZE)
                    .returns(SerializedPayload.class)
                    .addParameter(payload)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class DeserializeMethod extends MethodAssembler {
        final ParameterSpec payload = ParameterSpec.builder(SerializedPayload.class, "payload").build();
        final ParameterSpec context = ParameterSpec.builder(DeserializationContext.class, "ctx").build();

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(SerializerMeta.Method.DESERIALIZE)
                    .returns(typeInfo.getClassName())
                    .addParameter(payload)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class DeserializeCollectionMethod extends DeserializeMethod {
        final TypeVariableName collectionTypeVar = TypeVariableName.get(
                "C", ParameterizedTypeName.get(ClassName.get(Collection.class), typeInfo.getClassName()));
        final ParameterSpec collectionClass = ParameterSpec.builder(
                ParameterizedTypeName.get(ClassName.get(Class.class), collectionTypeVar), "colType").build();

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(SerializerMeta.Method.DESERIALIZE)
                    .returns(collectionTypeVar)
                    .addTypeVariable(collectionTypeVar)
                    .addParameter(collectionClass)
                    .addParameter(payload)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                            .addMember("value", "\"unchecked\"")
                            .build());
        }
    }
}
