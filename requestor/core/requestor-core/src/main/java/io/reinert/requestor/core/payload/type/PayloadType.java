/*
 * Copyright 2021-2022 Danilo Reinert
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
package io.reinert.requestor.core.payload.type;

import java.util.Map;

/**
 * Represents the expected type in the response body.
 *
 * @author Danilo Reinert
 */
public interface PayloadType extends Iterable<Map.Entry<String, PayloadType>> {

    PayloadType VOID = new SinglePayloadType<Void>(Void.class);

    String ROOT_KEY = "";

    Class<?> getType();
}
