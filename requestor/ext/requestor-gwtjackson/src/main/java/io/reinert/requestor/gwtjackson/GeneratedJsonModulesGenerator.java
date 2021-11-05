/*
 * Copyright 2021 Danilo Reinert
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
package io.reinert.requestor.gwtjackson;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import io.reinert.requestor.SerializationModule;
import io.reinert.requestor.annotations.JsonSerializationModule;

/**
 * Generator for GeneratedModules that instantiates all serializers for classes declared in
 * {@link JsonSerializationModule} annotation above interfaces extending from {@link SerializationModule}.
 *
 * @author Danilo Reinert
 */
public class GeneratedJsonModulesGenerator extends Generator {

    static final String SERIALIZATION_MODULE_FULL_NAME = SerializationModule.class.getName();

    private static final Logger LOGGER = Logger.getLogger(GeneratedJsonModulesGenerator.class.getName());

    private final StringBuilder sourceLog = new StringBuilder();

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx, String typeName) throws UnableToCompleteException {
        final TypeOracle typeOracle = ctx.getTypeOracle();
        assert typeOracle != null;

        final JClassType generatedModulesType = typeOracle.findType(typeName);
        if (generatedModulesType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }

        if (generatedModulesType.isInterface() == null) {
            logger.log(TreeLogger.ERROR, generatedModulesType.getQualifiedSourceName()
                    + " is not an interface", null);
            throw new UnableToCompleteException();
        }

        // TODO: check if type was already generated and reuse it
        TreeLogger treeLogger = logger.branch(TreeLogger.INFO, "Generating GeneratedModules...", null);

        final SourceWriter sourceWriter = getSourceWriter(treeLogger, ctx, generatedModulesType);

        if (sourceWriter != null) {
            sourceWriter.println();

            JClassType serializationModuleType = null;
            try {
                serializationModuleType = typeOracle.getType(SERIALIZATION_MODULE_FULL_NAME);
            } catch (NotFoundException e) {
                logger.log(TreeLogger.ERROR, "Could not find " + SERIALIZATION_MODULE_FULL_NAME, null);
                throw new UnableToCompleteException();
            }

            ArrayDeque<JClassType> nonEmptyModules = new ArrayDeque<JClassType>();

            for (JClassType moduleType : serializationModuleType.getSubtypes()) {
                final JsonSerializationModule serializationModuleAnn =
                        moduleType.getAnnotation(JsonSerializationModule.class);

                if (serializationModuleAnn != null && serializationModuleAnn.value().length > 0) {
                    nonEmptyModules.add(moduleType);
                }
            }

            generateMethods(sourceWriter, nonEmptyModules);

            // TODO: uncomment the line below to log the generated source code
            // LOGGER.info(sourceLog.toString());

            sourceWriter.commit(treeLogger);
        }

        return typeName + "Impl";
    }

    private String asCsv(Collection<JClassType> types) {
        StringBuilder result = new StringBuilder();
        for (JClassType type : types) {
            result.append("new ")
                    .append(type.getPackage().getName())
                    .append('.')
                    .append(getTypeImplName(type))
                    .append("()")
                    .append(", ");
        }
        result.replace(result.length() - 2, result.length(), "");
        return result.toString();
    }

    private void generateMethods(SourceWriter w, Collection<JClassType> moduleTypes) {
        print(w, String.format("@Override"));
        print(w, String.format("public SerializationModule[] getSerializationModules() {"));
        print(w, String.format("    return new SerializationModule[]{ %s };", asCsv(moduleTypes)));
        print(w, String.format("}"));
    }

    private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, JClassType type) {
        JPackage typePkg = type.getPackage();
        String packageName = typePkg == null ? "" : typePkg.getName();
        PrintWriter printWriter = ctx.tryCreate(logger, packageName, getTypeImplName(type));
        if (printWriter == null) {
            return null;
        }

        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, getTypeImplName(type));

        composerFactory.addImport(GWT.class.getCanonicalName());

        composerFactory.addImplementedInterface(type.getErasedType().getQualifiedSourceName());

        return composerFactory.createSourceWriter(ctx, printWriter);
    }

    private String getTypeImplName(JClassType type) {
        return type.getName().replace('.', '_') + "Impl";
    }

    private void print(SourceWriter srcWriter, String s) {
        srcWriter.println(s);
        sourceLog.append('\n').append(s);
    }
}
