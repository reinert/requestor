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

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.gson.rebind.codegen.TypeAssembler;
import io.reinert.requestor.gson.rebind.codegen.TypeInfo;

public class SerializerAssembler extends TypeAssembler {

    private final TypeInfo typeInfo;
    private final SerializerSchema schema;
    private final SerializerCode code;

    public SerializerAssembler(TypeInfo typeInfo) {
        super("io.reinert.requestor.gson.gen." + typeInfo.getPackage().getName(),
                typeInfo.getSimpleName() + "Serializer");

        this.typeInfo = typeInfo;
        this.schema = new SerializerSchema(typeInfo);
        this.code = new SerializerCode(schema);
    }

    @Override
    protected TypeSpec.Builder getSpec() {
        return TypeSpec.classBuilder(simpleName())
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Serializer.class),
                        typeInfo.getClassName()))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(schema.handledTypeMethod.assemble(code.handledType()))
                .addMethod(schema.mediaTypeMethod.assemble(code.mediaType()))
                //.addMethod(schema.constructor.assemble())
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
        if (!(o instanceof SerializerAssembler))
            return false;

        final SerializerAssembler that = (SerializerAssembler) o;

        return typeInfo.equals(that.typeInfo);
    }

    @Override
    public int hashCode() {
        return typeInfo.hashCode();
    }
}
