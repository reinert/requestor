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

import java.util.List;

import io.reinert.requestor.serialization.Serdes;

/**
 * Module that holds auto-generated Serdes.
 * <p>
 *
 * To declare a serialization module you must inherit this class and annotate it with a supported annotation that
 * enables you to specify which classes should have auto-generated serdes.
 *
 * @author Danilo Reinert
 */
public interface SerializationModule {

    List<Serdes<?>> getSerdes();

    List<Provider<?>> getProviders();

}
