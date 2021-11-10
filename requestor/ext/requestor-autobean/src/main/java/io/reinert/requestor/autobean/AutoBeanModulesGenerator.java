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
package io.reinert.requestor.autobean;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import io.reinert.requestor.autobean.annotations.AutoBeanSerializationModule;
import io.reinert.requestor.core.SerializationModule;
import io.reinert.requestor.core.TypeProvider;
import io.reinert.requestor.core.annotations.MediaType;
import io.reinert.requestor.core.serialization.DeserializationContext;
import io.reinert.requestor.core.serialization.Deserializer;
import io.reinert.requestor.core.serialization.HandlesSubTypes;
import io.reinert.requestor.core.serialization.SerializationContext;
import io.reinert.requestor.core.serialization.Serializer;
import io.reinert.requestor.core.serialization.UnableToDeserializeException;
import io.reinert.requestor.core.serialization.UnableToSerializeException;
import io.reinert.requestor.gwt.serialization.JsonObjectSerializer;
import io.reinert.requestor.gwt.serialization.JsonRecordReader;
import io.reinert.requestor.gwt.serialization.JsonRecordWriter;

/**
 * Generator for {@link AutoBeanSerializationModule} annotated types
 * powered by GWT AutoBean Framework.
 *
 * @author Danilo Reinert
 */
public class AutoBeanModulesGenerator extends Generator {

    public static String[] MEDIA_TYPE_PATTERNS = new String[] { "application/json" };

    private static final Logger LOGGER = Logger.getLogger(AutoBeanModulesGenerator.class.getName());

    private static final String factoryFieldName = "myFactory";
    private static final String factoryTypeName = "MyFactory";

    private final StringBuilder sourceLog = new StringBuilder();

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx, String typeName) throws UnableToCompleteException {
        final TypeOracle typeOracle = ctx.getTypeOracle();
        assert typeOracle != null;

        final JClassType serializationModuleType = typeOracle.findType(typeName);
        if (serializationModuleType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }

        if (serializationModuleType.isInterface() == null) {
            logger.log(TreeLogger.ERROR, serializationModuleType.getQualifiedSourceName()
                    + " is not an interface", null);
            throw new UnableToCompleteException();
        }

        // TODO: check if type was already generated and reuse it
        TreeLogger treeLogger = logger.branch(TreeLogger.INFO, "Generating Json Serializer powered by AutoBeans...",
                null);

        for (JClassType moduleType : serializationModuleType.getSubtypes()) {
            final AutoBeanSerializationModule serializationModuleAnn =
                    moduleType.getAnnotation(AutoBeanSerializationModule.class);
            if (serializationModuleAnn != null && serializationModuleAnn.value().length > 0) {
                generateModule(treeLogger, ctx, moduleType, serializationModuleAnn);
            }
        }

        return serializationModuleType.getPackage() + "." + getTypeName(serializationModuleType) + "Impl";
    }

    public void generateModule(TreeLogger treeLogger, GeneratorContext ctx,
                               JClassType moduleType, AutoBeanSerializationModule serializationModuleAnn) {
        final TypeOracle typeOracle = ctx.getTypeOracle();

        final SourceWriter sourceWriter = getSourceWriter(treeLogger, ctx, moduleType);
        sourceWriter.println();

        final ArrayDeque<String> allTypesAndWrappers = new ArrayDeque<String>();
        final ArrayDeque<JClassType> autoBeanTypes = getJTypes(typeOracle, serializationModuleAnn);
        final String[] mediaTypes = getMediaTypePatterns(moduleType.getAnnotation(MediaType.class));

        for (JClassType autoBeanType : autoBeanTypes) {
            final String listWrapperTypeName = generateListWrapperInterface(sourceWriter, autoBeanType);
            sourceWriter.println();

            final String setWrapperTypeName = generateSetWrapperInterface(sourceWriter, autoBeanType);
            sourceWriter.println();

            // Add current autoBeanType name for single de/serialization
            allTypesAndWrappers.add(autoBeanType.getQualifiedSourceName());
            allTypesAndWrappers.add(listWrapperTypeName);
            allTypesAndWrappers.add(setWrapperTypeName);
        }

        final ArrayDeque<String> serializerFields = new ArrayDeque<String>();
        final ArrayDeque<String> typeProviderFields = new ArrayDeque<String>();

        if (!allTypesAndWrappers.isEmpty()) {
            generateFactoryInterface(sourceWriter, allTypesAndWrappers);
            sourceWriter.println();

            generateFactoryField(sourceWriter);
            sourceWriter.println();

            for (JClassType autoBeanType : autoBeanTypes) {
                final String typeProviderFieldName = generateTypeProviderField(sourceWriter, autoBeanType);
                typeProviderFields.add(typeProviderFieldName);

                final String serializerFieldName = generateSerializerClassAndField(treeLogger, typeOracle,
                        sourceWriter, autoBeanType, mediaTypes);
                serializerFields.add(serializerFieldName);
            }
        }

        generateFields(sourceWriter);
        generateConstructor(sourceWriter, moduleType, serializerFields, typeProviderFields);
        generateMethods(sourceWriter);

        // TODO: uncomment the line below to log the generated source code
        // LOGGER.info(sourceLog.toString());

        sourceWriter.commit(treeLogger);
    }

    private String[] getMediaTypePatterns(MediaType mediaTypeAnn) {
        String[] mediaTypes = MEDIA_TYPE_PATTERNS;
        if (mediaTypeAnn != null && mediaTypeAnn.value().length > 0) {
            mediaTypes = mediaTypeAnn.value();
        }
        return mediaTypes;
    }

    private ArrayDeque<JClassType> getJTypes(TypeOracle oracle, AutoBeanSerializationModule serializationModuleAnn) {
        ArrayDeque<JClassType> types = new ArrayDeque<JClassType>();
        for (Class<?> cls : serializationModuleAnn.value()) {
            types.add(oracle.findType(cls.getCanonicalName()));
        }
        return types;
    }

    private String asStringCsv(String[] array) {
        StringBuilder result = new StringBuilder();
        for (String s : array) {
            result.append('"').append(s).append('"').append(", ");
        }
        result.replace(result.length() - 2, result.length(), "");
        return result.toString();
    }

    private String firstCharToLowerCase(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private String firstCharToUpperCase(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void generateConstructor(SourceWriter w, JClassType moduleType, Iterable<String> serializer,
                                     Iterable<String> typeProviders) {
        print(w, String.format("public %s() {", getModuleTypeImplName(moduleType)));
        for (String s : serializer) {
            print(w, String.format("    serializerList.add(%s);", s));
        }
        for (String s : typeProviders) {
            print(w, String.format("    typeProvidersList.add(%s);", s));
        }
        print(w, String.format("}"));
        print(w, String.format(""));
    }

    private void generateFactoryField(SourceWriter w) {
        print(w, String.format("private static %s %s = GWT.create(%s.class);", factoryTypeName, factoryFieldName,
                factoryTypeName));
    }

    private void generateFactoryInterface(SourceWriter w, Iterable<String> typeNames) {
        print(w, String.format("interface %s extends AutoBeanFactory {", factoryTypeName));
        for (String typeName : typeNames) {
            print(w, String.format("    AutoBean<%s> %s();", typeName,
                    replaceDotByUpperCase(firstCharToLowerCase(typeName))));
        }
        print(w, String.format("}"));
    }

    private void generateFields(SourceWriter w) {
        // Initialize a field with binary name of the remote service interface
        print(w, String.format("private final ArrayList<Serializer<?>> serializerList = " +
                "new ArrayList<Serializer<?>>();"));
        print(w, String.format("private final ArrayList<TypeProvider<?>> typeProvidersList = " +
                "new ArrayList<TypeProvider<?>>();"));
        print(w, String.format(""));
    }

    private String generateListWrapperInterface(SourceWriter w, JClassType type) {
        String wrapperTypeName = getListWrapperTypeName(type);

        print(w, String.format("interface %s {", wrapperTypeName));
        print(w, String.format("    List<%s> getResult();", type.getQualifiedSourceName()));
        print(w, String.format("    void setResult(List<%s> result);", type.getQualifiedSourceName()));
        print(w, String.format("}"));

        return wrapperTypeName;
    }

    private void generateMethods(SourceWriter w) {
        print(w, String.format("@Override"));
        print(w, String.format("public List<Serializer<?>> getSerializers() {"));
        print(w, String.format("    return serializerList;"));
        print(w, String.format("}"));
        print(w, String.format(""));
        print(w, String.format("@Override"));
        print(w, String.format("public List<TypeProvider<?>> getTypeProviders() {"));
        print(w, String.format("    return typeProvidersList;"));
        print(w, String.format("}"));
        print(w, String.format(""));
    }

    private String generateTypeProviderField(SourceWriter w, JClassType type) {
        final String fieldName = getFieldName(type);
        final String autoBeanFactoryMethodName = factoryFieldName + "." + fieldName;
        final String typeProviderFieldName = fieldName + "TypeProvider";

        print(w, String.format("private final TypeProvider %s = new TypeProvider<%s>() {", typeProviderFieldName,
                type.getQualifiedSourceName()));
        print(w, String.format("    @Override"));
        print(w, String.format("    public Class<%s> getType() {", type.getQualifiedSourceName()));
        print(w, String.format("        return %s.class;", type.getQualifiedSourceName()));
        print(w, String.format("    }"));
        print(w, String.format(""));
        print(w, String.format("    @Override"));
        print(w, String.format("    public %s getInstance() {", type.getQualifiedSourceName()));
        print(w, String.format("        return %s().as();", autoBeanFactoryMethodName));
        print(w, String.format("    }"));
        print(w, String.format("};"));
        print(w, String.format(""));

        return typeProviderFieldName;
    }

    /**
     * Create the serializer and return the field name.
     */
    private String generateSerializerClassAndField(TreeLogger logger, TypeOracle oracle, SourceWriter w,
                                                   JClassType type, String[] mediaTypes) {
        final String qualifiedSourceName = type.getQualifiedSourceName();
        final String fieldName = getFieldName(type);
        final String listWrapperTypeName = getListWrapperTypeName(type);
        final String setWrapperTypeName = getSetWrapperTypeName(type);
        final String listWrapperFactoryMethodName = factoryFieldName + "." + firstCharToLowerCase(listWrapperTypeName);
        final String setWrapperFactoryMethodName = factoryFieldName + "." + firstCharToLowerCase(setWrapperTypeName);

        final String serializerFieldName = fieldName + "Serializer";
        final String serializerTypeName = getTypeName(type) + "Serializer";

        // serializer field as anonymous class
        print(w, String.format("private static class %s extends JsonObjectSerializer<%s> implements %s {",
                serializerTypeName,
                qualifiedSourceName, HandlesSubTypes.class.getSimpleName()));

        // static field for impl array
        final String autoBeanInstanceClass = factoryFieldName + "." + fieldName + "().getClass()";
        print(w, String.format("    private final Class[] IMPL = new Class[]{ %s };", autoBeanInstanceClass));

        // static field to content-types
        print(w, String.format("    private final String[] PATTERNS = new String[]{ %s };", asStringCsv(mediaTypes)));
        print(w, String.format(""));

        // constructor
        print(w, String.format("    public %s() {", serializerTypeName));
        print(w, String.format("        super(%s.class);", qualifiedSourceName));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // mediaType
        print(w, String.format("    @Override"));
        print(w, String.format("    public Class[] handledSubTypes() {"));
        print(w, String.format("        return IMPL;"));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // mediaType
        print(w, String.format("    @Override"));
        print(w, String.format("    public String[] mediaType() {"));
        print(w, String.format("        return PATTERNS;"));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // readJson - used when any of deserialize alternatives succeeded (see JsonObjectSerializer)
        // TODO: improve this by not requiring parsing the json to an js array and latter stringifying it (see below)
        // Here would be no-op
        print(w, String.format("    @Override"));
        print(w, String.format("    public %s readJson(JsonRecordReader r, DeserializationContext ctx) {",
                qualifiedSourceName));
        print(w, String.format("        return AutoBeanCodex.decode(%s, %s.class," +
                        " JsonObjectSerializer.stringify(r)).as();",
                factoryFieldName, qualifiedSourceName));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // writeJson - not used
        print(w, String.format("    @Override"));
        print(w, String.format("    public void writeJson(%s o, JsonRecordWriter w, SerializationContext ctx) {",
                qualifiedSourceName));
        print(w, String.format("        return;"));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // deserialize - deserialize single object using ObjectMapper
        print(w, String.format("    @Override"));
        print(w, String.format("    public %s deserialize(String s, DeserializationContext ctx) {",
                qualifiedSourceName));
        print(w, String.format("        try {"));
        print(w, String.format("            return AutoBeanCodex.decode(%s, %s.class, s).as();", factoryFieldName,
                qualifiedSourceName));
        print(w, String.format("        } catch (java.lang.Exception e) {"));
        print(w, String.format("            throw new UnableToDeserializeException(\"The auto-generated AutoBean" +
                " deserializer failed to deserialize the response body to \" + ctx.getRequestedType().getName() +" +
                " \".\", e);"));
        print(w, String.format("        }"));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // deserialize
        print(w, String.format("    @Override"));
        print(w, String.format("    public <C extends Collection<%s>> C deserialize(Class<C> c, " +
                "String s, DeserializationContext ctx) {", qualifiedSourceName));
        print(w, String.format("        try {"));
        print(w, String.format("            if (c == List.class || c == Collection.class)"));
        print(w, String.format("                return (C) AutoBeanCodex.decode(%s, %s.class, " +
                "\"{\\\"result\\\":\" + s + \"}\").as().getResult();", factoryFieldName, listWrapperTypeName));
        print(w, String.format("            else if (c == Set.class)"));
        print(w, String.format("                return (C) AutoBeanCodex.decode(%s, %s.class, " +
                "\"{\\\"result\\\":\" + s + \"}\").as().getResult();", factoryFieldName, setWrapperTypeName));
        print(w, String.format("            else"));
        // TODO: improve this by not requiring parsing the json to an js array and latter stringifying it
        // An alternative would be manually traverse the json array and passing each json object to serialize method
        print(w, String.format("                return super.deserialize(c, s, ctx);"));
        print(w, String.format("        } catch (java.lang.Exception e) {"));
        print(w, String.format("            throw new UnableToDeserializeException(\"The auto-generated AutoBean" +
                " deserializer failed to deserialize the response body  to \" + c.getName() + \"<\" +" +
                " ctx.getRequestedType().getName() + \">.\", e);"));
        print(w, String.format("        }"));
        print(w, String.format("    }"));

        // serialize
        print(w, String.format("    @Override"));
        print(w, String.format("    public String serialize(%s o, SerializationContext ctx) {", qualifiedSourceName));
        print(w, String.format("        try {"));
        print(w, String.format("            return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(o)).getPayload();"));
        print(w, String.format("        } catch (java.lang.Exception e) {"));
        print(w, String.format("            throw new UnableToSerializeException(\"The auto-generated AutoBean" +
                " serializer failed to serialize the instance of \" + o.getClass().getName() + \" to JSON.\", e);"));
        print(w, String.format("        }"));
        print(w, String.format("    }"));
        print(w, String.format(""));

        // serialize
        print(w, String.format("    @Override"));
        print(w, String.format("    public String serialize(Collection<%s> c, SerializationContext ctx) {",
                qualifiedSourceName));
        print(w, String.format("        try {"));
        print(w, String.format("            if (c instanceof List) {"));
        print(w, String.format("                final AutoBean<%s> autoBean = %s();", listWrapperTypeName,
                listWrapperFactoryMethodName));
        print(w, String.format("                autoBean.as().setResult((List) c);"));
        print(w, String.format("                final String json = AutoBeanCodex.encode(autoBean).getPayload();"));
        print(w, String.format("                return json.substring(10, json.length() -1);"));
        print(w, String.format("            }"));
        print(w, String.format("            if (c instanceof Set) {"));
        print(w, String.format("                final AutoBean<%s> autoBean = %s();", setWrapperTypeName,
                setWrapperFactoryMethodName));
        print(w, String.format("                autoBean.as().setResult((Set) c);"));
        print(w, String.format("                final String json = AutoBeanCodex.encode(autoBean).getPayload();"));
        print(w, String.format("                return json.substring(10, json.length() -1);"));
        print(w, String.format("            }"));
        print(w, String.format("            return super.serialize(c, ctx);"));
        print(w, String.format("        } catch (java.lang.Exception e) {"));
        print(w, String.format("            throw new UnableToSerializeException(\"The auto-generated AutoBean" +
                " serializer failed to serialize the instance of \" + c.getClass().getName() + \" to JSON.\", e);"));
        print(w, String.format("        }"));
        print(w, String.format("    }"));

        // end anonymous class
        print(w, String.format("};"));
        print(w, String.format(""));

        // serializer field as anonymous class
        print(w, String.format("private final %s %s = new %s();", serializerTypeName, serializerFieldName,
                serializerTypeName));
        print(w, String.format(""));

        return serializerFieldName;
    }

    private String generateSetWrapperInterface(SourceWriter w, JClassType type) {
        String wrapperTypeName = getSetWrapperTypeName(type);

        print(w, String.format("interface %s {", wrapperTypeName));
        print(w, String.format("    Set<%s> getResult();", type.getQualifiedSourceName()));
        print(w, String.format("    void setResult(Set<%s> result);", type.getQualifiedSourceName()));
        print(w, String.format("}"));

        return wrapperTypeName;
    }

    private String getFieldName(JType type) {
        return replaceDotByUpperCase(type.getQualifiedSourceName());
    }

    private String getListWrapperTypeName(JType type) {
        return getTypeName(type) + "ListWrapper";
    }

    private String getSetWrapperTypeName(JType type) {
        return getTypeName(type) + "SetWrapper";
    }

    private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, JClassType moduleType) {
        JPackage modulePkg = moduleType.getPackage();
        String packageName = modulePkg == null ? "" : modulePkg.getName();
        PrintWriter printWriter = ctx.tryCreate(logger, packageName, getModuleTypeImplName(moduleType));
        if (printWriter == null) {
            return null;
        }

        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, getModuleTypeImplName(moduleType));

        String[] imports = new String[]{
                // java.util
                ArrayList.class.getCanonicalName(),
                Collection.class.getCanonicalName(),
                List.class.getCanonicalName(),
                Iterator.class.getCanonicalName(),
                Set.class.getCanonicalName(),
                // com.google.gwt.core.client
                GWT.class.getCanonicalName(),
                // com.google.web.bindery.autobean.shared
                AutoBean.class.getCanonicalName(),
                AutoBeanCodex.class.getCanonicalName(),
                AutoBeanFactory.class.getCanonicalName(),
                AutoBeanUtils.class.getCanonicalName(),
                // io.reinert.requestor
                SerializationModule.class.getCanonicalName(),
                Serializer.class.getCanonicalName(),
                TypeProvider.class.getCanonicalName(),
                // io.reinert.requestor.serialization
                DeserializationContext.class.getCanonicalName(),
                Deserializer.class.getCanonicalName(),
                HandlesSubTypes.class.getCanonicalName(),
                Serializer.class.getCanonicalName(),
                SerializationContext.class.getCanonicalName(),
                UnableToDeserializeException.class.getName(),
                UnableToSerializeException.class.getName(),
                // io.reinert.requestor.serialization.json
                JsonObjectSerializer.class.getCanonicalName(),
                JsonRecordReader.class.getCanonicalName(),
                JsonRecordWriter.class.getCanonicalName(),
        };

        for (String imp : imports) {
            composerFactory.addImport(imp);
        }

        composerFactory.addImplementedInterface(moduleType.getErasedType().getQualifiedSourceName());

        return composerFactory.createSourceWriter(ctx, printWriter);
    }

    private String getTypeName(JType type) {
        final String fieldName = getFieldName(type);
        return firstCharToUpperCase(fieldName);
    }

    private String getModuleTypeImplName(JClassType moduleType) {
        return moduleType.getName().replace('.', '_') + "Impl";
    }

    private String replaceDotByUpperCase(String s) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c == '.') {
                result.append(Character.toUpperCase(s.charAt(++i)));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private void print(SourceWriter srcWriter, String s) {
        srcWriter.println(s);
        sourceLog.append('\n').append(s);
    }
}
