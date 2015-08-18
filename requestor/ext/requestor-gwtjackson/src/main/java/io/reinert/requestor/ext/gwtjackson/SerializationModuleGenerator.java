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
package io.reinert.requestor.ext.gwtjackson;

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
import com.squareup.javapoet.JavaFile;

import io.reinert.requestor.JsonSerializationModule;
import io.reinert.requestor.SerializationModule;
import io.reinert.requestor.ext.gwtjackson.codegen.TypeInfo;
import io.reinert.requestor.ext.gwtjackson.processing.ProcessingException;
import io.reinert.requestor.ext.gwtjackson.processing.ProcessingLogger;

public class SerializationModuleGenerator {

    private final TypeElement moduleTypeElement;
    private final TypeInfo moduleTypeInfo;
    private final Set<JsonObjectSerdesAssembler> serdesAssemblers;
    private SerializationModuleAssembler serializationModuleAssembler;
    private boolean generated;

    private final ProcessingLogger logger;

    public SerializationModuleGenerator(Element element, ProcessingLogger logger) throws ProcessingException {
        this.logger = logger;
        checkElementIsInterface(element);

        moduleTypeElement = (TypeElement) element;

        checkInheritsFromSerializationModule(moduleTypeElement);

        moduleTypeInfo = new TypeInfo(moduleTypeElement.getQualifiedName().toString());

        serdesAssemblers = new LinkedHashSet<JsonObjectSerdesAssembler>();

        processAnnotationValues(moduleTypeElement);

        serializationModuleAssembler = new SerializationModuleAssembler(moduleTypeInfo, serdesAssemblers);
    }

    public Set<JsonObjectSerdesAssembler> getSerdesAssemblers() {
        return serdesAssemblers;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void generate(Map<TypeInfo, JsonObjectSerdesGenerator> serdesGenerators, Filer filer) throws ProcessingException {
        try {
            mergeAndAssembleSerdes(serdesGenerators, filer);
            serializationModuleAssembler.assemble();
            JavaFile.builder(serializationModuleAssembler.packageName(), serializationModuleAssembler.spec())
                    .build().writeTo(filer);
        } catch (Exception e) {
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
                    JsonSerializationModule.class.getSimpleName());
        }
    }

    private void checkInheritsFromSerializationModule(TypeElement element) throws ProcessingException {
        for (TypeMirror superItf : element.getInterfaces()) {
            if (SerializationModule.class.getName().equals(superItf.toString()))
                return; // is valid
        }

        throw new ProcessingException(element, "The interface %s annotated with @%s must inherit directly from %s",
                element.getQualifiedName().toString(),
                JsonSerializationModule.class.getSimpleName(),
                SerializationModule.class.getName());
    }

    private void mergeAndAssembleSerdes(Map<TypeInfo, JsonObjectSerdesGenerator> externalGenerators, Filer filer)
            throws ProcessingException {
        for (JsonObjectSerdesAssembler assembler : new LinkedHashSet<JsonObjectSerdesAssembler>(serdesAssemblers)) {
            JsonObjectSerdesGenerator serdesGenerator = externalGenerators.get(assembler.getTypeInfo());
            if (serdesGenerator == null) {
                serdesGenerator = new JsonObjectSerdesGenerator(assembler);
                externalGenerators.put(assembler.getTypeInfo(), serdesGenerator);
            } else {
                serdesAssemblers.remove(assembler);
                serdesAssemblers.add(serdesGenerator.getAssembler());
            }
            if (!serdesGenerator.isGenerated()) {
                serdesGenerator.generate(filer);
            }
        }
    }

    private void processAnnotationValues(TypeElement typeElement) throws ProcessingException {
        AnnotationMirror annMirror = MoreElements.getAnnotationMirror(typeElement, JsonSerializationModule.class).get();
        AnnotationValue annValue = AnnotationMirrors.getAnnotationValue(annMirror, "value");
        Object value = annValue.getValue();
        if (value instanceof List) {
            try {
                @SuppressWarnings("unchecked")
                List<AnnotationValue> types = (List<AnnotationValue>) value;
                for (AnnotationValue typeValue : types) {
                    try {
                        DeclaredType declaredType = (DeclaredType) typeValue.getValue();
                        TypeElement element = (TypeElement) declaredType.asElement();
                        TypeInfo typeInfo = new TypeInfo(element.getQualifiedName().toString());
                        if (!element.getModifiers().contains(Modifier.PUBLIC))
                            throw new IllegalArgumentException(String.format("Error while generating Serdes for %s: "
                                    + "class must be public.", typeInfo.getQualifiedName()));
                        aggregateAssembler(typeInfo);
                    } catch (ClassCastException e) {
                        @SuppressWarnings("unchecked")
                        Class<?> type = (Class<?>) typeValue.getValue();
                        TypeInfo typeInfo = new TypeInfo(type.getCanonicalName());
                        if (!java.lang.reflect.Modifier.isPublic(type.getModifiers()))
                            throw new IllegalArgumentException(String.format("Error while generating Serdes for %s: "
                                    + "class must be public.", typeInfo.getQualifiedName()));
                        aggregateAssembler(typeInfo);
                    }
                }
            } catch (Exception e) {
                throw new ProcessingException(typeElement, annMirror, annValue, e,
                        "Error while processing @%s annotation values: %s",
                        JsonSerializationModule.class.getSimpleName(),
                        e.getMessage());
            }
        } else {
            throw new ProcessingException(typeElement, annMirror, annValue,
                    "The value of @%s must be an array of classes.",
                    JsonSerializationModule.class.getSimpleName());
        }
    }

    private void aggregateAssembler(TypeInfo typeInfo) {
        serdesAssemblers.add(new JsonObjectSerdesAssembler(typeInfo));
    }
}
