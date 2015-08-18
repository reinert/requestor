package io.reinert.requestor.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import io.reinert.requestor.SerializationModule;

/**
 * Generator that returns the implementation type name of a interface extending {@link SerializationModule}.
 *
 * The implementation code generation must be handled externally, by annotation processing.
 *
 * @author Danilo Reinert
 */
public class SerializationModuleGenerator extends Generator {
    @Override
    public String generate(TreeLogger treeLogger, GeneratorContext generatorContext, String typeName)
            throws UnableToCompleteException {
        final String[] parts = typeName.split("\\.");
        String qualifiedName = "";
        String separator = "";
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            if (Character.isLowerCase(part.charAt(0))) {
                qualifiedName = qualifiedName + separator + part;
                separator = ".";
            } else {
                if (i == parts.length - 1) qualifiedName = qualifiedName + separator + part;
            }
        }
        return qualifiedName + "Impl";
    }
}
