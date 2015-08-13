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
package io.reinert.requestor;

/**
 * Promise marker interface.
 * <p>
 *
 * NOTE TO IMPLEMENTATIONS: Implementers should declare their own {@link Promise} interface (based on the package
 * io.reinert.requestor) exposing their Promise API, replacing this anemic interface. This exact type is removed from
 * classpath when Requestor assembles its jar.
 * <p>
 *
 * NOTE TO EXTENSIONS: This interface is removed from classpath when packaging the api in order to implementers replace
 * by their own interface. So, if you must reference it in some extension project, you should declare this type in your
 * project and remember to exclude it fromm packaging too. By replicating this interface in your project, java compiler
 * will not claim the absense of this type in the classpath, and by removing it from packaging you will prevent
 * conflicts with implementers that ship their own Promise interface in their package.
 *
 * @param <F> The type of the fulfilled (successful) result
 *
 * @author Danilo Reinert
 */
public interface Promise<F> {
}
