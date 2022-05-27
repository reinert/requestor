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
package io.reinert.requestor.gson.rebind.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

/**
 * A type schema that can be assembled to later get its spec.
 *
 * @author Danilo Reinert
 */
public abstract class TypeAssembler {

    private String packageName;
    private String simpleName;
    private ClassName className;
    private TypeSpec spec;

    protected TypeAssembler(String packageName, String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        if (packageName != null && simpleName != null)
            this.className = ClassName.get(packageName, simpleName);
    }

    protected TypeAssembler(String packageName, String simpleName, String... simplesNames) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        if (packageName != null && simpleName != null)
            this.className = ClassName.get(packageName, simpleName, simplesNames);
    }

    protected abstract TypeSpec.Builder getSpec();

    public final TypeSpec assemble() {
        spec = getSpec().build();
        return spec;
    }

    public final TypeSpec spec() {
        if (spec == null)
            throw new IllegalStateException("Type spec has not been built yet.");
        return spec;
    }

    public String packageName() {
        return packageName;
    }

    public String simpleName() {
        return simpleName;
    }

    public ClassName className() {
        return className;
    }

    protected void setName(String packageName, String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.className = ClassName.get(packageName, simpleName);
    }
}
