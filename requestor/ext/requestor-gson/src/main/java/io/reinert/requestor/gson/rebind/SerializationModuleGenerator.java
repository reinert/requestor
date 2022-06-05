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
package io.reinert.requestor.gson.rebind;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;
import com.squareup.javapoet.JavaFile;

import io.reinert.requestor.annotations.MediaType;
import io.reinert.requestor.core.SerializationModule;
import io.reinert.requestor.gson.annotations.GsonSerializationModule;
import io.reinert.requestor.gson.rebind.codegen.TypeInfo;
import io.reinert.requestor.gson.rebind.processing.ProcessingException;
import io.reinert.requestor.gson.rebind.processing.ProcessingLogger;

public class SerializationModuleGenerator {

    public static String[] MEDIA_TYPE_PATTERNS = new String[] { "application/json" };

    private final TypeElement moduleTypeElement;
    private final TypeInfo moduleTypeInfo;
    private final Set<SerializerAssembler> serializerAssemblers;
    private SerializationModuleAssembler serializationModuleAssembler;
    private boolean generated;

    private final ProcessingLogger logger;

    public SerializationModuleGenerator(Element element, ProcessingLogger logger) throws ProcessingException {
        this.logger = logger;
        checkElementIsInterface(element);

        moduleTypeElement = (TypeElement) element;

        checkInheritsFromSerializationModule(moduleTypeElement);

        moduleTypeInfo = new TypeInfo(moduleTypeElement.getQualifiedName().toString());

        serializerAssemblers = new LinkedHashSet<SerializerAssembler>();

        processAnnotationValues(moduleTypeElement);

        serializationModuleAssembler = new SerializationModuleAssembler(moduleTypeInfo, serializerAssemblers);
    }

    public Set<SerializerAssembler> getSerializerAssemblers() {
        return serializerAssemblers;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void generate(Map<TypeInfo, SerializerGenerator> serializerGenerators, Filer filer)
            throws ProcessingException {
        try {
            mergeAndAssembleSerializer(serializerGenerators, filer);
            serializationModuleAssembler.assemble();
            JavaFile.builder(serializationModuleAssembler.packageName(), serializationModuleAssembler.spec())
                    .build().writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProcessingException(moduleTypeElement, e,
                    "Error while writing generated code of %s: %s", moduleTypeInfo.getSimpleName(), e.getMessage());
        }
        generated = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SerializationModuleGenerator))
            return false;

        final SerializationModuleGenerator that = (SerializationModuleGenerator) o;

        return moduleTypeInfo.equals(that.moduleTypeInfo);
    }

    @Override
    public int hashCode() {
        return moduleTypeInfo.hashCode();
    }

    private void checkElementIsInterface(Element element) throws ProcessingException {
        if (element.getKind() != ElementKind.INTERFACE) {
            throw new ProcessingException(element, "Only interfaces can be annotated with @%s",
                    GsonSerializationModule.class.getSimpleName());
        }
    }

    private void checkInheritsFromSerializationModule(TypeElement element) throws ProcessingException {
        for (TypeMirror superItf : element.getInterfaces()) {
            if (SerializationModule.class.getName().equals(superItf.toString()))
                return; // is valid
        }

        throw new ProcessingException(element, "The interface %s annotated with @%s must inherit directly from %s",
                element.getQualifiedName().toString(),
                GsonSerializationModule.class.getSimpleName(),
                SerializationModule.class.getName());
    }

    private void mergeAndAssembleSerializer(Map<TypeInfo, SerializerGenerator> externalGenerators,
                                            Filer filer)
            throws ProcessingException {
        for (SerializerAssembler assembler :
                new LinkedHashSet<SerializerAssembler>(serializerAssemblers)) {
            SerializerGenerator serializerGenerator = externalGenerators.get(assembler.getTypeInfo());
            if (serializerGenerator == null) {
                serializerGenerator = new SerializerGenerator(assembler);
                externalGenerators.put(assembler.getTypeInfo(), serializerGenerator);
            } else {
                serializerAssemblers.remove(assembler);
                serializerAssemblers.add(serializerGenerator.getAssembler());
            }
            if (!serializerGenerator.isGenerated()) {
                serializerGenerator.generate(filer);
            }
        }
    }

    private void processAnnotationValues(TypeElement typeElement) throws ProcessingException {
        AnnotationMirror annMirror = MoreElements.getAnnotationMirror(typeElement, GsonSerializationModule.class).get();
        AnnotationValue annValue = AnnotationMirrors.getAnnotationValue(annMirror, "value");
        Object value = annValue.getValue();

        String[] mediaTypes = MEDIA_TYPE_PATTERNS;
        Optional<AnnotationMirror> oMediaTypeAnnMirror = MoreElements.getAnnotationMirror(typeElement, MediaType.class);
        if (oMediaTypeAnnMirror.isPresent()) {
            AnnotationMirror mediaTypeAnnMirror = oMediaTypeAnnMirror.get();
            AnnotationValue mediaTypeAnnValue = AnnotationMirrors.getAnnotationValue(mediaTypeAnnMirror, "value");
            Object mediaTypeValue = mediaTypeAnnValue.getValue();
            if (mediaTypeValue instanceof AnnotationValue) {
                mediaTypes = new String[]{ (String) ((AnnotationValue) mediaTypeValue).getValue() };
            } else if (mediaTypeValue instanceof List) {
                @SuppressWarnings("unchecked")
                List<AnnotationValue> mediaTypeValues = (List<AnnotationValue>) mediaTypeValue;
                if (mediaTypeValues.isEmpty()) {
                    throw new IllegalArgumentException(String.format(
                            "Error while generating SerializationModule for %s: @MediaType annotation cannot be empty.",
                            typeElement.getQualifiedName()
                    ));
                }
                mediaTypes = new String[mediaTypeValues.size()];
                for (int i = 0; i < mediaTypeValues.size(); i++) {
                    mediaTypes[i] = (String) mediaTypeValues.get(i).getValue();
                }
            }
        }

        if (value instanceof List) {
            try {
                @SuppressWarnings("unchecked")
                List<AnnotationValue> types = (List<AnnotationValue>) value;
                for (AnnotationValue typeValue : types) {
                    try {
                        DeclaredType declaredType = (DeclaredType) typeValue.getValue();
                        TypeElement element = (TypeElement) declaredType.asElement();
                        TypeInfo typeInfo = new TypeInfo(element.getQualifiedName().toString(), mediaTypes);
                        if (!element.getModifiers().contains(Modifier.PUBLIC))
                            throw new IllegalArgumentException(String.format(
                                    "Error while generating Serializer for %s: class must be public.",
                                    typeInfo.getQualifiedName()
                            ));
                        aggregateAssembler(typeInfo);
                    } catch (ClassCastException e) {
                        @SuppressWarnings("unchecked")
                        Class<?> type = (Class<?>) typeValue.getValue();
                        TypeInfo typeInfo = new TypeInfo(type.getCanonicalName(), mediaTypes);
                        if (!java.lang.reflect.Modifier.isPublic(type.getModifiers()))
                            throw new IllegalArgumentException(String.format(
                                    "Error while generating Serializer for %s: class must be public.",
                                    typeInfo.getQualifiedName()
                            ));
                        aggregateAssembler(typeInfo);
                    }
                }
            } catch (Exception e) {
                throw new ProcessingException(typeElement, annMirror, annValue, e,
                        "Error while processing @%s annotation values: %s",
                        GsonSerializationModule.class.getSimpleName(),
                        e.getMessage());
            }
        } else {
            throw new ProcessingException(typeElement, annMirror, annValue,
                    "The value of @%s must be an array of classes.",
                    GsonSerializationModule.class.getSimpleName());
        }
    }

    private void aggregateAssembler(TypeInfo typeInfo) {
        serializerAssemblers.add(new SerializerAssembler(typeInfo));
    }
}
