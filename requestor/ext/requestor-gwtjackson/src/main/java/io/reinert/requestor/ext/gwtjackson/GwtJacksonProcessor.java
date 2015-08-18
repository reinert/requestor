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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import io.reinert.requestor.JsonSerializationModule;
import io.reinert.requestor.ext.gwtjackson.codegen.TypeInfo;
import io.reinert.requestor.ext.gwtjackson.processing.ProcessingException;
import io.reinert.requestor.ext.gwtjackson.processing.ProcessingLogger;

@AutoService(Processor.class)
public class GwtJacksonProcessor extends AbstractProcessor {

    private final Map<TypeInfo, JsonObjectSerdesGenerator> serdesGenerators =
            new HashMap<TypeInfo, JsonObjectSerdesGenerator>();
    private final Set<SerializationModuleGenerator> moduleGenerators =
            new LinkedHashSet<SerializationModuleGenerator>();

    private Filer filer;
    private ProcessingLogger logger;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new LinkedHashSet<String>(Collections.singletonList(JsonSerializationModule.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        logger = new ProcessingLogger(processingEnv.getMessager());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(JsonSerializationModule.class)) {
                moduleGenerators.add(new SerializationModuleGenerator(annotatedElement, logger));
            }

            for (SerializationModuleGenerator generator : moduleGenerators) {
                if (!generator.isGenerated()) {
                    generator.generate(serdesGenerators, filer);
                }
            }
        } catch (ProcessingException e) {
            logger.error(e);
        }

        return true;
    }
}
