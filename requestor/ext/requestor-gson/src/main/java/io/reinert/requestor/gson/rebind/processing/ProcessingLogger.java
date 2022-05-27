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
package io.reinert.requestor.gson.rebind.processing;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * A logger for annotation processors.
 *
 * @author Danilo Reinert
 */
public class ProcessingLogger {

    private final Messager messager;

    public ProcessingLogger(Messager messager) {
        this.messager = messager;
    }

    public void error(ProcessingException e) {
        if (e.getAnnotationValue() != null && e.getAnnotationMirror() != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement(), e.getAnnotationMirror(),
                    e.getAnnotationValue());
            return;
        }

        if (e.getAnnotationMirror() != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement(), e.getAnnotationMirror());
            return;
        }

        if (e.getElement() != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
            return;
        }

        messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }

    public void error(Exception e) {
        messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }

    public void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    public void note(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    public void warn(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args));
    }

    public void mandatoryWarn(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, String.format(msg, args));
    }

    public void other(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.OTHER, String.format(msg, args));
    }
}
