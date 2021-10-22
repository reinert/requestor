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

import io.reinert.requestor.ext.gwtjackson.codegen.FieldAssembler;
import io.reinert.requestor.ext.gwtjackson.codegen.InnerTypeAssembler;
import io.reinert.requestor.ext.gwtjackson.meta.gwtjackson.ObjectMapperMeta;
import io.reinert.requestor.ext.gwtjackson.meta.gwtjackson.ObjectReaderMeta;
import io.reinert.requestor.ext.gwtjackson.meta.gwtjackson.ObjectWriterMeta;
import io.reinert.requestor.ext.gwtjackson.meta.requestor.DeserializationContextMeta;
import io.reinert.requestor.ext.gwtjackson.meta.requestor.SerializationContextMeta;
import io.reinert.requestor.serialization.UnableToDeserializeException;
import io.reinert.requestor.serialization.UnableToSerializeException;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;

class JsonObjectSerdesCode {

    private final JsonObjectSerdesSchema schema;

    JsonObjectSerdesCode(JsonObjectSerdesSchema schema) {
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
                        JsonObjectSerdes.class, "stringify",
                        schema.readJsonMethod.reader).build();
    }

    CodeBlock writeJson() {
        return CodeBlock.builder()
                .addStatement("throw new $T($S)",
                        UnsupportedOperationException.class,
                        "writeJson method should not be used in generated serdes")
                .build();
    }

    CodeBlock deserialize() {
        final JsonObjectSerdesSchema.DeserializeMethod currentScope = schema.deserializeMethod;
        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("return $N.$L($N)",
                        schema.mapperField.spec(),
                        ObjectMapperMeta.Method.READ,
                        currentScope.rawJson)
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
        final JsonObjectSerdesSchema.DeserializeCollectionMethod currentScope = schema.deserializeCollectionMethod;
        return CodeBlock.builder()
                .beginControlFlow("try")
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
                        currentScope.rawJson)
                .nextControlFlow("else if ($N.equals($T.class))",
                        currentScope.collectionClass,
                        LinkedList.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getLinkedListReaderMethod.spec(),
                        ObjectReaderMeta.Method.READ,
                        currentScope.rawJson)
                .nextControlFlow("else if ($N.equals($T.class) || $N.equals($T.class))",
                        currentScope.collectionClass,
                        Set.class,
                        currentScope.collectionClass,
                        HashSet.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getHashSetReaderMethod.spec(),
                        ObjectReaderMeta.Method.READ,
                        currentScope.rawJson)
                .nextControlFlow("else if ($N.equals($T.class))",
                        currentScope.collectionClass,
                        LinkedHashSet.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getLinkedHashSetReaderMethod.spec(),
                        ObjectReaderMeta.Method.READ,
                        currentScope.rawJson)
                .nextControlFlow("else if ($N.equals($T.class))",
                        currentScope.collectionClass,
                        LinkedList.class)
                .addStatement("return ($T) $N().$L($N)",
                        currentScope.collectionTypeVar,
                        schema.getTreeSetReaderMethod.spec(),
                        ObjectMapperMeta.Method.READ,
                        currentScope.rawJson)
                .nextControlFlow("else")
                .addStatement("return super.deserialize($N, $N, $N)",
                        currentScope.collectionClass,
                        currentScope.rawJson,
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
        final JsonObjectSerdesSchema.SerializeMethod currentScope = schema.serializeMethod;
        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("return $N.$L($N)",
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
        final JsonObjectSerdesSchema.SerializeCollectionMethod currentScope = schema.serializeCollectionMethod;
        return CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("return $N.$L($N)",
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
}
