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
package io.reinert.requestor.gwtjackson.rebind.processing;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

/**
 * An exception to be thrown as validation errors when processing annotations.
 *
 * @author Danilo Reinert
 */
public class ProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Element element;
    private final AnnotationMirror annotationMirror;
    private final AnnotationValue annotationValue;

    public ProcessingException(Element element, AnnotationMirror annotationMirror, AnnotationValue annotationValue,
                               Throwable e, String msg, Object... args) {
        super(String.format(msg, args), e);
        this.element = element;
        this.annotationMirror = annotationMirror;
        this.annotationValue = annotationValue;
    }

    public ProcessingException(Element element, AnnotationMirror annotationMirror, AnnotationValue annotationValue,
                               String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
        this.annotationMirror = annotationMirror;
        this.annotationValue = annotationValue;
    }

    public ProcessingException(Element element, AnnotationMirror annotationMirror, String msg, Object... args) {
        this(element, annotationMirror, null, msg, args);
    }

    public ProcessingException(Element element, String msg, Object... args) {
        this(element, null, null, msg, args);
    }

    public ProcessingException(Element element, Throwable e, String msg, Object... args) {
        this(element, null, null, e, msg, args);
    }

    public Element getElement() {
        return element;
    }

    public AnnotationMirror getAnnotationMirror() {
        return annotationMirror;
    }

    public AnnotationValue getAnnotationValue() {
        return annotationValue;
    }
}
