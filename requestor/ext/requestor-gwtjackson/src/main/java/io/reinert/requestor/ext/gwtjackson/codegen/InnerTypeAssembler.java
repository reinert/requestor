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
package io.reinert.requestor.ext.gwtjackson.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

/**
 * A inner type schema that can be assembled to later get its spec.
 *
 * @author Danilo Reinert
 */
public abstract class InnerTypeAssembler extends TypeAssembler {

    public InnerTypeAssembler() {
        super(null, null);
    }

    protected final TypeSpec.Builder annotationBuilder(String simpleName) {
        setName("", simpleName);
        return TypeSpec.annotationBuilder(simpleName);
    }

    protected final TypeSpec.Builder classBuilder(String simpleName) {
        setName("", simpleName);
        return TypeSpec.classBuilder(simpleName);
    }

    protected final TypeSpec.Builder enumBuilder(String simpleName) {
        setName("", simpleName);
        return TypeSpec.enumBuilder(simpleName);
    }

    protected final TypeSpec.Builder interfaceBuilder(String simpleName) {
        setName("", simpleName);
        return TypeSpec.interfaceBuilder(simpleName);
    }

    @Override
    public ClassName className() {
        if (simpleName() == null)
            throw new IllegalStateException("SimpleName was not set through any of the builder methods.");
        return super.className();
    }
}
