/*
 * Copyright 2014-2021 Danilo Reinert
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
 * Base class for {@link RequestSerializer}.
 *
 * It serializes the request using the {@link SerializationEngine} and proceeds the request processing.
 *
 * @author Danilo Reinert
 */
public class BaseRequestSerializer implements RequestSerializer {
    public void serialize(SerializableRequestInProcess request, SerializationEngine serializationEngine) {
        serializationEngine.serializeRequest(request);
        request.proceed();
    }
}
