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
package io.reinert.requestor.core;

/**
 * A utility class that provides reflection methods not compatible with GWT.
 *
 * @author Danilo Reinert
 */
class Reflection {

    public static boolean isInnerClass(Class<?> cls) {
        return cls.getEnclosingClass() != null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newImpl(Class<? extends T> cls) {
        String name = cls.getCanonicalName();

        if (Reflection.isInnerClass(cls)) {
            int i = name.lastIndexOf('.');
            name = name.substring(0, i) + '_' + name.substring(i + 1);
        }

        try {
            return (T) Class.forName(name + "Impl").newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not instantiate the implementation for " + cls.getSimpleName(), e);
        }
    }
}
