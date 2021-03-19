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

import java.util.ArrayList;

import com.squareup.javapoet.ClassName;

/**
 * Info about some type that will be handled for code generation.
 *
 * @author Danilo Reinert
 */
public class TypeInfo {

    private final Package pack;
    private final String simpleName;
    private final String qualifiedName;
    private final ClassName className;
    private final ArrayList<String> names = new ArrayList<String>();
    private String[] mediaTypes = null;
    private boolean innerType;

    public TypeInfo(String qualifiedName) {
        final String[] parts = qualifiedName.split("\\.");
        String packageName = "";
        String separator = "";
        for (String part : parts) {
            if (Character.isLowerCase(part.charAt(0))) {
                packageName = packageName + separator + part;
                separator = ".";
            } else {
                names.add(part);
            }
        }
        this.pack = new Package(packageName);
        this.simpleName = names.get(names.size() - 1);
        this.qualifiedName = qualifiedName;
        this.className = names.size() == 1 ? ClassName.get(packageName, simpleName) :
                ClassName.get(packageName, names.get(0),
                        names.subList(1, names.size()).toArray(new String[names.size() - 1]));
        this.innerType = names.size() > 1;
    }

    public TypeInfo(String packageName, String simpleName) {
        this.pack = new Package(packageName);
        this.simpleName = simpleName;
        this.qualifiedName = pack.getName() + '.' + simpleName;
        this.className = ClassName.get(pack.getName(), simpleName);
        names.add(simpleName);
    }

    public TypeInfo(String qualifiedName, String[] mediaTypes) {
        this(qualifiedName);
        this.mediaTypes = mediaTypes;
    }

    public ClassName getClassName() {
        return className;
    }

    public Package getPackage() {
        return pack;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public boolean isInnerType() {
        return innerType;
    }

    public boolean hasMediaTypes() {
        return mediaTypes != null;
    }

    public String[] getMediaTypes() {
        return mediaTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TypeInfo))
            return false;

        final TypeInfo typeInfo = (TypeInfo) o;

        return qualifiedName.equals(typeInfo.qualifiedName);
    }

    @Override
    public int hashCode() {
        return qualifiedName.hashCode();
    }
}
