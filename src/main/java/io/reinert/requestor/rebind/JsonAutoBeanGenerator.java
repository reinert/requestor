/*
 * Copyright 2014 Danilo Reinert
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

import io.reinert.requestor.Json;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.HasImpl;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;

import org.turbogwt.core.util.Overlays;

/**
 * Generator for {@link io.reinert.requestor.Json} annotated types powered by GWT AutoBean Framework.
 *
 * @author Danilo Reinert
 */
public class JsonAutoBeanGenerator extends Generator {

    private static String factoryFieldName = "myFactory";
    private static String factoryTypeName = "MyFactory";

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx, String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = ctx.getTypeOracle();
        assert typeOracle != null;

        JClassType intfType = typeOracle.findType(typeName);
        if (intfType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
                    + typeName + "'", null);
            throw new UnableToCompleteException();
        }

        if (intfType.isInterface() == null) {
            logger.log(TreeLogger.ERROR, intfType.getQualifiedSourceName()
                    + " is not an interface", null);
            throw new UnableToCompleteException();
        }

        TreeLogger typeLogger = logger.branch(TreeLogger.ALL, "Generating Json SerDes powered by AutoBeans...", null);
        final SourceWriter sourceWriter = getSourceWriter(typeLogger, ctx, intfType);

        if (sourceWriter != null) {
            sourceWriter.println();

            ArrayDeque<JClassType> annotatedTypes = new ArrayDeque<JClassType>();
            ArrayList<Json> jsonAnnotations = new ArrayList<Json>();

            final ArrayDeque<String> allTypesAndWrappers = new ArrayDeque<String>();

            for (JClassType type : typeOracle.getTypes()) {
                Json annotation = type.getAnnotation(Json.class);
                if (annotation != null && type.isInterface() != null) {
                    annotatedTypes.add(type);
                    jsonAnnotations.add(annotation);

                    final String listWrapperTypeName = generateListWrapperInterface(sourceWriter, type);
                    sourceWriter.println();

                    final String setWrapperTypeName = generateSetWrapperInterface(sourceWriter, type);
                    sourceWriter.println();

                    // Add current type name for single de/serialization
                    allTypesAndWrappers.add(type.getQualifiedSourceName());
                    allTypesAndWrappers.add(listWrapperTypeName);
                    allTypesAndWrappers.add(setWrapperTypeName);
                }
            }

            final ArrayDeque<String> serdesFields = new ArrayDeque<String>();
            final ArrayDeque<String> providerFields = new ArrayDeque<String>();

            if (!allTypesAndWrappers.isEmpty()) {
                generateFactoryInterface(sourceWriter, allTypesAndWrappers);
                sourceWriter.println();

                generateFactoryField(sourceWriter);
                sourceWriter.println();

                int i = 0;
                for (JClassType annotatedType : annotatedTypes) {
                    final String providerFieldName = generateProviderField(sourceWriter, annotatedType);
                    providerFields.add(providerFieldName);

                    final String serdesFieldName = generateSerdesClassAndField(logger, typeOracle, sourceWriter,
                            annotatedType, jsonAnnotations.get(i++));
                    serdesFields.add(serdesFieldName);
                }
            }

            generateFields(sourceWriter);
            generateConstructor(sourceWriter, serdesFields, providerFields);
            generateMethods(sourceWriter);

            sourceWriter.commit(typeLogger);
        }

        return typeName + "Impl";
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

    private void generateConstructor(SourceWriter srcWriter, Iterable<String> serdes, Iterable<String> providers) {
        srcWriter.println("public GeneratedJsonSerdesImpl() {");
        for (String s : serdes) {
            srcWriter.println("    serdesList.add(%s);", s);
        }
        for (String s : providers) {
            srcWriter.println("    providersList.add(%s);", s);
        }
        srcWriter.println("}");
        srcWriter.println();
    }

    private void generateFactoryField(SourceWriter w) {
        w.println("private static %s %s = GWT.create(%s.class);", factoryTypeName, factoryFieldName, factoryTypeName);
    }

    private void generateFactoryInterface(SourceWriter w, Iterable<String> typeNames) {
        w.println("interface %s extends AutoBeanFactory {", factoryTypeName);
        for (String typeName : typeNames) {
            w.println("    AutoBean<%s> %s();", typeName, replaceDotByUpperCase(firstCharToLowerCase(typeName)));
        }
        w.println("}");
    }

    private void generateFields(SourceWriter srcWriter) {
        // Initialize a field with binary name of the remote service interface
        srcWriter.println("private final ArrayList<Serdes<?>> serdesList = new ArrayList<Serdes<?>>();");
        srcWriter.println("private final ArrayList<GeneratedProvider<?>> providersList = " +
                "new ArrayList<GeneratedProvider<?>>();");
        srcWriter.println();
    }

    private void generateMethods(SourceWriter srcWriter) {
        srcWriter.println("@Override");
        srcWriter.println("public List<Serdes<?>> getGeneratedSerdes() {");
        srcWriter.println("    return serdesList;");
        srcWriter.println("}");
        srcWriter.println();
        srcWriter.println("@Override");
        srcWriter.println("public List<GeneratedProvider<?>> getGeneratedProviders() {");
        srcWriter.println("    return providersList;");
        srcWriter.println("}");
        srcWriter.println();
    }

    private String generateListWrapperInterface(SourceWriter w, JClassType type) {
        String wrapperTypeName = getListWrapperTypeName(type);

        w.println("interface %s {", wrapperTypeName);
        w.println("    List<%s> getResult();", type.getQualifiedSourceName());
        w.println("    void setResult(List<%s> result);", type.getQualifiedSourceName());
        w.println("}");

        return wrapperTypeName;
    }

    private String generateProviderField(SourceWriter w, JClassType type) {
        final String fieldName = getFieldName(type);
        final String autoBeanFactoryMethodName = factoryFieldName + "." + fieldName;
        final String providerFieldName = fieldName + "Provider";

        w.println("private final GeneratedProvider %s = new GeneratedProvider<%s>() {", providerFieldName,
                type.getQualifiedSourceName());
        w.println("    @Override");
        w.println("    public Class<%s> getType() {", type.getQualifiedSourceName());
        w.println("        return %s.class;", type.getQualifiedSourceName());
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public %s get() {", type.getQualifiedSourceName());
        w.println("        return %s().as();", autoBeanFactoryMethodName);
        w.println("    }");
        w.println("};");
        w.println();

        return providerFieldName;
    }

    /**
     * Create the serdes and return the field name.
     */
    private String generateSerdesClassAndField(TreeLogger logger, TypeOracle oracle, SourceWriter w,
                                               JClassType type, Json annotation) {
        final String qualifiedSourceName = type.getQualifiedSourceName();
        final String fieldName = getFieldName(type);
        final String listWrapperTypeName = getListWrapperTypeName(type);
        final String setWrapperTypeName = getSetWrapperTypeName(type);
        final String listWrapperFactoryMethodName = factoryFieldName + "." + firstCharToLowerCase(listWrapperTypeName);
        final String setWrapperFactoryMethodName = factoryFieldName + "." + firstCharToLowerCase(setWrapperTypeName);

        final String serdesFieldName = fieldName + "Serdes";
        final String serdesTypeName = getTypeName(type) + "Serdes";

        // serializer field as anonymous class
        w.println("private static class %s extends JsonObjectSerdes<%s> implements HasImpl {", serdesTypeName,
                qualifiedSourceName);

        // static field for impl array
        final String autoBeanInstanceClass = factoryFieldName + "." + fieldName + "().getClass()";
        w.println("    private final Class[] IMPL = new Class[]{ %s };", autoBeanInstanceClass);

        // static field to content-types
        w.println("    private final String[] PATTERNS = new String[]{ %s };", asStringCsv(annotation.value()));
        w.println();

        // constructor
        w.println("    public %s() {", serdesTypeName);
        w.println("        super(%s.class);", qualifiedSourceName);
        w.println("    }");
        w.println();

        // contentType
        w.println("    @Override");
        w.println("    public Class[] implTypes() {");
        w.println("        return IMPL;");
        w.println("    }");
        w.println();

        // contentType
        w.println("    @Override");
        w.println("    public String[] contentType() {");
        w.println("        return PATTERNS;");
        w.println("    }");
        w.println();

        // readJson - used when any of deserializeAsCollection alternatives succeeded (see JsonObjectSerdes)
        // TODO: improve this by not requiring parsing the json to an js array and latter stringyfying it (see below)
        // Here would be no-op
        w.println("    @Override");
        w.println("    public %s readJson(JsonRecordReader r, DeserializationContext ctx) {",
                qualifiedSourceName);
        w.println("        return AutoBeanCodex.decode(%s, %s.class, Overlays.stringify(r)).as();",
                factoryFieldName, qualifiedSourceName);
        w.println("    }");
        w.println();

        // writeJson - not used
        w.println("    @Override");
        w.println("    public void writeJson(%s o, JsonRecordWriter w, SerializationContext ctx) {",
                qualifiedSourceName);
        w.println("        return;");
        w.println("    }");
        w.println();

        // deserialize - deserialize single object using ObjectMapper
        w.println("    @Override");
        w.println("    public %s deserialize(String s, DeserializationContext ctx) {", qualifiedSourceName);
        w.println("        return AutoBeanCodex.decode(%s, %s.class, s).as();", factoryFieldName, qualifiedSourceName);
        w.println("    }");
        w.println();

        // deserializeAsCollection
        w.println("    @Override");
        w.println("    public <C extends Collection<%s>> C deserializeAsCollection(Class<C> c, " +
                "String s, DeserializationContext ctx) {", qualifiedSourceName);
        w.println("        if (c == List.class || c == Collection.class)");
        w.println("            return (C) AutoBeanCodex.decode(%s, %s.class, " +
                "\"{\\\"result\\\":\" + s + \"}\").as().getResult();", factoryFieldName, listWrapperTypeName);
        w.println("        else if (c == Set.class)");
        w.println("            return (C) AutoBeanCodex.decode(%s, %s.class, " +
                "\"{\\\"result\\\":\" + s + \"}\").as().getResult();", factoryFieldName, setWrapperTypeName);
        w.println("        else");
        // TODO: improve this by not requiring parsing the json to an js array and latter stringyfying it
        // An alternative would be manually traverse the json array and passing each json object to serialize method
        w.println("            return super.deserializeAsCollection(c, s, ctx);");
        w.println("    }");

        // serialize
        w.println("    @Override");
        w.println("    public String serialize(%s o, SerializationContext ctx) {", qualifiedSourceName);
        w.println("        return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(o)).getPayload();");
        w.println("    }");
        w.println();

        // serializeFromCollection
        w.println("    @Override");
        w.println("    public String serializeFromCollection(Collection<%s> c, SerializationContext ctx) {",
                qualifiedSourceName);
        w.println("        if (c instanceof List) {");
        w.println("            final AutoBean<%s> autoBean = %s();", listWrapperTypeName, listWrapperFactoryMethodName);
        w.println("            autoBean.as().setResult((List) c);");
        w.println("            final String json = AutoBeanCodex.encode(autoBean).getPayload();");
        w.println("            return json.substring(10, json.length() -1);");
        w.println("        }");
        w.println("        if (c instanceof Set) {");
        w.println("            final AutoBean<%s> autoBean = %s();", setWrapperTypeName, setWrapperFactoryMethodName);
        w.println("            autoBean.as().setResult((Set) c);");
        w.println("            final String json = AutoBeanCodex.encode(autoBean).getPayload();");
        w.println("            return json.substring(10, json.length() -1);");
        w.println("        }");
        w.println("        return super.serializeFromCollection(c, ctx);");
        w.println("    }");

        // end anonymous class
        w.println("};");
        w.println();

        // serializer field as anonymous class
        w.println("private final %s %s = new %s();", serdesTypeName, serdesFieldName, serdesTypeName);
        w.println();

        return serdesFieldName;
    }

    private String generateSetWrapperInterface(SourceWriter w, JClassType type) {
        String wrapperTypeName = getSetWrapperTypeName(type);

        w.println("interface %s {", wrapperTypeName);
        w.println("    Set<%s> getResult();", type.getQualifiedSourceName());
        w.println("    void setResult(Set<%s> result);", type.getQualifiedSourceName());
        w.println("}");

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
                HasImpl.class.getCanonicalName(),
                Serdes.class.getCanonicalName(),
                Serializer.class.getCanonicalName(),
                SerializationContext.class.getCanonicalName(),
                // io.reinert.requestor.serialization.json
                JsonObjectSerdes.class.getCanonicalName(),
                JsonRecordReader.class.getCanonicalName(),
                JsonRecordWriter.class.getCanonicalName(),
                // org.turbogwt.core.util
                Overlays.class.getCanonicalName()
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
        return "GeneratedJsonSerdesImpl";
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
}
