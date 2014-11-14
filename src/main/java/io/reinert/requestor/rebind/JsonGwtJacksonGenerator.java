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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.client.ObjectReader;
import com.github.nmorel.gwtjackson.client.ObjectWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import io.reinert.requestor.Json;
import io.reinert.requestor.serialization.DeserializationContext;
import io.reinert.requestor.serialization.Deserializer;
import io.reinert.requestor.serialization.Serdes;
import io.reinert.requestor.serialization.SerializationContext;
import io.reinert.requestor.serialization.Serializer;
import io.reinert.requestor.serialization.UnableToDeserializeException;
import io.reinert.requestor.serialization.UnableToSerializeException;
import io.reinert.requestor.serialization.json.JsonObjectSerdes;
import io.reinert.requestor.serialization.json.JsonRecordReader;
import io.reinert.requestor.serialization.json.JsonRecordWriter;

import org.turbogwt.core.util.Overlays;

/**
 * Generator for {@link io.reinert.requestor.Json} annotated types powered by GWT-Jackson.
 *
 * @author Danilo Reinert
 */
public class JsonGwtJacksonGenerator extends Generator {

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

        TreeLogger typeLogger = logger.branch(TreeLogger.ALL, "Generating Json SerDes powered by Gwt Jackson...", null);
        final SourceWriter sourceWriter = getSourceWriter(typeLogger, ctx, intfType);

        if (sourceWriter != null) {
            sourceWriter.println();

            final ArrayList<String> serdes = new ArrayList<String>();
            for (JClassType type : typeOracle.getTypes()) {
                Json annotation = type.getAnnotation(Json.class);
                if (annotation != null) {
                    serdes.add(generateSerdes(sourceWriter, type, annotation));
                }
            }

            generateFields(sourceWriter);
            generateConstructor(sourceWriter, serdes);
            generateMethods(sourceWriter);

            sourceWriter.commit(typeLogger);
        }

        return typeName + "Impl";
    }

    private String getTypeSimpleName() {
        return "GeneratedJsonSerdesImpl";
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

        String[] imports = new String[] {
                // java
                ArrayList.class.getCanonicalName(),
                Collection.class.getCanonicalName(),
                HashSet.class.getCanonicalName(),
                Iterator.class.getCanonicalName(),
                LinkedHashSet.class.getCanonicalName(),
                LinkedList.class.getCanonicalName(),
                List.class.getCanonicalName(),
                Set.class.getCanonicalName(),
                TreeSet.class.getCanonicalName(),
                // com.github.nmorel.gwtjackson
                ObjectMapper.class.getCanonicalName(),
                ObjectReader.class.getCanonicalName(),
                ObjectWriter.class.getCanonicalName(),
                // com.google.gwt
                GWT.class.getCanonicalName(),
                // io.reinert.requestor
                DeserializationContext.class.getCanonicalName(),
                Deserializer.class.getCanonicalName(),
                JsonObjectSerdes.class.getCanonicalName(),
                JsonRecordReader.class.getCanonicalName(),
                JsonRecordWriter.class.getCanonicalName(),
                UnableToDeserializeException.class.getName(),
                UnableToSerializeException.class.getName(),
                Serdes.class.getCanonicalName(),
                Serializer.class.getCanonicalName(),
                SerializationContext.class.getCanonicalName(),
                // org.turbogwt
                Overlays.class.getCanonicalName()
        };

        for (String imp : imports) {
            composerFactory.addImport(imp);
        }

        composerFactory.addImplementedInterface(intfType.getErasedType().getQualifiedSourceName());

        return composerFactory.createSourceWriter(ctx, printWriter);
    }

    /**
     * Create the serdes and return the field name.
     */
    private String generateSerdes(SourceWriter w, JClassType type, Json annotation) {
        final String qualifiedSourceName = type.getQualifiedSourceName();

        final String qualifiedCamelCaseFieldName = replaceDotByUpperCase(qualifiedSourceName);
        final String qualifiedCamelCaseTypeName = Character.toUpperCase(qualifiedCamelCaseFieldName.charAt(0)) +
                qualifiedCamelCaseFieldName.substring(1);

        final String singleMapperType = qualifiedCamelCaseTypeName + "Mapper";
        final String collectionWriterType = qualifiedCamelCaseTypeName + "CollectionWriter";
        final String arrayListReaderType = qualifiedCamelCaseTypeName + "ArrayListReader";
        final String linkedListReaderType = qualifiedCamelCaseTypeName + "LinkedListReader";
        final String hashSetReaderType = qualifiedCamelCaseTypeName + "HashSetReader";
        final String linkedHashSetReaderType = qualifiedCamelCaseTypeName + "LinkedHashSetReader";
        final String treeSetReaderType = qualifiedCamelCaseTypeName + "TreeSetReader";

        // interfaces extending Gwt Jackson
        w.println("interface %s extends ObjectMapper<%s> {}", singleMapperType, qualifiedSourceName);
        w.println("interface %s extends ObjectWriter<Collection<%s>> {}", collectionWriterType,
                qualifiedSourceName);
        w.println("interface %s extends ObjectReader<ArrayList<%s>> {}", arrayListReaderType,
                qualifiedSourceName);
        w.println("interface %s extends ObjectReader<LinkedList<%s>> {}", linkedListReaderType,
                qualifiedSourceName);
        w.println("interface %s extends ObjectReader<HashSet<%s>> {}", hashSetReaderType, qualifiedSourceName);
        w.println("interface %s extends ObjectReader<TreeSet<%s>> {}", treeSetReaderType, qualifiedSourceName);
        w.println("interface %s extends ObjectReader<LinkedHashSet<%s>> {}", linkedHashSetReaderType,
                qualifiedSourceName);
        w.println();

        final String singleMapperField = qualifiedCamelCaseFieldName + "Mapper";
        final String collectionWriterField = qualifiedCamelCaseFieldName + "CollectionWriter";
        final String arrayListReaderField = qualifiedCamelCaseFieldName + "ArrayListReader";
        final String linkedListReaderField = qualifiedCamelCaseFieldName + "LinkedListReader";
        final String hashSetReaderField = qualifiedCamelCaseFieldName + "HashSetReader";
        final String linkedHashSetReaderField = qualifiedCamelCaseFieldName + "LinkedHashSetReader";
        final String treeSetReaderField = qualifiedCamelCaseFieldName + "TreeSetReader";

        // fields creating interfaces
        w.println("private final %s %s = GWT.create(%s.class);", singleMapperType, singleMapperField,
                singleMapperType);
        w.println("private final %s %s = GWT.create(%s.class);", collectionWriterType, collectionWriterField,
                collectionWriterType);
        w.println("private final %s %s = GWT.create(%s.class);", arrayListReaderType, arrayListReaderField,
                arrayListReaderType);
        w.println("private final %s %s = GWT.create(%s.class);", linkedListReaderType, linkedListReaderField,
                linkedListReaderType);
        w.println("private final %s %s = GWT.create(%s.class);", hashSetReaderType, hashSetReaderField,
                hashSetReaderType);
        w.println("private final %s %s = GWT.create(%s.class);", linkedHashSetReaderType,
                linkedHashSetReaderField, linkedHashSetReaderType);
        w.println("private final %s %s = GWT.create(%s.class);", treeSetReaderType, treeSetReaderField,
                treeSetReaderType);
        w.println();

        final String serdesField = qualifiedCamelCaseFieldName + "Serdes";
        final String serdesType = "JsonObjectSerdes<" + qualifiedSourceName + ">";

        // serializer field as anonymous class
        w.println("private final %s %s = new %s(%s.class) {", serdesType, serdesField, serdesType,
                qualifiedSourceName);
        w.println();

        // static field to content-types
        w.println("    private final String[] PATTERNS = new String[]{ %s };", asStringCsv(annotation.value()));
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
        w.println("        return %s.read(Overlays.stringify(r));", singleMapperField);
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
        w.println("        try {");
        w.println("            return %s.read(s);", singleMapperField);
        w.println("        } catch (com.github.nmorel.gwtjackson.client.exception.JsonDeserializationException e) {");
        w.println("            throw new UnableToDeserializeException(\"The auto-generated gwt-jackson deserializer" +
                " failed to deserialize the response body to \" + ctx.getRequestedType().getName() + \".\", e);");
        w.println("        }");
        w.println("    }");
        w.println();

        // deserializeAsCollection
        w.println("    @Override");
        w.println("    public <C extends Collection<%s>> C deserializeAsCollection(Class<C> c, " +
                "String s, DeserializationContext ctx) {", qualifiedSourceName);
        w.println("        try {");
        w.println("            if (c == List.class || c == ArrayList.class || c == Collection.class)");
        w.println("                return (C) %s.read(s);", arrayListReaderField);
        w.println("            else if (c == LinkedList.class)");
        w.println("                return (C) %s.read(s);", linkedListReaderField);
        w.println("            else if (c == Set.class || c == HashSet.class)");
        w.println("                return (C) %s.read(s);", hashSetReaderField);
        w.println("            else if (c == TreeSet.class)");
        w.println("                return (C) %s.read(s);", treeSetReaderField);
        w.println("            else if (c == LinkedHashSet.class)");
        w.println("                return (C) %s.read(s);", linkedHashSetReaderField);
        w.println("            else");
        // TODO: improve this by not requiring parsing the json to an js array and latter stringyfying it
        // An alternative would be manually traverse the json array and passing each json object to serialize method
        w.println("                return super.deserializeAsCollection(c, s, ctx);");
        w.println("        } catch (com.github.nmorel.gwtjackson.client.exception.JsonDeserializationException e) {");
        w.println("            throw new UnableToDeserializeException(\"The auto-generated gwt-jackson deserializer" +
                " failed to deserialize the response body" +
                " to \" + c.getName() + \"<\" + ctx.getRequestedType().getName() + \">.\", e);");
        w.println("        }");
        w.println("    }");

        // serialize
        w.println("    @Override");
        w.println("    public String serialize(%s o, SerializationContext ctx) {", qualifiedSourceName);
        w.println("        try {");
        w.println("            return %s.write(o);", singleMapperField);
        w.println("        } catch (com.github.nmorel.gwtjackson.client.exception.JsonDeserializationException e) {");
        w.println("            throw new UnableToSerializeException(\"The auto-generated gwt-jackson serializer" +
                " failed to serialize the instance of \" + o.getClass().getName() + \" to JSON.\", e);");
        w.println("        }");
        w.println("    }");
        w.println();

        // serializeFromCollection
        w.println("    @Override");
        w.println("    public String serializeFromCollection(Collection<%s> c, SerializationContext ctx) {",
                qualifiedSourceName);
        w.println("        try {");
        w.println("            return %s.write(c);", collectionWriterField);
        w.println("        } catch (com.github.nmorel.gwtjackson.client.exception.JsonDeserializationException e) {");
        w.println("            throw new UnableToSerializeException(\"The auto-generated gwt-jackson serializer" +
                " failed to serialize the instance of \" + c.getClass().getName() + \" to JSON.\", e);");
        w.println("        }");
        w.println("    }");

        // end anonymous class
        w.println("};");
        w.println();

        return serdesField;
    }

    private String asStringCsv(String[] array) {
        StringBuilder result = new StringBuilder();
        for (String s : array) {
            result.append('"').append(s).append('"').append(", ");
        }
        result.replace(result.length() - 2, result.length(), "");
        return result.toString();
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

    private void generateConstructor(SourceWriter srcWriter, ArrayList<String> serdes) {
        srcWriter.println("public GeneratedJsonSerdesImpl() {");
        for (String s : serdes) {
            srcWriter.println("    serdesList.add(%s);", s);
        }
        srcWriter.println("}");
        srcWriter.println();
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
}
