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
import java.util.TreeSet;

import javax.lang.model.element.Modifier;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.client.ObjectReader;
import com.github.nmorel.gwtjackson.client.ObjectWriter;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import io.reinert.requestor.core.payload.SerializedPayload;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.gwt.serialization.JsonRecordReader;
import io.reinert.requestor.gwt.serialization.JsonRecordWriter;
import io.reinert.requestor.gwtjackson.rebind.codegen.FieldAssembler;
import io.reinert.requestor.gwtjackson.rebind.codegen.InnerTypeAssembler;
import io.reinert.requestor.gwtjackson.rebind.codegen.MethodAssembler;
import io.reinert.requestor.gwtjackson.rebind.codegen.TypeInfo;
import io.reinert.requestor.gwtjackson.rebind.meta.requestor.JsonObjectSerializerMeta;

class JsonObjectSerializerSchema {

    final TypeInfo typeInfo;
    final MapperInterface mapperInterface = new MapperInterface();
    final CollectionWriterInterface collectionWriterInterface = new CollectionWriterInterface();
    final ArrayListReaderInterface arrayListReaderInterface = new ArrayListReaderInterface();
    final LinkedListReaderInterface linkedListReaderInterface = new LinkedListReaderInterface();
    final HashSetReaderInterface hashSetReaderInterface = new HashSetReaderInterface();
    final LinkedHashSetReaderInterface linkedHashSetReaderInterface = new LinkedHashSetReaderInterface();
    final TreeSetReaderInterface treeSetReaderInterface = new TreeSetReaderInterface();
    final MapperField mapperField = new MapperField();
    final CollectionWriterField collectionWriterField = new CollectionWriterField();
    final ArrayListReaderField arrayListReaderField = new ArrayListReaderField();
    final LinkedListReaderField linkedListReaderField = new LinkedListReaderField();
    final HashSetReaderField hashSetReaderField = new HashSetReaderField();
    final LinkedHashSetReaderField linkedHashSetReaderField = new LinkedHashSetReaderField();
    final TreeSetReaderField treeSetReaderField = new TreeSetReaderField();
    final Constructor constructor = new Constructor();
    final ReadJsonMethod readJsonMethod = new ReadJsonMethod();
    final WriteJsonMethod writeJsonMethod = new WriteJsonMethod();
    final DeserializeMethod deserializeMethod = new DeserializeMethod();
    final DeserializeCollectionMethod deserializeCollectionMethod;
    final SerializeMethod serializeMethod;
    final SerializeCollectionMethod serializeCollectionMethod;
    final GetLinkedListReaderMethod getLinkedListReaderMethod = new GetLinkedListReaderMethod();
    final GetHashSetReaderMethod getHashSetReaderMethod = new GetHashSetReaderMethod();
    final GetLinkedHashSetReaderMethod getLinkedHashSetReaderMethod = new GetLinkedHashSetReaderMethod();
    final GetTreeSetReaderMethod getTreeSetReaderMethod = new GetTreeSetReaderMethod();
    final MediaTypeMethod mediaTypeMethod = new MediaTypeMethod();

    JsonObjectSerializerSchema(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
        this.deserializeCollectionMethod = new DeserializeCollectionMethod(typeInfo);
        this.serializeMethod = new SerializeMethod(typeInfo);
        this.serializeCollectionMethod = new SerializeCollectionMethod(typeInfo);
    }

    class MapperInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("Mapper")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectMapper.class),
                            typeInfo.getClassName()));
        }
    }

    class CollectionWriterInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("CollectionWriter")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectWriter.class),
                            ParameterizedTypeName.get(ClassName.get(Collection.class), typeInfo.getClassName())));
        }
    }

    class ArrayListReaderInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("ArrayListReader")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectReader.class),
                            ParameterizedTypeName.get(ClassName.get(ArrayList.class), typeInfo.getClassName())));
        }
    }

    class LinkedListReaderInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("LinkedListReader")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectReader.class),
                            ParameterizedTypeName.get(ClassName.get(LinkedList.class), typeInfo.getClassName())));
        }
    }

    class HashSetReaderInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("HashSetReader")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectReader.class),
                            ParameterizedTypeName.get(ClassName.get(HashSet.class), typeInfo.getClassName())));
        }
    }

    class LinkedHashSetReaderInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("LinkedHashSetReader")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectReader.class),
                            ParameterizedTypeName.get(ClassName.get(LinkedHashSet.class), typeInfo.getClassName())));
        }
    }

    class TreeSetReaderInterface extends InnerTypeAssembler {
        protected TypeSpec.Builder getSpec() {
            return interfaceBuilder("TreeSetReader")
                    .addModifiers(Modifier.STATIC)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ObjectReader.class),
                            ParameterizedTypeName.get(ClassName.get(TreeSet.class), typeInfo.getClassName())));
        }
    }

    class MapperField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(mapperInterface.className(), "mapper")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL);
        }
    }

    class CollectionWriterField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(collectionWriterInterface.className(), "collectionWriter")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL);
        }
    }

    class ArrayListReaderField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(arrayListReaderInterface.className(), "arrayListReader")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL);
        }
    }

    class LinkedListReaderField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(linkedListReaderInterface.className(), "linkedListReader")
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class HashSetReaderField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(hashSetReaderInterface.className(), "hashSetReader")
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class LinkedHashSetReaderField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(linkedHashSetReaderInterface.className(), "linkedHashSetReader")
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class TreeSetReaderField extends FieldAssembler {
        protected FieldSpec.Builder getDeclaration() {
            return FieldSpec.builder(treeSetReaderInterface.className(), "treeSetReader")
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class Constructor extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
        }
    }

    class GetLinkedListReaderMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("getLinkedListReader")
                    .returns(linkedListReaderInterface.className())
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class GetHashSetReaderMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("getHashSetReader")
                    .returns(hashSetReaderInterface.className())
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class GetLinkedHashSetReaderMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("getLinkedHashSetReader")
                    .returns(linkedHashSetReaderInterface.className())
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class GetTreeSetReaderMethod extends MethodAssembler {
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("getTreeSetReader")
                    .returns(treeSetReaderInterface.className())
                    .addModifiers(Modifier.PRIVATE);
        }
    }

    class ReadJsonMethod extends MethodAssembler {
        final ParameterSpec reader = ParameterSpec.builder(JsonRecordReader.class, "r").build();
        final ParameterSpec context = ParameterSpec.builder(DeserializationContext.class, "ctx").build();

        protected MethodSpec.Builder getSignature() {
            // #readJson - used when none of deserialize alternatives succeeded (see JsonObjectSerializer)
            return MethodSpec.methodBuilder(JsonObjectSerializerMeta.Method.READ_JSON)
                    .returns(typeInfo.getClassName())
                    .addParameter(reader)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class WriteJsonMethod extends MethodAssembler {
        // #writeJson - not used
        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(JsonObjectSerializerMeta.Method.WRITE_JSON)
                    .addParameter(typeInfo.getClassName(), "o")
                    .addParameter(JsonRecordWriter.class, "w")
                    .addParameter(SerializationContext.class, "ctx")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class DeserializeMethod extends MethodAssembler {
        final ParameterSpec payload = ParameterSpec.builder(SerializedPayload.class, "payload").build();
        final ParameterSpec context = ParameterSpec.builder(DeserializationContext.class, "ctx").build();

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder("deserialize")
                    .returns(typeInfo.getClassName())
                    .addParameter(payload)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class DeserializeCollectionMethod extends DeserializeMethod {
        final TypeVariableName collectionTypeVar;
        final ParameterSpec collectionClass;

        private DeserializeCollectionMethod(TypeInfo typeInfo) {
            collectionTypeVar = TypeVariableName.get(
                    "C", ParameterizedTypeName.get(ClassName.get(Collection.class), typeInfo.getClassName()));
            collectionClass = ParameterSpec.builder(
                    ParameterizedTypeName.get(ClassName.get(Class.class), collectionTypeVar), "c").build();
        }

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(JsonObjectSerializerMeta.Method.DESERIALIZE)
                    .returns(collectionTypeVar)
                    .addTypeVariable(collectionTypeVar)
                    .addParameter(collectionClass)
                    .addParameter(payload)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addAnnotation(
                            AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"unchecked\"").build()
                    );
        }
    }

    class SerializeMethod extends MethodAssembler {
        final ParameterSpec object;
        final ParameterSpec context = ParameterSpec.builder(SerializationContext.class, "ctx").build();

        SerializeMethod(TypeInfo typeInfo) {
            object = ParameterSpec.builder(typeInfo.getClassName(), "o").build();
        }

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(JsonObjectSerializerMeta.Method.SERIALIZE)
                    .returns(SerializedPayload.class)
                    .addParameter(object)
                    .addParameter(context)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);
        }
    }

    class SerializeCollectionMethod extends MethodAssembler {
        final ParameterSpec collection;
        final ParameterSpec context = ParameterSpec.builder(SerializationContext.class, "ctx").build();

        SerializeCollectionMethod(TypeInfo typeInfo) {
            this.collection = ParameterSpec.builder(
                    ParameterizedTypeName.get(ClassName.get(Collection.class), typeInfo.getClassName()), "c").build();
        }

        protected MethodSpec.Builder getSignature() {
            return MethodSpec.methodBuilder(JsonObjectSerializerMeta.Method.SERIALIZE)
                    .returns(SerializedPayload.class)
                    .addParameter(collection)
                    .addParameter(context)
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
}
