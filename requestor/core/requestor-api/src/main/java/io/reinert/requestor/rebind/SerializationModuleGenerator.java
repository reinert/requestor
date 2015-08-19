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
package io.reinert.requestor.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import io.reinert.requestor.SerializationModule;

/**
 * Generator that returns the implementation type name of a interface extending {@link SerializationModule}.
 *
 * The implementation code generation must be handled externally, by annotation processing.
 *
 * @author Danilo Reinert
 */
public class SerializationModuleGenerator extends Generator {
    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        final String[] parts = typeName.split("\\.");
        String qualifiedName = "";
        String separator = "";
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            if (Character.isLowerCase(part.charAt(0))) {
                qualifiedName = qualifiedName + separator + part;
                separator = ".";
            } else {
                if (i == parts.length - 1) qualifiedName = qualifiedName + separator + part;
            }
        }
        return qualifiedName + "Impl";
    }
}
