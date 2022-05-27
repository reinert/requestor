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

import java.io.StringWriter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;

import io.reinert.requestor.core.payload.TextSerializedPayload;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;
import io.reinert.requestor.core.serialization.UnableToSerializeException;
import io.reinert.requestor.gson.FilterableJsonWriter;
import io.reinert.requestor.gson.rebind.codegen.FieldAssembler;
import io.reinert.requestor.gson.rebind.meta.requestor.DeserializationContextMeta;
import io.reinert.requestor.gson.rebind.meta.requestor.SerializationContextMeta;

class SerializerCode {

    private final SerializerSchema schema;

    SerializerCode(SerializerSchema schema) {
        this.schema = schema;
    }

    CodeBlock lazyGetter(FieldAssembler field) {
        return CodeBlock.builder()
                .beginControlFlow("if ($N == null)",
                        field.spec())
                .addStatement("$N = new $T()",
                        field.spec(),
                        field.spec().type)
                .endControlFlow()
                .addStatement("return $N",
                        field.spec())
                .build();
    }

    CodeBlock handledType() {
        return CodeBlock.builder()
                .addStatement("return $T.class",
                        schema.typeInfo.getClassName()) // TODO: .getClassName() ?
                .build();
    }

    CodeBlock mediaType() {
        final StringBuilder arrayValues = new StringBuilder();
        for (String value : schema.typeInfo.getMediaTypes()) {
            arrayValues.append("\"").append(value).append("\", ");
        }
        return CodeBlock.builder()
                .addStatement("return new String[] { $L }", arrayValues.substring(0, arrayValues.length() - 2))
                .build();
    }

    CodeBlock serialize() {
        final SerializerSchema.SerializeMethod methodSchema = schema.serializeMethod;

        return CodeBlock.builder()
                .beginControlFlow("try")
                .add(getSerializeCodeBlock(methodSchema.payload, methodSchema.context))
                .nextControlFlow("catch ($T e)",
                        Exception.class)
                .addStatement("throw new $T($S + $N.$L().getName(), e)",
                        UnableToSerializeException.class,
                        "The auto-generated gson serializer failed to serialize the instance of ",
                        methodSchema.context,
                        SerializationContextMeta.Method.GET_REQUESTED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock serializeCollection() {
        final SerializerSchema.SerializeCollectionMethod methodSchema = schema.serializeCollectionMethod;

        return CodeBlock.builder()
                .beginControlFlow("try")
                .add(getSerializeCodeBlock(methodSchema.payload, methodSchema.context))
                .nextControlFlow("catch ($T e)",
                        Exception.class)
                .addStatement("throw new $T($S + $N.$L().getSimpleName() + \" of \" + $N.$L().getName(), e)",
                        UnableToSerializeException.class,
                        "The auto-generated gson serializer failed to serialize the ",
                        methodSchema.context,
                        SerializationContextMeta.Method.GET_REQUESTED_TYPE,
                        methodSchema.context,
                        SerializationContextMeta.Method.GET_PARAMETRIZED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock deserialize() {
        final SerializerSchema.DeserializeMethod methodSchema = schema.deserializeMethod;
        final String gsonVar = "gson";

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("final $T $N = $N.$L($T.class)",
                        Gson.class,
                        gsonVar,
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_INSTANCE,
                        Gson.class)
                .addStatement("return ($T) $N.fromJson($N.asString(), $N.$L())",
                        schema.typeInfo.getClassName(),
                        gsonVar,
                        methodSchema.payload,
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_REQUESTED_TYPE)
                .nextControlFlow("catch ($T e)",
                        Exception.class)
                .addStatement("throw new $T($S + $N.$L().getName(), e)",
                        UnableToDeserializeException.class,
                        "The auto-generated gson deserializer failed to deserialize the response body to ",
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_REQUESTED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock deserializeCollection() {
        final SerializerSchema.DeserializeCollectionMethod methodSchema = schema.deserializeCollectionMethod;
        final String typeTokenVar = "typeToken";
        final String gsonVar = "gson";

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("final $T<?> $N = $T.getParameterized($N.$L(), $N.$L())",
                        TypeToken.class,
                        typeTokenVar,
                        TypeToken.class,
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_REQUESTED_TYPE,
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_PARAMETRIZED_TYPE)
                .addStatement("final $T $N = $N.$L($T.class)",
                        Gson.class,
                        gsonVar,
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_INSTANCE,
                        Gson.class)
                .addStatement("return $N.fromJson($N.asString(), $N.getType())",
                        gsonVar,
                        methodSchema.payload,
                        typeTokenVar)
                .nextControlFlow("catch ($T e)",
                        Exception.class)
                .addStatement("throw new $T($S + $N.$L().getSimpleName() + \" of \" + $N.$L().getName(), e)",
                        UnableToDeserializeException.class,
                        "The auto-generated gwt-jackson json deserializer failed to deserialize the response body to ",
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_REQUESTED_TYPE,
                        methodSchema.context,
                        DeserializationContextMeta.Method.GET_PARAMETRIZED_TYPE)
                .endControlFlow()
                .build();
    }

    private CodeBlock getSerializeCodeBlock(ParameterSpec payload, ParameterSpec context) {
        final String stringWriterVar = "out";
        final String jsonWriterVar = "fjw";
        final String gsonVar = "gson";

        return CodeBlock.builder()
                .addStatement("final $T $N = new $T()",
                        StringWriter.class,
                        stringWriterVar,
                        StringWriter.class)
                .addStatement("final $T $N = new $T($N, $N.$L())",
                        FilterableJsonWriter.class,
                        jsonWriterVar,
                        FilterableJsonWriter.class,
                        stringWriterVar,
                        context,
                        SerializationContextMeta.Method.GET_FIELDS)
                .addStatement("final $T $N = $N.$L($T.class)",
                        Gson.class,
                        gsonVar,
                        context,
                        SerializationContextMeta.Method.GET_INSTANCE,
                        Gson.class)
                .addStatement("$N.toJson($N, $N.getClass(), $N)",
                        gsonVar,
                        payload,
                        payload,
                        jsonWriterVar)
                .addStatement("return new $T($N.toString())",
                        TextSerializedPayload.class,
                        stringWriterVar)
                .build();
    }
}
