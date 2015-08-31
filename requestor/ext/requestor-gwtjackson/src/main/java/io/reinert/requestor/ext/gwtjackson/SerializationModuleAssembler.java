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

import com.squareup.javapoet.TypeSpec;

import io.reinert.requestor.ext.gwtjackson.codegen.TypeAssembler;
import io.reinert.requestor.ext.gwtjackson.codegen.TypeInfo;

public class SerializationModuleAssembler extends TypeAssembler {

    private final TypeInfo moduleTypeInfo;
    private final SerializationModuleSchema schema;
    private final SerializationModuleCode code;

    public SerializationModuleAssembler(TypeInfo moduleTypeInfo,
                                        Iterable<JsonObjectSerdesAssembler> serdesAssemblers) {
        super(moduleTypeInfo.getPackage().getName(), moduleTypeInfo.getSimpleName() + "Impl");

        this.moduleTypeInfo = moduleTypeInfo;
        this.schema = new SerializationModuleSchema();
        this.code = new SerializationModuleCode(schema, serdesAssemblers);
    }

    @Override
    protected TypeSpec.Builder getSpec() {
        return TypeSpec.classBuilder(simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(moduleTypeInfo.getClassName())
                .addField(schema.serdesListField.assemble(code.serdesListField()))
                .addField(schema.providersListField.assemble(code.providersListField()))
                .addMethod(schema.constructor.assemble(code.constructor()))
                .addMethod(schema.getSerdesMethod.assemble(code.getSerdesListMethod()))
                .addMethod(schema.getProvidersMethod.assemble(code.getProvidersListMethod()));
    }
}
