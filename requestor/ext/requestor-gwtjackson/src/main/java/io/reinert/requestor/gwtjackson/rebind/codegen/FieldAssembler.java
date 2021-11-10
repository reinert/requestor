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
package io.reinert.requestor.gwtjackson.rebind.codegen;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;

/**
 * A field schema that can be assembled to later get its spec.
 *
 * @author Danilo Reinert
 */
public abstract class FieldAssembler {

    private FieldSpec spec;

    protected abstract FieldSpec.Builder getDeclaration();

    public FieldSpec assemble(CodeBlock initializer) {
        final FieldSpec.Builder builder = getDeclaration();
        if (initializer != null) builder.initializer(initializer);
        spec = builder.build();
        return spec;
    }

    public FieldSpec spec() {
        if (spec == null)
            throw new IllegalStateException("Field spec has not been built yet.");
        return spec;
    }
}
