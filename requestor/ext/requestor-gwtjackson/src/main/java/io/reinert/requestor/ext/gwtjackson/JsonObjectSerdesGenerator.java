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

import java.io.IOException;

import javax.annotation.processing.Filer;

import com.squareup.javapoet.JavaFile;

import io.reinert.requestor.ext.gwtjackson.codegen.TypeInfo;
import io.reinert.requestor.ext.gwtjackson.processing.ProcessingException;

public class JsonObjectSerdesGenerator {

    private final TypeInfo typeInfo;
    private final JsonObjectSerdesAssembler assembler;
    private boolean generated;

    public JsonObjectSerdesGenerator(JsonObjectSerdesAssembler assembler) {
        this.typeInfo = assembler.getTypeInfo();
        this.assembler = assembler;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void generate(Filer filer) throws ProcessingException {
        try {
            assembler.assemble();
            JavaFile.builder(assembler.packageName(), assembler.spec()).build().writeTo(filer);
        } catch (IOException e) {
            throw new ProcessingException(null, e,
                    "Error while writing generated Serdes of %s: %s", typeInfo.getQualifiedName(), e.getMessage());
        }
        generated = true;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public JsonObjectSerdesAssembler getAssembler() {
        return assembler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof JsonObjectSerdesGenerator))
            return false;

        final JsonObjectSerdesGenerator that = (JsonObjectSerdesGenerator) o;

        return typeInfo.equals(that.typeInfo);

    }

    @Override
    public int hashCode() {
        return typeInfo.hashCode();
    }
}
