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

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.reinert.requestor.ext.gwtjackson.codegen.TypeAssembler;
import io.reinert.requestor.ext.gwtjackson.codegen.TypeInfo;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;

public class JsonObjectSerdesAssembler extends TypeAssembler {

    private final TypeInfo typeInfo;
    private final JsonObjectSerdesSchema schema;
    private final JsonObjectSerdesCode code;

    public JsonObjectSerdesAssembler(TypeInfo typeInfo) {
        super("io.reinert.requestor.gen." + typeInfo.getPackage().getName(), typeInfo.getSimpleName() + "Serdes");

        this.typeInfo = typeInfo;
        this.schema = new JsonObjectSerdesSchema(typeInfo);
        this.code = new JsonObjectSerdesCode(schema);
    }

    @Override
    protected TypeSpec.Builder getSpec() {
        return TypeSpec.classBuilder(simpleName())
                .superclass(ParameterizedTypeName.get(ClassName.get(JsonObjectSerdes.class), typeInfo.getClassName()))
                .addModifiers(Modifier.PUBLIC)
                .addType(schema.mapperInterface.assemble())
                .addType(schema.collectionWriterInterface.assemble())
                .addType(schema.arrayListReaderInterface.assemble())
                .addType(schema.linkedListReaderInterface.assemble())
                .addType(schema.hashSetReaderInterface.assemble())
                .addType(schema.linkedHashSetReaderInterface.assemble())
                .addType(schema.treeSetReaderInterface.assemble())
                .addField(schema.mapperField.assemble(
                        code.gwtCreateInitializer(schema.mapperInterface)))
                .addField(schema.collectionWriterField.assemble(
                        code.gwtCreateInitializer(schema.collectionWriterInterface)))
                .addField(schema.arrayListReaderField.assemble(
                        code.gwtCreateInitializer(schema.arrayListReaderInterface)))
                .addField(schema.linkedListReaderField.assemble(null))
                .addField(schema.hashSetReaderField.assemble(null))
                .addField(schema.linkedHashSetReaderField.assemble(null))
                .addField(schema.treeSetReaderField.assemble(null))
                .addMethod(schema.constructor.assemble(code.constructor()))
                .addMethod(schema.getLinkedListReaderMethod.assemble(
                        code.lazyGetter(schema.linkedListReaderField)))
                .addMethod(schema.getHashSetReaderMethod.assemble(
                        code.lazyGetter(schema.hashSetReaderField)))
                .addMethod(schema.getLinkedHashSetReaderMethod.assemble(
                        code.lazyGetter(schema.linkedHashSetReaderField)))
                .addMethod(schema.getTreeSetReaderMethod.assemble(
                        code.lazyGetter(schema.treeSetReaderField)))
                .addMethod(schema.readJsonMethod.assemble(code.readJson()))
                .addMethod(schema.writeJsonMethod.assemble(code.writeJson()))
                .addMethod(schema.deserializeMethod.assemble(code.deserialize()))
                .addMethod(schema.deserializeCollectionMethod.assemble(code.deserializeCollection()))
                .addMethod(schema.serializeMethod.assemble(code.serialize()))
                .addMethod(schema.serializeCollectionMethod.assemble(code.serializeCollection()));
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof JsonObjectSerdesAssembler))
            return false;

        final JsonObjectSerdesAssembler that = (JsonObjectSerdesAssembler) o;

        return typeInfo.equals(that.typeInfo);
    }

    @Override
    public int hashCode() {
        return typeInfo.hashCode();
    }
}
