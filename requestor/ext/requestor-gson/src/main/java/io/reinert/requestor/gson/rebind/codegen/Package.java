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

/**
 * Info about some package that will be handled in code generation.
 *
 * @author Danilo Reinert
 */
public class Package {

    private final String canonicalName;
    private final String[] parts;

    public Package(String canonicalName) {
        this.canonicalName = canonicalName;
        this.parts = canonicalName.split("\\.");
    }

    public String getName() {
        return canonicalName;
    }

    public String[] getParts() {
        return parts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Package aPackage = (Package) o;

        return canonicalName.equals(aPackage.canonicalName);
    }

    @Override
    public int hashCode() {
        return canonicalName.hashCode();
    }

    @Override
    public String toString() {
        return canonicalName;
    }
}
