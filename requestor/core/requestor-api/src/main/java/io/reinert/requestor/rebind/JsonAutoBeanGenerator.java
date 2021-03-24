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
package io.reinert.requestor.rebind;

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

import io.reinert.requestor.JsonSerializationModule;
import io.reinert.requestor.MediaType;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.HandlesSubTypes;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.UnableToDeserializeException;
import io.reinert.requestor.serialization.UnableToSerializeException;
import io.reinert.requestor.serialization.json.JsonObjectSerializer;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;

/**
 * Generator for {@link io.reinert.requestor.Json} annotated types powered by GWT AutoBean Framework.
 *
 * @author Danilo Reinert
 */
public class JsonAutoBeanGenerator extends Generator {

    public static String REBIND_INTERFACE = "GeneratedSerializers";
    public static String[] MEDIA_TYPE_PATTERNS = new String[] { "application/json" };

    private static final Logger LOGGER = Logger.getLogger(JsonAutoBeanGenerator.class.getName());

    private static String factoryFieldName = "myFactory";
    private static String factoryTypeName = "MyFactory";

    private final StringBuilder sourceLog = new StringBuilder();

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx, String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = ctx.getTypeOracle();
        assert typeOracle != null;

        JClassType intfType = typeOracle.findType(typeName);
        if (intfType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }

        if (intfType.isInterface() == null) {
            logger.log(TreeLogger.ERROR, intfType.getQualifiedSourceName() + " is not an interface", null);
            throw new UnableToCompleteException();
        }

        // TODO: check if type was already generated and reuse it
        TreeLogger typeLogger = logger.branch(TreeLogger.INFO, "Generating Json Serializer powered by AutoBeans...",
                null);
        final SourceWriter sourceWriter = getSourceWriter(typeLogger, ctx, intfType);

        if (sourceWriter != null) {
            sourceWriter.println();

            for (JClassType type : typeOracle.getTypes()) {
                JsonSerializationModule serializationModuleAnn = type.getAnnotation(JsonSerializationModule.class);
                if (serializationModuleAnn != null && serializationModuleAnn.value().length > 0) {
                    final ArrayDeque<String> allTypesAndWrappers = new ArrayDeque<String>();
                    final ArrayDeque<JClassType> autoBeanTypes = getJTypes(typeOracle, serializationModuleAnn);
                    final String[] mediaTypes = getMediaTypePatterns(type.getAnnotation(MediaType.class));

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
                    final ArrayDeque<String> providerFields = new ArrayDeque<String>();

                    if (!allTypesAndWrappers.isEmpty()) {
                        generateFactoryInterface(sourceWriter, allTypesAndWrappers);
                        sourceWriter.println();

                        generateFactoryField(sourceWriter);
                        sourceWriter.println();

                        for (JClassType autoBeanType : autoBeanTypes) {
                            final String providerFieldName = generateProviderField(sourceWriter, autoBeanType);
                            providerFields.add(providerFieldName);

                            final String serializerFieldName = generateSerializerClassAndField(logger, typeOracle,
                                    sourceWriter, autoBeanType, mediaTypes);
                            serializerFields.add(serializerFieldName);
                        }
                    }

                    generateFields(sourceWriter);
                    generateConstructor(sourceWriter, serializerFields, providerFields);
                    generateMethods(sourceWriter);

                    // TODO: uncomment the line below to log the generated source code
//                    LOGGER.info(sourceLog.toString());

                    sourceWriter.commit(typeLogger);

                    // Early break for loop after finding the annotated SerializationModule (limits for only one module)
                    break;
                }
            }
        }

        return typeName + "Impl";
    }

    private String[] getMediaTypePatterns(MediaType mediaTypeAnn) {
        String[] mediaTypes = MEDIA_TYPE_PATTERNS;
        if (mediaTypeAnn != null && mediaTypeAnn.value().length > 0) {
            mediaTypes = mediaTypeAnn.value();
        }
        return mediaTypes;
    }

    private ArrayDeque<JClassType> getJTypes(TypeOracle oracle, JsonSerializationModule serializationModuleAnn) {
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

    private void generateConstructor(SourceWriter w, Iterable<String> serializer, Iterable<String> providers) {
        print(w, String.format("public %sImpl() {", REBIND_INTERFACE));
        for (String s : serializer) {
            print(w, String.format("    serializerList.add(%s);", s));
        }
        for (String s : providers) {
            print(w, String.format("    providersList.add(%s);", s));
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
        print(w, String.format("private final ArrayList<Serializer<?>> serializerList =" +
                " new ArrayList<Serializer<?>>();"));
        print(w, String.format("private final ArrayList<Provider<?>> providersList = new ArrayList<Provider<?>>();"));
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
        print(w, String.format("public List<Provider<?>> getProviders() {"));
        print(w, String.format("    return providersList;"));
        print(w, String.format("}"));
        print(w, String.format(""));
    }

    private String generateProviderField(SourceWriter w, JClassType type) {
        final String fieldName = getFieldName(type);
        final String autoBeanFactoryMethodName = factoryFieldName + "." + fieldName;
        final String providerFieldName = fieldName + "Provider";

        print(w, String.format("private final Provider %s = new Provider<%s>() {", providerFieldName,
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

        return providerFieldName;
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

    private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, JClassType intfType) {
        JPackage serviceIntfPkg = intfType.getPackage();
        String packageName = serviceIntfPkg == null ? "" : serviceIntfPkg.getName();
        PrintWriter printWriter = ctx.tryCreate(logger, packageName, getTypeSimpleName());
        if (printWriter == null) {
            return null;
        }

        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, getTypeSimpleName());

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

        composerFactory.addImplementedInterface(intfType.getErasedType().getQualifiedSourceName());

        return composerFactory.createSourceWriter(ctx, printWriter);
    }

    private String getTypeName(JType type) {
        final String fieldName = getFieldName(type);
        return firstCharToUpperCase(fieldName);
    }

    private String getTypeSimpleName() {
        return REBIND_INTERFACE + "Impl";
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
