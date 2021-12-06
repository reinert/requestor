/*
 * Copyright 2021 Danilo Reinert
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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.nmorel.gwtjackson.client.exception.JsonDeserializationException;
import com.github.nmorel.gwtjackson.client.exception.JsonSerializationException;
import com.google.gwt.core.client.GWT;
import com.squareup.javapoet.CodeBlock;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;
import io.reinert.requestor.core.serialization.UnableToSerializeException;
import io.reinert.requestor.gwt.serialization.JsonObjectSerializer;
import io.reinert.requestor.gwtjackson.rebind.codegen.FieldAssembler;
import io.reinert.requestor.gwtjackson.rebind.codegen.InnerTypeAssembler;
import io.reinert.requestor.gwtjackson.rebind.meta.gwtjackson.ObjectMapperMeta;
import io.reinert.requestor.gwtjackson.rebind.meta.gwtjackson.ObjectReaderMeta;
import io.reinert.requestor.gwtjackson.rebind.meta.gwtjackson.ObjectWriterMeta;
import io.reinert.requestor.gwtjackson.rebind.meta.requestor.DeserializationContextMeta;
import io.reinert.requestor.gwtjackson.rebind.meta.requestor.SerializationContextMeta;

class JsonObjectSerializerCode {

    private final JsonObjectSerializerSchema schema;

    JsonObjectSerializerCode(JsonObjectSerializerSchema schema) {
        this.schema = schema;
    }

    CodeBlock gwtCreateInitializer(InnerTypeAssembler innerTypeAssembler) {
        // FIXME: add (instead of addStatement) is used because javapoet is putting an additional ';' unnecessarily
        return CodeBlock.builder()
                .add("$T.create($T.class)", GWT.class, innerTypeAssembler.className()).build();
    }

    CodeBlock constructor() {
        return CodeBlock.builder()
                .addStatement("super($T.class)", schema.typeInfo.getClassName()).build();
    }

    CodeBlock lazyGetter(FieldAssembler field) {
        return CodeBlock.builder()
                .beginControlFlow("if ($N == null)",
                        field.spec())
                .addStatement("$N = $T.create($T.class)",
                        field.spec(),
                        GWT.class,
                        field.spec().type)
                .endControlFlow()
                .addStatement("return $N",
                        field.spec())
                .build();
    }

    CodeBlock readJson() {
        return CodeBlock.builder()
                .addStatement("return $N.$L($T.$L($N))",
                        schema.mapperField.spec(), ObjectMapperMeta.Method.READ,
                        JsonObjectSerializer.class, "stringify",
                        schema.readJsonMethod.reader).build();
    }

    CodeBlock writeJson() {
        return CodeBlock.builder()
                .addStatement("throw new $T($S)",
                        UnsupportedOperationException.class,
                        "writeJson method should not be used in generated serializer")
                .build();
    }

    CodeBlock deserialize() {
        final JsonObjectSerializerSchema.DeserializeMethod currentScope = schema.deserializeMethod;

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("return $N.$L($N.asText())",
                        schema.mapperField.spec(),
                        ObjectMapperMeta.Method.READ,
                        currentScope.payload)
                .nextControlFlow("catch ($T e)",
                        JsonDeserializationException.class)
                .addStatement("throw new $T($S + $N.$L().getName(), e)",
                        UnableToDeserializeException.class,
                        "The auto-generated gwt-jackson deserializer failed to deserialize the response body to ",
                        currentScope.context,
                        DeserializationContextMeta.Method.GET_REQUESTED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock deserializeCollection() {
        final JsonObjectSerializerSchema.DeserializeCollectionMethod currentScope = schema.deserializeCollectionMethod;

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("$T $N = $N.asText()",
                        String.class,
                        "text",
                        currentScope.payload)
                .beginControlFlow("if ($N.equals($T.class) || $N.equals($T.class) || $N.equals($T.class))",
                        currentScope.collectionClass,
                        List.class,
                        currentScope.collectionClass,
                        ArrayList.class,
                        currentScope.collectionClass,
                        Collection.class)
                .addStatement("return ($T) $N.$L($N)",
                        currentScope.collectionTypeVar,
                        schema.arrayListReaderField.spec(),
                        ObjectReaderMeta.Method.READ,
                        "text")
                .nextControlFlow("else if ($N.equals($T.class))",
                        currentScope.collectionClass,
                        LinkedList.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getLinkedListReaderMethod.spec(),
                        ObjectReaderMeta.Method.READ,
                        "text")
                .nextControlFlow("else if ($N.equals($T.class) || $N.equals($T.class))",
                        currentScope.collectionClass,
                        Set.class,
                        currentScope.collectionClass,
                        HashSet.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getHashSetReaderMethod.spec(),
                        ObjectReaderMeta.Method.READ,
                        "text")
                .nextControlFlow("else if ($N.equals($T.class))",
                        currentScope.collectionClass,
                        LinkedHashSet.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getLinkedHashSetReaderMethod.spec(),
                        ObjectReaderMeta.Method.READ,
                        "text")
                .nextControlFlow("else if ($N.equals($T.class))",
                        currentScope.collectionClass,
                        LinkedList.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getTreeSetReaderMethod.spec(),
                        ObjectMapperMeta.Method.READ,
                        "text")
                .nextControlFlow("else")
                .addStatement("return super.deserialize($N, $N, $N)",
                        currentScope.collectionClass,
                        currentScope.payload,
                        currentScope.context)
                .endControlFlow()
                .nextControlFlow("catch ($T e)",
                        JsonDeserializationException.class)
                .addStatement("throw new $T($S + $N.$L().getSimpleName() + \" of \" + $N.$L().getName(), e)",
                        UnableToDeserializeException.class,
                        "The auto-generated gwt-jackson json deserializer failed to deserialize the response body to ",
                        currentScope.context,
                        DeserializationContextMeta.Method.GET_REQUESTED_TYPE,
                        currentScope.context,
                        DeserializationContextMeta.Method.GET_PARAMETRIZED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock serialize() {
        final JsonObjectSerializerSchema.SerializeMethod currentScope = schema.serializeMethod;

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("return new $T($N.$L($N))",
                        SerializedPayload.class,
                        schema.mapperField.spec(),
                        ObjectMapperMeta.Method.WRITE,
                        currentScope.object)
                .nextControlFlow("catch ($T e)",
                        JsonSerializationException.class)
                .addStatement("throw new $T($S + $N.$L().getName(), e)",
                        UnableToSerializeException.class,
                        "The auto-generated gwt-jackson json serializer failed to serialize the instance of ",
                        currentScope.context,
                        SerializationContextMeta.Method.GET_REQUESTED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock serializeCollection() {
        final JsonObjectSerializerSchema.SerializeCollectionMethod currentScope = schema.serializeCollectionMethod;

        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("return new $T($N.$L($N))",
                        SerializedPayload.class,
                        schema.collectionWriterField.spec(),
                        ObjectWriterMeta.Method.WRITE,
                        currentScope.collection)
                .nextControlFlow("catch ($T e)",
                        JsonSerializationException.class)
                .addStatement("throw new $T($S + $N.$L().getSimpleName() + \" of \" + $N.$L().getName(), e)",
                        UnableToSerializeException.class,
                        "The auto-generated gwt-jackson json serializer failed to serialize the ",
                        currentScope.context,
                        SerializationContextMeta.Method.GET_REQUESTED_TYPE,
                        currentScope.context,
                        SerializationContextMeta.Method.GET_PARAMETRIZED_TYPE)
                .endControlFlow()
                .build();
    }

    CodeBlock mediaType(String[] mediaTypes) {
        StringBuilder arrayValues = new StringBuilder();
        for (String value : mediaTypes) {
            arrayValues.append("\"").append(value).append("\", ");
        }
        return CodeBlock.builder()
                .addStatement("return new String[] { $L }", arrayValues.substring(0, arrayValues.lastIndexOf(",")))
                .build();
    }
}
